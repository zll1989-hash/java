

@Component
public class MQListener {


    @KafkaListener(topics = {"user.register.topic"},groupId = "xdclass-gp2")
    public void onMessage(ConsumerRecord<?, ?> record, Acknowledgment ack,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){


        System.out.println("消费消息："+record.topic()+"----"+record.partition()+"----"+record.value());

        ack.acknowledge();
    }

}
