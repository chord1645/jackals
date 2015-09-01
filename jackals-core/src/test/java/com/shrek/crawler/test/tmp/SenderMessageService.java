package com.shrek.crawler.test.tmp;

import jackals.Constants;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Enumeration;

public class SenderMessageService {
    private static final int SEND_NUMBER = 5;
    // ConnectionFactory ：连接工厂，JMS 用它创建连接
    ConnectionFactory connectionFactory;

    public SenderMessageService() {

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
        new SenderMessageService().send();

    }

    public void send() {
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


            Queue replyTo = session.createTemporaryQueue();
            MessageConsumer consumer = session.createConsumer(replyTo);
            Queue testQueue = session.createQueue("queue_1");
            MessageProducer producer = session.createProducer(null);
            Queue query = session.createQueue(" ActiveMQ.Statistics.Destination" + testQueue.getQueueName());
//            Destination replyTo = session.createTemporaryQueue();
//            MessageConsumer consumer = session.createConsumer(replyTo);
//            Queue query = session.createQueue("ActiveMQ.Statistics.Broker");
//            MessageProducer producer = session.createProducer(query);
            Message msg = session.createMessage();

            producer.send(testQueue, msg);
            msg.setJMSReplyTo(replyTo);
            producer.send(query, msg);
            MapMessage reply = (MapMessage) consumer.receive();

            for (Enumeration e = reply.getMapNames(); e.hasMoreElements(); ) {
                String name = e.nextElement().toString();
                System.err.println(name + "=" + reply.getObject(name));
            }
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