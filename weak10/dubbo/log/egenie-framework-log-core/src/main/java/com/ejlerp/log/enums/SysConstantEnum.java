package com.ejlerp.log.enums;

/**
 * 系统常量ENUM
 * @author paul
 */
public enum SysConstantEnum {
    EMPTY_STR(100000,""),
    ZERO_INT(0,""),
    ZERO_ONE(1,""),
    DEFAULT_LIST_SIZE(10,""),
    ;

    SysConstantEnum(Integer code, String value) {
        this.value = value;
        this.code = code;
    }

    private String value;

    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
