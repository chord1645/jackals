package com.shrek.crawler.test.kafka;

import com.shrek.crawler.test.BaseTest;
import jackals.Constants;
import jackals.mq.MQListener;
import jackals.mq.kafka.KafkaReceiver;
import jackals.utils.BlockExecutorPool;
import jackals.utils.CountableThreadPool;
import jackals.utils.LogbackConfigurer;
import jackals.utils.SpringContextHolder;
import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class KafkaReceiverTest extends BaseTest implements Runnable {
    @Test
    public void main() throws InterruptedException {
        new LogbackConfigurer("/jar/config/test/logback.xml");
        KafkaReceiver receiver = new KafkaReceiver();
        receiver.startListen(KafkaConsumer.testTopic,new MQListener() {
            @Override
            public void onReceived(String message) throws IOException {
                System.out.println(message);
            }

            @Override
            public void onReceived(MessageAndMetadata<byte[], byte[]> mnm) throws IOException {
                String key = mnm.key() == null ? null : new String(mnm.key());
                System.out.println("partition " + mnm.topic() + "\t" + mnm.partition() + " " + key
                        + ": " + new String(mnm.message()));
            }
        });
    }

    @Override
    public void run() {

    }
}