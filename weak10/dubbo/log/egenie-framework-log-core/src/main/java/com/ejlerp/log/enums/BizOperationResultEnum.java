package com.ejlerp.log.enums;

/**
 * @date 2021-05-18
 * @author paul
 */
public enum BizOperationResultEnum {
    /**
     * 操作成功
     */
    SUCCESSFUL("successful","操作成功"),
    /**
     * 操作失败
     */
    FAILED("failed","操作失败"),
    ;

    private String code;

    private String text;

    BizOperationResultEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
