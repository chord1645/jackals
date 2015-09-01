package com.shrek.crawler.test;

import jackals.bin.ActiveMQSpider;
import jackals.bin.KafkaSpider;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


public class RunSpider1 extends BaseTest {
    @Test
    public void run() throws InterruptedException {
//        new LogbackConfigurer();
//        new ClassPathXmlApplicationContext("/jar/config/spring/applicationContext.xml");
//        new ActiveMQSpider("1").start();
//        TimeUnit.MINUTES.sleep(300000000);
        new KafkaSpider("8080").start();
    }



}