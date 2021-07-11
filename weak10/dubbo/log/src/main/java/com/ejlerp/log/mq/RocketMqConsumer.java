package com.ejlerp.log.mq;

import cn.egenie.mq.client.consumer.DefaultConsumer;
import cn.egenie.mq.client.spring.annotation.RocketMQMessageListener;
import cn.egenie.mq.client.spring.core.RocketMQListener;
import cn.egenie.mq.client.spring.core.RocketMQPushConsumerLifecycleListener;
import cn.egenie.mq.core.MessageConsumeResult;
import com.ejlerp.log.api.ConsumerService;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author paul
 */
@RocketMQMessageListener
public class RocketMqConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqConsumer.class);

    private final ConsumerService consumerService;

    public RocketMqConsumer(ConsumerService consumerService) {
        this.consumerService = consumerService;
    }


    @Override
    public MessageConsumeResult onMessage(MessageExt messageExt) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("mq consumer messageExt ID:{}, consumer times:{}", messageExt.getMsgId(), messageExt.getReconsumeTimes());
        } else if (logger.isInfoEnabled()) {
            logger.info("mq consumer messageExt ID:{}, consumer times:{}", messageExt.getMsgId(), messageExt.getReconsumeTimes());
        }
        // 检查消息的完整性
        String tags = messageExt.getTags();

        int msgTags = Integer.parseInt(tags);

        switch (msgTags){
            case 5000004:
                return consumerService.logSingle(messageExt);
            case 5000005:
                return consumerService.logBatch(messageExt);
            default:
                return new MessageConsumeResult(true);
        }

    }

    @Override
    public void prepareStart(DefaultConsumer consumer) {

    }
}
