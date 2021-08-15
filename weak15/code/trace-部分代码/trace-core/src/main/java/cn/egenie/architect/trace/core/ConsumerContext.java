package cn.egenie.architect.trace.core;

import java.io.Serializable;

/**
 * 传输给服务提供者的上下文
 *
 * @author lucien
 * @since 2021/08/06 2021/01/07
 */
public class ConsumerContext implements Serializable {
    private String traceId;

    /**
     * 提供者span相当于消费者的childSpan
     * consumerChildId = consumerSpanId + "." + consumerChildIndex
     * consumerSpan相当于parentSpan
     */
    private String consumerChildId;

    private String clientAppKey;
    private String clientIp;

    /**
     * 灰度接口版本信息
     */
    private int apiVersion;

    public static ConsumerContext of(Span span) {
        ConsumerContext consumerContext = new ConsumerContext();
        consumerContext.setTraceId(span.getTraceId());
        consumerContext.setConsumerChildId(span.nextChildId());

        consumerContext.setClientAppKey(span.getAppKey());
        consumerContext.setClientIp(span.getIp());

        consumerContext.setApiVersion(span.getApiVersion());

        return consumerContext;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getConsumerChildId() {
        return consumerChildId;
    }

    public void setConsumerChildId(String consumerChildId) {
        this.consumerChildId = consumerChildId;
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

    public int getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }
}
