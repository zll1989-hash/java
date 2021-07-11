package com.ejlerp.log.enums;

/**
 * @author paul
 * @date 2021-05-14
 */
public enum LogBizModuleTypeEnum {
    /**
     * 发货单模块
     */
    WMS_ORDER(10000, "发货单"),

    /**
     * 收货单模块
     */
    WMS_RECEIVE_ORDER(20000, "收货单"),
    /**
     * 移库单单模块
     */
    SHIFT_BIN_ORDER(30000, "移库单"),

    /**
     * 售后单模块
     */
    AFTER_SALE_ORDER(40000, "售后单"),

    WMS_INOUT_STOCK(10001, "出入库单"),

    PMS_PURCHASE_ORDER(50000,"采购单"),

    /**
     * 盘点单
     */
    STOCK_TAKE_ORDER(60000,"盘点单"),

    BO_ORDER(70000,"波次单")
    ;

    LogBizModuleTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    /**
     * 查看是否包含指定的type
     * @param type 指定type
     * @return 是否包含指定type
     */
    public static Boolean contain(String type){
        for (LogBizModuleTypeEnum moduleType : LogBizModuleTypeEnum.values()){
            if (moduleType.getType().equals(type)){
                return true;
            }
        }
        return false;
    }

    private Integer code;

    private String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
