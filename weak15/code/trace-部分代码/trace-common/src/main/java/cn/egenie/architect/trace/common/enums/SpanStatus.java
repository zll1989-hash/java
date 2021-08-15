package cn.egenie.architect.trace.common.enums;

import cn.egenie.architect.common.core.enums.EnumBase;

/**
 * @author lucien
 * @since 2021/08/06 2021/02/08
 */
public enum SpanStatus implements EnumBase {
    /**
     * Span 状态：0-成功，1-失败
     */
    SUCCESS(0, "success"),
    FAILED(1, "failed");

    private final int id;
    private final String message;

    SpanStatus(int id, String message) {
        this.id = id;
        this.message = message;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public String message() {
        return this.message;
    }
}
