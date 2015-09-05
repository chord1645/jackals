package com.shrek.crawler.test.kafka;

import com.shrek.crawler.test.BaseTest;
import jackals.mq.kafka.KafkaSender;
import jackals.mq.kafka.TopicManager;
import jackals.utils.LogbackConfigurer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


public class KafkaProducer extends BaseTest implements Runnable {

    KafkaSender sender = new KafkaSender();

    public KafkaProducer() {
        super();
    }


    @Override
    public void run() {

//        Producer producer = createProducer();
        int i = 0;
        while (true) {
            System.out.println("send=" + i);
            sender.sendOne(KafkaConsumer.testTopic,i+"", "message: " + i++);
//            producer.send(new KeyedMessage<String, String>(KafkaConsumer.testTopic, i+"", "message: " + i++));
            if (i > 50000)
                break;
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void start() throws InterruptedException {
        new LogbackConfigurer("/jar/config/test/logback.xml");
        TopicManager.deleteTopic(KafkaConsumer.testTopic);
//        for(int i=0;i<3;i++){
        new Thread(new KafkaProducer()).start();
//        }
        TimeUnit.HOURS.sleep(1);

    }

}  
  