package com.shrek.crawler.test;

import jackals.bin.ActiveMQSpider;
import jackals.bin.KafkaSpider;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


public class RunSpider2 extends BaseTest {
    @Test
    public void run() throws InterruptedException {
//        new LogbackConfigurer();
//        new ClassPathXmlApplicationContext("/jar/config/spring/applicationContext.xml");
//        new KafkaSpider("2");
        new KafkaSpider("2").start();


    }



}