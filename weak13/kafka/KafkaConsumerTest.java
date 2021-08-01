


public class KafkaConsumerTest {


    public static Properties getProperties() {

        Properties props = new Properties();

        //broker地址
        props.put("bootstrap.servers", "112.74.55.160:9092");

        //消费者分组ID，分组内的消费者只能消费该消息一次，不同分组内的消费者可以重复消费该消息
        props.put("group.id", "xdclass-g1");

        //默认是latest，如果需要从头消费partition消息，需要改为 earliest 且消费者组名变更，才生效
        props.put("auto.offset.reset","earliest");

        //开启自动提交offset
        //props.put("enable.auto.commit", "true");
        props.put("enable.auto.commit", "false");

        //自动提交offset延迟时间
        //props.put("auto.commit.interval.ms", "1000");

        //反序列化
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        return props;
    }



    @Test
    public void simpleConsumerTest(){

        Properties properties = getProperties();

        KafkaConsumer<String,String> kafkaConsumer = new KafkaConsumer<>(properties);

        //订阅主题
        kafkaConsumer.subscribe(Arrays.asList(KafkaProducerTest.TOPIC_NAME));


        while (true){
            //领取时间，阻塞超时时间
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));

            for(ConsumerRecord record : records){
                System.err.printf("topic=%s, offset=%d,key=%s,value=%s %n",record.topic(),record.offset(),record.key(),record.value());
            }

            //同步阻塞提交offset
            //kafkaConsumer.commitSync();

            if(!records.isEmpty()){
                //异步提交offset
                kafkaConsumer.commitAsync(new OffsetCommitCallback() {
                    @Override
                    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {

                        if(exception == null){
                            System.err.println("手工提交offset成功:"+offsets.toString());
                        }else {
                            System.err.println("手工提交offset失败:"+offsets.toString());
                        }
                    }
                });
            }
        }

    }





}
