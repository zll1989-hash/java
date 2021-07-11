package com.ejlerp.log.domain;

import com.ejlerp.common.util.AbstractObject;
import com.ejlerp.log.enums.LogBizModuleTypeEnum;
import com.ejlerp.log.enums.LogBizOperationTypeEnum;
import com.ejlerp.log.enums.SysConstantEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 业务日志DTO实体类
 * @author paul
 */
public class LogBizRecordDTO extends AbstractObject {

    /**
     * 分页起始页
     */
    private Integer page;
    /**
     * 页大小
     */
    private Integer pageSize;


    private Long logBizRecordId;

    /**
    * 业务类型--如果发货单、入库单 
    */
    private String moduleType;

    /**
    * 业务操作类型：如创建发货单、发货单获取单号
    */
    private String operationType;

    /**
    * 操作结果：成功，或者失败的原因，或者描述信息的变化
    */
    private String operationResult;

    /**
    * 表名
    */
    private String entityTableName;

    /**
    * ID名字
    */
    private String entityIdName;

    /**
    * 操作对象实体ID
    */
    private Long entityId;

    /**
    * 1:系统异常
    */
    private Boolean exception;

    /**
    * 1:业务异常
    */
    private Boolean failed;

    /**
    * 日志是否客户可见 0:客户不可见 1:可见
    */
    private Boolean visibleToClient;

    /**
    * 操作人名称
    */
    private String operatorShowName;

    private Long creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    private Long lastUpdater;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastUpdateTime;

    private Boolean usable;

    private Long tenantId;

    public LogBizRecordDTO(String operationType, Long entityId, Long tenantId, Boolean usable) {
        this.operationType = operationType;
        this.entityId = entityId;
        this.tenantId = tenantId;
        this.usable = usable;
    }

    public LogBizRecordDTO() {
    }

    public static void propertyAfterSet(LogBizRecordDTO logBizRecordDTO, LogBizModuleTypeEnum moduleType,
                                        LogBizOperationTypeEnum operationType){
        logBizRecordDTO.setModuleType(null == moduleType ? SysConstantEnum.EMPTY_STR.getValue() : moduleType.getType());
        logBizRecordDTO.setOperationType(null == operationType ? SysConstantEnum.EMPTY_STR.getValue() :  operationType.getType());
        logBizRecordDTO.setVisibleToClient(Boolean.TRUE);
        logBizRecordDTO.setUsable(Boolean.TRUE);
    }


    @Override
    public String toString() {
        return "LogBizRecordDTO{" +
                "logBizRecordId=" + logBizRecordId +
                ", moduleType='" + moduleType + '\'' +
                ", operationType='" + operationType + '\'' +
                ", operationResult='" + operationResult + '\'' +
                ", entityTableName='" + entityTableName + '\'' +
                ", entityIdName='" + entityIdName + '\'' +
                ", entityId=" + entityId +
                ", exception=" + exception +
                ", failed=" + failed +
                ", visibleToClient=" + visibleToClient +
                ", operatorShowName='" + operatorShowName + '\'' +
                ", creator=" + creator +
                ", createTime=" + createTime +
                ", lastUpdater=" + lastUpdater +
                ", lastUpdateTime=" + lastUpdateTime +
                ", usable=" + usable +
                ", tenantId=" + tenantId +
                '}';
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getLogBizRecordId() {
        return logBizRecordId;
    }

    public void setLogBizRecordId(Long logBizRecordId) {
        this.logBizRecordId = logBizRecordId;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public String getEntityTableName() {
        return entityTableName;
    }

    public void setEntityTableName(String entityTableName) {
        this.entityTableName = entityTableName;
    }

    public String getEntityIdName() {
        return entityIdName;
    }

    public void setEntityIdName(String entityIdName) {
        this.entityIdName = entityIdName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Boolean getException() {
        return exception;
    }

    public void setException(Boolean exception) {
        this.exception = exception;
    }

    public Boolean getFailed() {
        return failed;
    }

    public void setFailed(Boolean failed) {
        this.failed = failed;
    }

    public Boolean getVisibleToClient() {
        return visibleToClient;
    }

    public void setVisibleToClient(Boolean visibleToClient) {
        this.visibleToClient = visibleToClient;
    }

    public String getOperatorShowName() {
        return operatorShowName;
    }

    public void setOperatorShowName(String operatorShowName) {
        this.operatorShowName = operatorShowName;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getLastUpdater() {
        return lastUpdater;
    }

    public void setLastUpdater(Long lastUpdater) {
        this.lastUpdater = lastUpdater;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Boolean getUsable() {
        return usable;
    }

    public void setUsable(Boolean usable) {
        this.usable = usable;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}