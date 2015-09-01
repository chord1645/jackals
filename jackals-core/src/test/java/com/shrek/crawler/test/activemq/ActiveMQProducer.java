package com.shrek.crawler.test.activemq;

import com.shrek.crawler.test.kafka.KafkaConsumer;
import jackals.Constants;
import jackals.mq.activemq.ActiveMQSender;
import jackals.utils.SpringContextHolder;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class ActiveMQProducer extends Thread {


    public ActiveMQProducer() {
        super();
    }


    @Override
    public void run() {

//        s.producter(KafkaConsumer.testTopic);
//        List<String> list = new ArrayList<String>();
//        for (int i = 0; i < 520; i++) {
//            list.add("message: " + i);
//        }
//        s._sendBatch(KafkaConsumer.testTopic, list);
        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    Properties mqConfig = SpringContextHolder.getBean("mqConfig");
                    ActiveMQSender s = new ActiveMQSender(mqConfig.getProperty("mq.amq.broker"));
                    while (true) {
                        List<String> list = new ArrayList<String>();
                        for (int i = 0; i < 30; i++) {
                            list.add("message: " + i);
                        }
                        s._sendBatch(KafkaConsumer.testTopic, list);
//                    for (int i = 0; i < 10000; i++) {
//
//                        System.out.println("send=" + i);
//                        s.sendOne(KafkaConsumer.testTopic, "message: " + i++);
//                        try {
//                            TimeUnit.MILLISECONDS.sleep(200);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
                    }
                }
            }.start();

        }

//        System.out.println("_sendBatch");
    }


    public static void main(String[] args) {
//        for(int i=0;i<3;i++){
        new ActiveMQProducer().start();// 使用kafka集群中创建好的主题 test
//        }

    }

}  
  