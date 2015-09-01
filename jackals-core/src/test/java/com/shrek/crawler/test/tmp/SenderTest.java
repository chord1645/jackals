package com.shrek.crawler.test.tmp;

import jackals.Constants;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class SenderTest {
    private static final int SEND_NUMBER = 5;
    // ConnectionFactory ：连接工厂，JMS 用它创建连接
    ConnectionFactory connectionFactory;

    public SenderTest() {

        // Connection ：JMS 客户端到JMS Provider 的连接
        Connection connection = null;
        // Session： 一个发送或接收消息的线程
        Session session;
        // Destination ：消息的目的地;消息发送给谁.
        Destination destination;
        // MessageProducer：消息发送者
        MessageProducer producer;
        // TextMessage message;
        // 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
        connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "");

    }

    public static void main(String[] args) {
        for (int i = 0;i<10; i++) {
            new Thread(){
                @Override
                public void run() {
                    SenderTest s = new SenderTest();
                    for (int n = 0;n<5000; n++) {
                        s.send("test" + n, "queue_" + 1, n);
                    }
                }
            }.start();
        }

        //        SenderTest s = new SenderTest();
//        for (int i = 0;i<1000000; i++) {
//        s.send("test"+i, "queue_" + 1,i);
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

    }

    public void send(String obj, String query, int priority) {
        Connection connection = null;
        try {
            // 构造从工厂得到连接对象
            connection = connectionFactory.createConnection();
            // 启动
            connection.start();
            // 获取操作连接
            Session session = connection.createSession(Boolean.TRUE,
                    Session.AUTO_ACKNOWLEDGE);
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            Destination destination = session.createQueue(query);
            // 得到消息生成者【发送者】
            MessageProducer producer = session.createProducer(destination);
            // 设置不持久化，此处学习，实际根据项目决定
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            TextMessage message = session.createTextMessage(obj);
//            message.setStringProperty("JMSXGroupID", "A");
            message.setJMSPriority(priority);
            // 发送消息到目的地方
            producer.send(message);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != connection)
                    connection.close();
            } catch (Throwable ignore) {
            }
        }
    }
}