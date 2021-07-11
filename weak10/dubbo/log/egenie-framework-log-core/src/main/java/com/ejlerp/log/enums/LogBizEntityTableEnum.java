package com.ejlerp.log.enums;

public enum LogBizEntityTableEnum {
    WMS_ORDER("wms_order"),
    WMS_ORDER_DETAIL("wms_order_detail"),
    WMS_PICKING_ORDER("wms_picking_order"),
    WMS_PICKING_ORDER_DETAIL("wms_picking_order_detail"),
    LOG_BIZ_RECORD_DETAIL("log_biz_record"),
    CLOUD_PMS_PURCHASE_ORDER("cloud_pms_purchase_order"),
    CLOUD_PMS_PURCHASE_ORDER_DETAIL("cloud_pms_purchase_order_detail")
    ;

    LogBizEntityTableEnum(String table) {
        this.table = table;
    }

    private String table;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
