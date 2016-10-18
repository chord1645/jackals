package jackals.mq.kafka;

import jackals.Constants;
import jackals.mq.MQListener;
import jackals.utils.LogbackConfigurer;
import jackals.utils.SpringContextHolder;
import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Properties;


public class KafkaReceiver {
    private Logger logger = LoggerFactory.getLogger(getClass());
    ConsumerConnector consumer;
    List<KafkaStream<byte[], byte[]>> streams;
    public KafkaReceiver() {
    }

    public void startListen(String topic, String group, MQListener callback) {
        logger.info("startListen : {} {} ", topic, group);
        consumer = createConsumer(group);
//        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//        topicCountMap.put(topic, 1); // 一次从主题中获取一个数据
        Whitelist whitelist = new Whitelist(topic);
         streams = consumer.createMessageStreamsByFilter(whitelist);
//        System.out.println(" messageStreams.get = " + streams.size());
        for (final KafkaStream stream : streams) {
//            System.out.println("  for (final KafkaStream stream : streams) {");
            ConsumerIterator<byte[], byte[]> it = stream.iterator();
//            logger.info("stream.iterator(); {__________________________________________________");
//            logger.info("stream.iterator(); {__________________________________________________"+it.size());
            while (it.hasNext()) {
//                logger.info("while (it.hasNext()) {__________________________________________________");
                MessageAndMetadata<byte[], byte[]> mnm = it.next();
//                String msg = new String(mnm.message());

                logger.debug("kafka msg [{}] [{}] [{}] [{}]", topic, mnm.partition(), mnm.offset(), mnm);
                try {
                    callback.requestReceived(mnm);
                } catch (Throwable e) {
                    logger.error("MQListener callback Exception [{}] [{}] [{}] [{}] [{}] [{}]:",
                            topic, mnm.partition(), mnm.offset(), new String(mnm.message()), e.toString(), e);
                } finally {
//                    consumer.commitOffsets();
                }
//                System.out.println("partition " + mnm.partition() + ": " + new String(mnm.message()));
            }
            logger.info("Shutting down Thread: ");
        }
    }

    public void startListen(String topic, MQListener callback) {
        startListen(topic, Constants.Kafka.defaultGroup, callback);
    }

    private ConsumerConnector createConsumer(String group) {
        Properties mqConfig = SpringContextHolder.getBean("mqConfig");
        Properties properties = new Properties();
        properties.put("zookeeper.connect", mqConfig.getProperty("mq.kafka.zookeeper"));//声明zk
        properties.put("group.id", group);// 必须要使用别的组名称， 如果生产者和消费者都在同一组，则不能访问同一组内的topic数据
        ConsumerConfig cfg = new ConsumerConfig(properties);
//        System.out.println(cfg.autoCommitEnable());
        return Consumer.createJavaConsumerConnector(cfg);
    }


    public static void main(String[] args) throws InterruptedException {
        new LogbackConfigurer();
        final KafkaReceiver kafkaReceiver = new KafkaReceiver();
        new Thread() {
            @Override
            public void run() {
                kafkaReceiver.startListen("test", "group003", new MQListener() {
                    long id = 1;

                    public void requestReceived(String message) {
                    }

                    public void requestReceived(MessageAndMetadata<byte[], byte[]> mnm) {

                        long offset = mnm.offset();
//                        if (id == offset % 2) {
                        String msg = new String(mnm.message());
                        String key = new String(mnm.key());
//                        if (offset)
                        System.out.println("requestReceived " + "test  " + key + "  " + mnm.offset() + "    " + msg);
//                        }

                    }
                });
            }
        }.start();
        System.out.println("start>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        TimeUnit.SECONDS.sleep(300);
//        System.exit(0);
//        kafkaReceiver.consumer.shutdown();

    }

    public void storpListen() {
        if (!CollectionUtils.isEmpty(streams)){
            for (KafkaStream<byte[], byte[]> s:streams){
                s.clear();
            }
        }
        if (consumer != null) {
            logger.info("consumer shutdown 1");
            consumer.shutdown();
            logger.info("consumer shutdown 2");
        }
    }
}