package jackals.mq.kafka;

import com.alibaba.fastjson.JSON;
import jackals.Constants;
import jackals.model.RequestOjb;
import jackals.mq.CommonTextSender;
import jackals.utils.SpringContextHolder;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class KafkaSender extends CommonTextSender {
    private Logger logger = LoggerFactory.getLogger(getClass());


    private Producer createProducer() {
        Properties mqConfig = SpringContextHolder.getBean("mqConfig");
        Properties properties = new Properties();
        properties.put("zookeeper.connect", mqConfig.getProperty("mq.kafka.zookeeper"));//声明zk
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("metadata.broker.list", mqConfig.getProperty("mq.kafka.server"));
        properties.put("group.id", Constants.Kafka.defaultGroup+System.currentTimeMillis());// 必须要使用别的组名称， 如果生产者和消费者都在同一组，则不能访问同一组内的topic数据
        return new Producer<Integer, String>(new ProducerConfig(properties));
    }
    public void sendOne(String topic, String key ,String msg) {
        long s = System.currentTimeMillis();
        logger.debug("send topic={} ,msg={}", topic, msg);
        Producer producer = createProducer();
        producer.send(new KeyedMessage<String, String>(topic,key, msg));
//        producer.send(new KeyedMessage<String, String>(topic, randomKey(), msg));
        producer.close();
        logger.info("cost:{}ms", System.currentTimeMillis() - s);
    }
    @Override
    public void sendOne(String topic, String msg) {
        long s = System.currentTimeMillis();
        logger.debug("send topic={} ,msg={}", topic, msg);
        Producer producer = createProducer();
//        producer.send(new KeyedMessage<String, String>(topic, msg));
        producer.send(new KeyedMessage<String, String>(topic, randomKey(), msg));
        producer.close();
        logger.info("cost:{}ms", System.currentTimeMillis() - s);
    }

    private String randomKey() {
        return UUID.randomUUID().toString();
    }

//    @Override
//    public void sendBatchRequest(String topic, List<String> msg) {
//        logger.info("sendBatchRequest topic={} ,msg={}", topic, msg);
//        Producer producer = createProducer();
//        for (String s : msg) {
//            producer.send(new KeyedMessage<Integer, String>(topic, s));
//        }
//        producer.close();
//        logger.info("sendBatchRequest done topic={} ,msg={}", topic, msg);
//
//    }

    @Override
    public void sendBatchRequest(String topic, List<RequestOjb> list) {
        logger.debug("sendBatchRequest topic={} ,msg={}", topic, list);
        if (CollectionUtils.isEmpty(list))
            return;
        Producer producer = createProducer();
        int i = 0;//key=0时无法正常接收消息
        List<KeyedMessage> msgList = new ArrayList<KeyedMessage>();
        for (RequestOjb r : list) {
            i++;
            msgList.add(new KeyedMessage<String, String>(topic,i+"", JSON.toJSONString(r)));
        }
        producer.send(msgList);
        producer.close();
        logger.debug("sendBatchRequest done topic={} ,msg={}", topic, list);
    }

    public static void main(String[] args) {
        //TODO 任务管理
//        CommonTextSender commonSender = new KafkaSender();
//        JobInfo jobInfo = williamlong();
//        commonSender.sendOne(Constants.TopicSpiderPrefix + "1", JSON.toJSONString(jobInfo));
//
//        commonSender = new KafkaSender();
//        Request request = new Request("http://www.williamlong.info/");
//        commonSender.sendOne(Constants.TopicJobPrefix +jobInfo.getId(), JSON.toJSONString(request));


    }

    public void sendBatch(String topic, List<KeyedMessage> list) {
        logger.debug("sendBatchRequest topic={} ,msg={}", topic, list);
        if (CollectionUtils.isEmpty(list))
            return;
        Producer producer = createProducer();
        producer.send(list);
        producer.close();
        logger.debug("sendBatchRequest done topic={} ,msg={}", topic, list);
    }


}