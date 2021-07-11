package com.ejlerp.log.enums;

/**
 * @author paul
 */
public enum LogBizOperationTypeEnum {
    /**
     * 发货单创建
     */
    WMS_ORDER_CREATE(10000, "创建发货单"),
    /**
     * 发货单作废
     */
    WMS_ORDER_CANCEL(10001, "作废发货单"),
    /**
     * 创建波次
     */
    WMS_ORDER_WAVE(10002, "创建波次"),
    /**
     * 移除波次
     */
    WMS_ORDER_REMOVE_WAVE(10003, "移除波次"),
    /**
     * 分拣
     */
    WMS_ORDER_REMOVE_PICK(10004, "分拣"),
    /**
     * 捡货
     */
    WMS_ORDER_SORT(10005, "捡货"),
    /**
     * 验货
     */
    WMS_ORDER_EXAMINE_GOODS(10006, "验货"),
    /**
     * 称重
     */
    WMS_ORDER_WEIGHT(10007, "称重"),
    /**
     * 发货
     */
    WMS_ORDER_SEND_GOODS(10008, "发货"),
    /**
     * 退货
     */
    WMS_ORDER_REFUND_GOODS(10009, "退货"),
    /**
     * 发货单挂起
     */
    WMS_ORDER_ON_HOOK(10010, "发货单挂起"),
    /**
     * 发货单更改快递
     */
    WMS_ORDER_CHANGE_COURIER(10011, "发货单更改快递"),
    /**
     * 发货单已打印
     */
    WMS_ORDER_PRINT_GOODS(10012, "面单打印"),

    /**
     * 发货单打印标记
     */
    WMS_ORDER_PRINT_MARK(10013, " 面单状态"),
    /**
     * 发货单获取单号
     */
    WMS_ORDER_WAYBILL_APPLY(10014, "自动获取单号"),
    /**
     * 发货单更新物流获取单号
     */
    WMS_ORDER_WAYBILL_LOGISTICS(10015, "更新快递公司获取单号"),
    /**
     * 手动获取单号
     */
    WMS_ORDER_WAYBILL_MANUAL(10016, "手动获取单号"),
    /**
     * 手动重新获取单号
     */
    WMS_ORDER_WAYBILL_AGAIN(10017, "手动重新获取单号"),
    /**
     * 平台发货获取单号
     */
    WMS_ORDER_WAYBILL_SENT(10018, "平台发货获取单号"),
    /**
     * 更新收件人信息
     */
    WMS_ORDER_WAYBILL_UPDATE(10019, "更新收件人信息"),

    /**
     * 出入库单更新备注
     */
    WMS_INOUT_STOCK_NOTE(11001, "更新备注"),
    /**
     * 出入库单删除单据
     */
    WMS_INOUT_STOCK_DELETE(11002, "删除单据"),
    /**
     * 出入库单新增单据
     */
    WMS_INOUT_STOCK_INSERT(11003, "新增单据"),
    /**
     * 出入库单更新单据
     */
    WMS_INOUT_STOCK_UPDATE(11004, "更新单据"),
    /**
     * 出入库单审核单据
     */
    WMS_INOUT_STOCK_APPROVE(11005, "审核单据"),
    /**
     * 出入库单
     */
    WMS_INOUT_STOCK_RECEIVE(11100, "收货单生成入库单"),
    /**
     * 出入库单
     */
    WMS_INOUT_STOCK_PDA(11101, "PDA生成入库单"),
    /**
     * 出入库单
     */
    WMS_INOUT_STOCK_PLAN(11102, "计划出库单生成出库单"),


    /**
     * 收货单创建
     */
    WMS_RECEIVE_ORDER_CREATE(20001, "收货单创建"),

    /**
     * 收货单同意或拒绝送货
     */
    WMS_RECEIVE_ORDER_ACCEPT_OR_REFUSE(20002, "收货单同意或拒绝送货"),

    /**
     * 收货单确认收货
     */
    WMS_RECEIVE_ORDER_CONFIRM_RECEIVE(20003, "收货单确认收货"),

    /**
     * 收货单反写pos
     */
    WMS_RECEIVE_ORDER_REVERSE_POS(20004, "收货单反写pos"),

    /**
     * 作废收货单
     */
    WMS_RECEIVE_ORDER_INVALIDATE(20005, "作废收货单"),

    /**
     * 修改备注
     */
    WMS_RECEIVE_ORDER_REMARK(20006, "修改备注"),

    /**
     * 移库单生成
     */
    SHIFT_BIN_ORDER_CREATE(30001, "移库单生成"),

    /**
     * 移库单审核
     */
    SHIFT_BIN_ORDER_APPROVE(30002, "移库单审核"),

    /**
     * 移库单更新
     */
    SHIFT_BIN_ORDER_UPDATE(30001, "移库单更新"),

    /**
     * 售后单生成
     */
    AFTER_SALE_ORDER_CREATE(40001, "售后单生成"),

    /**
     * 售后单更新
     */
    AFTER_SALE_ORDER_UPDATE(40002, "售后单更新"),

    /**
     * 售后单打印唯一码
     */
    AFTER_SALE_ORDER_PRINT_UNIQUE_CODE(40003, "售后单打印唯一码"),


    /**
     * 售后单确认退货
     */
    AFTER_SALE_ORDER_CONFIRM_RETURN(40004, "售后单确认退货"),
    /**
     * 入库单打印
     */
    ENTRY_ORDER_PRINT_CODE(40005, "入库单打印"),

    /**
     * 入库单打印唯一码
     */
    ENTRY_ORDER_PRINT_UNIQUE_CODE(40006, "入库单打印唯一码"),

    /**
     * 采购单审核
     */
    PMS_PURCHASE_ORDER_APPROVE(50001, "采购单审核"),
    /**
     * 采购单关闭
     */
    PMS_PURCHASE_ORDER_CLOSE(50002, "采购单关闭"),
    /**
     * 采购单删除
     */
    PMS_PURCHASE_ORDER_DELETE(50003, "采购单删除"),
    /**
     * 采购单明细删除
     */
    PMS_PURCHASE_ORDER_DETAIL_DELETE(50004, "采购单明细删除"),
    /**
     * 采购单改备注
     */
    PMS_PURCHASE_ORDER_MODIFY_REMARK(50005, "采购单改备注"),
    /**
     * 采购单打印唯一码
     */
    PMS_PURCHASE_ORDER_PRINT_UNIQUE_CODE(50006,"采购单打印唯一码"),
    /**
     * 盘点单创建
     */
    STOCK_TAKING_CREATE(60001,"盘点单创建"),
    /**
     * 盘点单审核
     */
    STOCK_TAKING_APPROVE(60002,"盘点单审核"),
    /**
     * 分配拣货员
     */
    PICK_TAKING_USER(70001,"分配拣货员"),
    /**
     * 重新分配拣货员
     */
    PICK_RETAKING_USER(70002,"重新分配拣货员")
    ;

    LogBizOperationTypeEnum(Integer code, String type) {
        this.code = code;
        this.type = type;
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
