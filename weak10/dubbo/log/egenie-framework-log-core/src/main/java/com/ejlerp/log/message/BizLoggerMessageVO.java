package com.ejlerp.log.message;

import cn.egenie.mq.util.vo.AbstractMessageVo;
import com.ejlerp.log.enums.LogBizModuleTypeEnum;
import com.ejlerp.log.enums.LogBizOperationTypeEnum;

import java.util.Date;

/**
 * 日志记录消息VO
 * @author paul
 */
public class BizLoggerMessageVO extends AbstractMessageVo {

    private LogBizModuleTypeEnum moduleTypeEnum;
    private LogBizOperationTypeEnum operationTypeEnum;
    private String operationResult;
    private Long entityId;
    private String operatorShowName;
    private Boolean isException;
    private Boolean isFailed;
    private Date createTime;

    public BizLoggerMessageVO(String tags, String topic, String prefix, Long tenantId, Long operatorId, Long orderlyKey) {
        super(tags, topic, prefix, tenantId, operatorId, orderlyKey);
    }

    public BizLoggerMessageVO(String tags, String topic, String prefix, Long tenantId, Long operatorId, Long orderlyKey,
                              LogBizModuleTypeEnum moduleTypeEnum,
                              LogBizOperationTypeEnum operationTypeEnum, String operationResult,
                              Long entityId, String operatorShowName, Boolean isException, Boolean isFailed,
                              Date serviceTime) {
        super(tags, topic, prefix, tenantId, operatorId, orderlyKey);
        this.moduleTypeEnum = moduleTypeEnum;
        this.operationTypeEnum = operationTypeEnum;
        this.operationResult = operationResult;
        this.entityId = entityId;
        this.operatorShowName = operatorShowName;
        this.isException = isException;
        this.isFailed = isFailed;
        this.createTime = serviceTime;
        
    }
    @Override
    public String toString() {
        return "BizLoggerMessageVO{" +
                "moduleTypeEnum=" + moduleTypeEnum +
                ", operationTypeEnum=" + operationTypeEnum +
                ", operationResult='" + operationResult + '\'' +
                ", entityId=" + entityId +
                ", operationUserName='" + operatorShowName + '\'' +
                ", isException=" + isException +
                ", isFailed=" + isFailed +
                ", createTime=" + createTime +
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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getOperatorShowName() {
        return operatorShowName;
    }

    public void setOperatorShowName(String operatorShowName) {
        this.operatorShowName = operatorShowName;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
