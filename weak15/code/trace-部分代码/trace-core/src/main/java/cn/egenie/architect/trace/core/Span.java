package cn.egenie.architect.trace.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import cn.egenie.architect.container.dubbo.ApiVersionInfoContainer;
import cn.egenie.architect.container.dubbo.domain.ApiVersionInfo;
import cn.egenie.architect.trace.core.constants.TraceConstants;
import cn.egenie.architect.trace.core.enums.ServiceType;
import cn.egenie.architect.trace.core.util.NetworkUtils;
import cn.egenie.architect.trace.core.util.TraceUtils;

/**
 * 鉴于agent要使用，这里尽量使用jdk自带的方法，避免引入其他的类
 *
 * @author lucien
 * @since 2021/08/06 2021/01/06
 */
public class Span {
    /**
     * span id, 类似0， 0.1，0.2 用来表示调用层级
     */
    private String id;
    private int depth;
    private String traceId;
    private String name;
    private String serviceType;

    private String clientAppKey;
    private String clientIp;

    private String appKey;
    private String ip;

    /**
     * 灰度接口版本,默认0，表示没有灰度设置
     */
    private int apiVersion = 0;

    /**
     * 实际使用的灰度接口版本,默认0
     */
    private int actualApiVersion = 0;

    /**
     * 额外信息
     * http: httPath
     * dubbo: request参数
     * jdbc: sql
     */
    private Map<String, String> tagMap;

    /**
     * 毫秒
     */
    private long start;
    private long end;
    private int cost;

    private Span parent;
    private List<Span> children;
    private List<String> errorMessages;

    /**
     * 新开线程池的时候，copy当前的span放在子线程的threadLocal里面
     */
    private boolean isAsyncParent;

    /**
     * 是否被收集过
     */
    private volatile boolean collected;

    /**
     * 生成下一级span的id
     */
    private AtomicInteger childCounter = new AtomicInteger(0);

    public String nextChildId() {
        return id + "." + childCounter.incrementAndGet();
    }

    /**
     * 设置结束时间
     */
    public void end() {
        this.end = System.currentTimeMillis();
        this.cost = (int) (end - start);
    }

    public void addChild(Span childSpan) {
        if (children == null) {
            children = new LinkedList<>();
        }
        children.add(childSpan);
    }

    public void putTag(String key, String tag) {
        if (tagMap == null) {
            tagMap = new HashMap<>(16);
        }
        tagMap.put(key, tag);
    }

    public void putTags(Map<String, String> tagMap) {
        if (tagMap == null) {
            return;
        }

        if (this.tagMap == null) {
            this.tagMap = new HashMap<>(16);
        }
        this.tagMap.putAll(tagMap);
    }

    public String getTag(String key) {
        return tagMap == null ? null : tagMap.get(key);
    }

    public void fillErrors(Throwable exception) {
        if (errorMessages == null) {
            errorMessages = new ArrayList<>();
        }

        errorMessages.add(exception.getClass().getCanonicalName() + ": " + exception.getMessage());

        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        if (stackTraceElements != null && stackTraceElements.length > 0) {
            // 最多关心前10个
            int maxErrorNum = Math.min(stackTraceElements.length, 10);
            for (int i = 0; i < maxErrorNum; i++) {
                errorMessages.add(stackTraceElements[i].toString());
            }
        }
    }

    public void addError(String error) {
        if (errorMessages == null) {
            errorMessages = new ArrayList<>();
        }
        errorMessages.add(error);
    }

    public static Span copyAsAsyncParent(Span span, String name) {
        if (span == null) {
            return null;
        }

        Span copy = new Span();
        copy.setName(name);
        copy.setAsyncParent(true);

        copy.setId(span.nextChildId());
        copy.setServiceType(ServiceType.ASYNC.message());
        copy.setDepth(span.getId().split(TraceConstants.POINT_SPLIT).length);
        copy.setStart(System.currentTimeMillis());
        copyFromParent(copy, span);

        return copy;
    }

    /**
     * 微服务提供者场景使用
     */
    public static Span of(ConsumerContext consumerContext, ServiceType serviceType, String name, Map<String, String> tagMap) {
        Span span = new Span();
        span.setName(name);
        span.putTags(tagMap);
        span.setServiceType(serviceType.message());

        String rootSpanId = consumerContext.getConsumerChildId();
        span.setId(rootSpanId);
        span.setDepth(rootSpanId.split(TraceConstants.POINT_SPLIT).length);

        span.setTraceId(consumerContext.getTraceId());
        span.setClientAppKey(consumerContext.getClientAppKey());
        span.setClientIp(consumerContext.getClientIp());

        span.setApiVersion(consumerContext.getApiVersion());

        fillServerInfo(span);
        return span;
    }


    /**
     * 内部调用使用parentSpan可能是null，标书第一个span
     */
    public static Span of(Span parentSpan, ServiceType serviceType, String name, Map<String, String> tagMap) {
        Span span = new Span();
        span.setName(name);

        span.putTags(tagMap);

        if (parentSpan == TraceConstants.DUMMY_SPAN) {
            span.setServiceType(serviceType.message());
            span.setTraceId(TraceUtils.buildTraceId());

            span.setId(TraceConstants.ROOT_SPAN_ID);
            span.setDepth(1);

            ApiVersionInfo apiVersionInfo = ApiVersionInfoContainer.getDubboApiVersionInfo();
            if (apiVersionInfo != null) {
                span.setApiVersion(apiVersionInfo.getApiVersion());
            }

            fillServerInfo(span);
            return span;
        }
        else {
            span.setId(parentSpan.nextChildId());
            span.setDepth(span.getId().split(TraceConstants.POINT_SPLIT).length);

            span.setTraceId(parentSpan.getTraceId());
            span.setStart(System.currentTimeMillis());
            span.setServiceType(serviceType.message());

            copyFromParent(span, parentSpan);

            span.setParent(parentSpan);
            parentSpan.addChild(span);
            return span;
        }
    }

    private static void fillServerInfo(Span span) {
        span.setAppKey(TraceConfig.getAppKey());
        span.setIp(NetworkUtils.getLocalIp());
        span.setStart(System.currentTimeMillis());
    }

    private static void copyFromParent(Span span, Span parentSpan) {
        span.setTraceId(parentSpan.getTraceId());
        span.setAppKey(parentSpan.getAppKey());
        span.setIp(parentSpan.getIp());
        span.setClientIp(parentSpan.getClientIp());
        span.setClientAppKey(parentSpan.getClientAppKey());
        span.setApiVersion(parentSpan.getApiVersion());
        span.setActualApiVersion(parentSpan.getActualApiVersion());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getClientAppKey() {
        return clientAppKey;
    }

    public void setClientAppKey(String clientAppKey) {
        this.clientAppKey = clientAppKey;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    public int getActualApiVersion() {
        return actualApiVersion;
    }

    public void setActualApiVersion(int actualApiVersion) {
        this.actualApiVersion = actualApiVersion;
    }

    public Map<String, String> getTagMap() {
        return tagMap;
    }

    public void setTagMap(Map<String, String> tagMap) {
        this.tagMap = tagMap;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Span getParent() {
        return parent;
    }

    public void setParent(Span parent) {
        this.parent = parent;
    }

    public List<Span> getChildren() {
        return children;
    }

    public void setChildren(List<Span> children) {
        this.children = children;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public boolean isAsyncParent() {
        return isAsyncParent;
    }

    public void setAsyncParent(boolean asyncParent) {
        isAsyncParent = asyncParent;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public AtomicInteger getChildCounter() {
        return childCounter;
    }

    public void setChildCounter(AtomicInteger childCounter) {
        this.childCounter = childCounter;
    }
}
