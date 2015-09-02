package com.shrek.crawler.test.kafka;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.shrek.crawler.test.BaseTest;
import jackals.Constants;
import jackals.mq.kafka.KafkaSender;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.junit.Test;


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
            sender.sendOne(KafkaConsumer.testTopic, "message: " + i++);
//            producer.send(new KeyedMessage<String, String>(KafkaConsumer.testTopic, i+"", "message: " + i++));
            if (i > 50000)
                break;
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void start() throws InterruptedException {
//        for(int i=0;i<3;i++){
        new Thread(new KafkaProducer()).start();
//        }
        TimeUnit.HOURS.sleep(1);

    }

}  
  