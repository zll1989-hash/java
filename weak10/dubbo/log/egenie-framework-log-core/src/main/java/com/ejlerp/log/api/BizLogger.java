package com.ejlerp.log.api;

import cn.egenie.mq.client.spring.core.RocketMQTemplate;
import cn.egenie.mq.util.model.MQBizModel;
import cn.egenie.mq.util.tags.MQNormalMsgTagsConstants;

import com.ejlerp.log.enums.LogBizModuleTypeEnum;
import com.ejlerp.log.enums.LogBizOperationTypeEnum;
import com.ejlerp.log.message.BatchBizLoggerMessageVO;
import com.ejlerp.log.message.BizLoggerMessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

/**
 * @author paul
 */
@Component
public class BizLogger {
    /**
     * mq消息发送组建
     */
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    /**
     * 新增业务日志
     *
     * @param operatorId        登陆用户ID
     * @param tenantId          登陆用户租户ID
     * @param moduleTypeEnum    业务操作大业务类型 ：如发货单 入库单
     * @param operationTypeEnum 业务操作类型 ： 如创建发货单 获取单号
     * @param operationResult   操作结果 成功或者失败 或者异常信息
     * @param entityId          操作表ID
     * @param operatorShowName  操作人员名称
     * @param isException       是否是系统异常
     * @param isFailed          是否是业务异常
     * @param serviceTime       业务操作时间
     * @return 新增结果条数
     * @see com.ejlerp.log.enums.LogBizModuleTypeEnum
     * @see com.ejlerp.log.enums.LogBizOperationTypeEnum
     */
    public void recordLog(Long operatorId, Long tenantId, LogBizModuleTypeEnum moduleTypeEnum,
                          LogBizOperationTypeEnum operationTypeEnum, String operationResult, Long entityId,
                          String operatorShowName, Boolean isException, Boolean isFailed, Date serviceTime) {
        BizLoggerMessageVO messageVO = new BizLoggerMessageVO(MQNormalMsgTagsConstants.LOG_WMS,
                null, null, tenantId, operatorId, null,
                moduleTypeEnum, operationTypeEnum, operationResult, entityId, operatorShowName, isException, isFailed,
                serviceTime);

        rocketMQTemplate.sendMessage(MQBizModel.MQ_MODEL_LOG, messageVO);
    }

    /**
     * 批量新增业务日志
     *
     * @param operatorId        登陆用户ID
     * @param tenantId          登陆用户租户ID
     * @param moduleTypeEnum    业务操作大业务类型 ：如发货单 入库单
     * @param operationTypeEnum 业务操作类型 ： 如创建发货单 获取单号
     * @param operationResult   操作结果 成功或者失败 或者异常信息
     * @param entityIds         操作表ID
     * @param operationUserName 操作人员名称
     * @param isException       是否是系统异常
     * @param isFailed          是否是业务异常
     * @param serviceTime       业务操作时间
     * @return 新增结果条数
     * @see com.ejlerp.log.enums.LogBizModuleTypeEnum
     * @see com.ejlerp.log.enums.LogBizOperationTypeEnum
     */
    public void batchRecordLog(Long operatorId, Long tenantId, LogBizModuleTypeEnum moduleTypeEnum,
                               LogBizOperationTypeEnum operationTypeEnum, String operationResult, Set<Long> entityIds,
                               String operationUserName, Boolean isException, Boolean isFailed, Date serviceTime) {
        BatchBizLoggerMessageVO messageVO = new BatchBizLoggerMessageVO(MQNormalMsgTagsConstants.BATCH_LOG_WMS,
                null, null, tenantId, operatorId, null,
                moduleTypeEnum, operationTypeEnum, operationResult, entityIds, operationUserName, isException, isFailed,
                serviceTime);

        rocketMQTemplate.sendMessage(MQBizModel.MQ_MODEL_LOG, messageVO);
    }

}
