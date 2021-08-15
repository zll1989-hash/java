package cn.egenie.architect.trace.common.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import cn.egenie.architect.common.core.util.JsonUtils;
import cn.egenie.architect.trace.common.enums.SpanStatus;
import cn.egenie.architect.trace.core.Span;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lucien
 * @since 2021/08/06 2021/02/04
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IndexSpan {
    private String traceId;
    private String name;
    private String id;
    private int depth;

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

    private long start;
    private long end;
    private long cost;

    private String serviceType;

    private String errorMessage;
    private String status;

    private Map<String, String> tagMap;

    public String generateIndexId() {
        return traceId + "." + id;
    }

    public static IndexSpan of(Span span) {
        // 注意这里span不能用json方式，里面有循环依赖
        IndexSpan indexSpan = new IndexSpan();
        indexSpan.setTraceId(span.getTraceId());
        indexSpan.setName(span.getName());
        indexSpan.setId(span.getId());
        indexSpan.setDepth(span.getDepth());

        indexSpan.setClientAppKey(span.getClientAppKey());
        indexSpan.setClientIp(span.getClientIp());
        indexSpan.setAppKey(span.getAppKey());
        indexSpan.setIp(span.getIp());

        indexSpan.setStart(span.getStart());
        indexSpan.setEnd(span.getEnd());
        indexSpan.setCost(span.getCost());

        indexSpan.setServiceType(span.getServiceType());

        indexSpan.setApiVersion(span.getApiVersion());
        indexSpan.setActualApiVersion(span.getActualApiVersion());

        List<String> errorMessages = span.getErrorMessages();
        if (errorMessages != null && !errorMessages.isEmpty()) {
            indexSpan.setErrorMessage(JsonUtils.toJson(span.getErrorMessages()));
            indexSpan.setStatus(SpanStatus.FAILED.message());
        }
        else {
            indexSpan.setStatus(SpanStatus.SUCCESS.message());
        }

        if (span.getTagMap() != null) {
            indexSpan.setTagMap(new HashMap<>(span.getTagMap()));
        }

        return indexSpan;
    }
}
