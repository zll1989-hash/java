package com.ejlerp.log.api;

import cn.egenie.mq.core.MessageConsumeResult;
import cn.egenie.mq.util.helper.MessageHelper;
import com.ejlerp.log.message.BatchBizLoggerMessageVO;
import com.ejlerp.log.message.BizLoggerMessageVO;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @date 2021-05-17
 * @author paul
 */
@Component
public class ConsumerService {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);
    /***
     * 日志记录管理service组件
     */
    @Autowired
    private LogBizService logBizService;

    /***
     * 单条日志记录
     * @param messageExt 消息体
     * @return 消费结果
     */
    public MessageConsumeResult logSingle(MessageExt messageExt){
        MessageConsumeResult messageConsumeResult = new MessageConsumeResult(Boolean.TRUE);

        BizLoggerMessageVO messageVO = MessageHelper.message(messageExt.getBody(), BizLoggerMessageVO.class);

        logger.info("### 记录日志：{}", messageVO);
        logBizService.log(messageVO);

        return messageConsumeResult;
    }

    /**
     * 同一业务操作的批量日志记录
     * @param messageExt 消息体
     * @return 消费结果
     */
    public MessageConsumeResult logBatch(MessageExt messageExt){
        MessageConsumeResult messageConsumeResult = new MessageConsumeResult(Boolean.TRUE);

        BatchBizLoggerMessageVO messageVO = MessageHelper.message(messageExt.getBody(), BatchBizLoggerMessageVO.class);

        logger.info("### 记录日志：{}", messageVO);
        logBizService.batchLog(messageVO);

        return messageConsumeResult;
    }
}
