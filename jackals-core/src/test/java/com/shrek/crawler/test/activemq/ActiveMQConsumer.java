package com.shrek.crawler.test.activemq;

import com.shrek.crawler.test.kafka.KafkaConsumer;
import jackals.mq.MQListener;
import jackals.mq.activemq.ActiveMQReceiver;
import jackals.utils.LogbackConfigurer;
import jackals.utils.SpringContextHolder;
import kafka.message.MessageAndMetadata;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.Properties;


public class ActiveMQConsumer extends ActiveMQReceiver {
    MQListener callback;

    public ActiveMQConsumer(String broker, String queueId, MQListener callback) {
        super(broker, queueId);
        this.callback = callback;
    }

    @Override
    public void onMessage(Message message) {
        try {
            //取得监听的任务ID
            TextMessage textMessage = (TextMessage) message;
            callback.requestReceived(textMessage.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    static String testTopic = "listener_job_news.mtime.com_1006";


//    public void run1() {
//        ConsumerConnector consumer = createConsumer();
//        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//        topicCountMap.put(testTopic, 3); // 一次从主题中获取一个数据
//        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
//        KafkaStream<byte[], byte[]> stream = messageStreams.get(testTopic).get(0);// 获取每次接收到的这个数据
//
//        System.out.println("stream.size(); " + stream.size());
//        ConsumerIterator<byte[], byte[]> iterator = stream.iterator();
//        while (iterator.hasNext()) {
//            MessageAndMetadata<byte[], byte[]> mnm = iterator.next();
//            String message = new String(mnm.message());
//            System.out.println("partition " + mnm.partition() + "接收到: " + message);
//        }
//        System.out.println("finish");
//    }

    public static void main(String[] args) {
        new LogbackConfigurer("/jar/config/test/logback.xml");
        Properties mqConfig = SpringContextHolder.getBean("mqConfig");
        ActiveMQConsumer testReceiver = new ActiveMQConsumer( mqConfig.getProperty("mq.amq.broker"), KafkaConsumer.testTopic, new MQListener() {
            public void requestReceived(String message) throws IOException {
                System.out.println(message);
            }

            public void requestReceived(MessageAndMetadata<byte[], byte[]> mnm) throws IOException {

            }
        });
        System.out.println("kafkaConsumer started");
    }


}