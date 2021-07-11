package com.ejlerp.log.message;

import cn.egenie.mq.util.vo.AbstractMessageVo;
import com.ejlerp.log.enums.LogBizModuleTypeEnum;
import com.ejlerp.log.enums.LogBizOperationTypeEnum;

import java.util.Date;
import java.util.Set;

/**
 * 日志记录消息VO
 * @author paul
 */
public class BatchBizLoggerMessageVO extends AbstractMessageVo {

    private LogBizModuleTypeEnum moduleTypeEnum;
    private LogBizOperationTypeEnum operationTypeEnum;
    private String operationResult;
    private Set<Long> entityIds;
    private String operationUserName;
    private Boolean isException;
    private Boolean isFailed;
    private Date serviceTime;

    public BatchBizLoggerMessageVO(String tags, String topic, String prefix, Long tenantId, Long operatorId, Long orderlyKey) {
        super(tags, topic, prefix, tenantId, operatorId, orderlyKey);
    }

    public BatchBizLoggerMessageVO(String tags, String topic, String prefix, Long tenantId, Long operatorId, Long orderlyKey,
                                   LogBizModuleTypeEnum moduleTypeEnum,
                                   LogBizOperationTypeEnum operationTypeEnum, String operationResult,
                                   Set<Long> entityIds, String operationUserName, Boolean isException, Boolean isFailed,
                                   Date serviceTime) {
        super(tags, topic, prefix, tenantId, operatorId, orderlyKey);
        this.moduleTypeEnum = moduleTypeEnum;
        this.operationTypeEnum = operationTypeEnum;
        this.operationResult = operationResult;
        this.entityIds = entityIds;
        this.operationUserName = operationUserName;
        this.isException = isException;
        this.isFailed = isFailed;
        this.serviceTime = serviceTime;
    }

    @Override
    public String toString() {
        return "BatchBizLoggerMessageVO{" +
                "moduleTypeEnum=" + moduleTypeEnum +
                ", operationTypeEnum=" + operationTypeEnum +
                ", operationResult='" + operationResult + '\'' +
                ", entityIds=" + entityIds +
                ", operationUserName='" + operationUserName + '\'' +
                ", isException=" + isException +
                ", isFailed=" + isFailed +
                ", serviceTime=" + serviceTime +
                '}';
    }

    public LogBizModuleTypeEnum getModuleTypeEnum() {
        return moduleTypeEnum;
    }

    public void setModuleTypeEnum(LogBizModuleTypeEnum moduleTypeEnum) {
        this.moduleTypeEnum = moduleTypeEnum;
    }

    public LogBizOperationTypeEnum getOperationTypeEnum() {
        return operationTypeEnum;
    }

    public void setOperationTypeEnum(LogBizOperationTypeEnum operationTypeEnum) {
        this.operationTypeEnum = operationTypeEnum;
    }

    public String getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(String operationResult) {
        this.operationResult = operationResult;
    }

    public Set<Long> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(Set<Long> entityIds) {
        this.entityIds = entityIds;
    }

    public String getOperationUserName() {
        return operationUserName;
    }

    public void setOperationUserName(String operationUserName) {
        this.operationUserName = operationUserName;
    }

    public Boolean getException() {
        return isException;
    }

    public void setException(Boolean exception) {
        isException = exception;
    }

    public Boolean getFailed() {
        return isFailed;
    }

    public void setFailed(Boolean failed) {
        isFailed = failed;
    }

    public Date getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(Date serviceTime) {
        this.serviceTime = serviceTime;
    }
}
