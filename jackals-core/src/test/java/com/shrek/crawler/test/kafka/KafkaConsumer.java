package com.shrek.crawler.test.kafka;

import com.shrek.crawler.test.BaseTest;
import jackals.Constants;
import jackals.mq.MQListener;
import jackals.utils.BlockExecutorPool;
import jackals.utils.CountableThreadPool;
import jackals.utils.LogbackConfigurer;
import jackals.utils.SpringContextHolder;
import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.junit.Test;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class KafkaConsumer extends BaseTest implements Runnable {
    //    static String testTopic = "listener_job_news.mtime.com_1006";
    public static String testTopic = "test";
    @Test
    public void main() throws InterruptedException {
//        testTopic = Constants.TopicSpiderPrefix+"30";
        new LogbackConfigurer("/jar/config/test/logback.xml");
        new Thread(new KafkaConsumer()).start();
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    TimeUnit.SECONDS.sleep(30);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                kafkaConsumer.shutdown();
//            }
//        }.start();
        System.out.println("kafkaConsumer started");
        TimeUnit.MINUTES.sleep(3000);
    }
    public KafkaConsumer() {
        super();
    }

    ConsumerConnector consumer;

    private ConsumerConnector createConsumer() {
        Properties mqConfig = SpringContextHolder.getBean("mqConfig");
        Properties properties = new Properties();
        properties.put("zookeeper.connect", mqConfig.getProperty("mq.kafka.zookeeper"));//声明zk
        properties.put("group.id", Constants.Kafka.defaultGroup);// 必须要使用别的组名称， 如果生产者和消费者都在同一组，则不能访问同一组内的topic数据
        return Consumer.createJavaConsumerConnector(new ConsumerConfig(properties));
    }

    List<KafkaStream<byte[], byte[]>> streams;

    public void run(MQListener callback) {
        consumer = createConsumer();
//        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//        topicCountMap.put(testTopic, 1); // 一次从主题中获取一个数据
        Whitelist whitelist = new Whitelist(testTopic);

        streams = consumer.createMessageStreamsByFilter(whitelist);

//        Map<String, List<KafkaStream<byte[], byte[]>>> messageStreams = consumer.createMessageStreams(topicCountMap);
//        List<KafkaStream<byte[], byte[]>> streams = messageStreams.get(testTopic);
        System.out.println(" messageStreams.get = " + streams.size());

        // now launch all the threads
        //
        // now create an object to consume the messages
        //
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            System.out.println("for (final KafkaStream stream : streams) {");
            ConsumerIterator<byte[], byte[]> it = stream.iterator();

            while (it.hasNext()) {
                synchronized (this) {
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++");
                    MessageAndMetadata<byte[], byte[]> mnm = it.next();

                    try {
                        callback.requestReceived(mnm);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
//                        consumer.commitOffsets();
//                        consumer.commitOffsets(true);
                    }
                    System.out.println("-------------------------------------");

                }
//                System.out.println("partition " + mnm.partition() +" "+new String(mnm.key())+ ": " + new String(mnm.message()));
            }
            System.out.println("Shutting down Thread: ");
            threadNumber++;
        }
    }

    class Foo implements MQListener {
        protected BlockExecutorPool executor = new BlockExecutorPool(3);
        protected CountableThreadPool executor1 = new CountableThreadPool(3);

        public void execute(String message) {
            executor.execute(new Runnable() {
                public void run() {
//                    System.out.println("======================================");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
//                    System.out.println("--------------------------------------------");
                }
            });
//            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

        }

        public void requestReceived(MessageAndMetadata<byte[], byte[]> mnm) {
//            System.out.println("+++++++++++++++++++++++++++++++++++++++++++");

            String key = mnm.key() == null ? null : new String(mnm.key());
            System.out.println("partition " + mnm.topic() + "\t" + mnm.partition() + " " + key
                    + ": " + new String(mnm.message()));

            String msg = new String(mnm.message());
            requestReceived(msg);
        }

        //    @Override
        public void requestReceived(String message) {
            execute(message);
        }
    }
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

    public void run() {
        run(new Foo());
    }



    synchronized private void shutdown() {
        System.out.println("{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{");
        for (KafkaStream<byte[], byte[]> s : streams) {
            s.clear();
        }
        consumer.shutdown();

        System.out.println("]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");

    }

}