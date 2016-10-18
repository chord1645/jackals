package com.shrek.crawler.test.tmp;

import jackals.Constants;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class ReceiverTest implements MessageListener {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public ReceiverTest() {
        // ConnectionFactory ：连接工厂，JMS 用它创建连接
        ConnectionFactory connectionFactory;
        // Connection ：JMS 客户端到JMS Provider 的连接
        Connection connection = null;
        // Session： 一个发送或接收消息的线程
        Session session;
        // Destination ：消息的目的地;消息发送给谁.
        Queue queue;
        // 消费者，消息接收者
        MessageConsumer consumer;
        connectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                "");
        try {
            // 构造从工厂得到连接对象
            connection = connectionFactory.createConnection();
            // 启动
            connection.start();
            // 获取操作连接
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
//            session = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            queue = session.createQueue("queue_1");
            consumer = session.createConsumer(queue);
            consumer.setMessageListener(this);
//            while (true) {
//                System.out.println("while" );
//                Thread.sleep(5000);
//                TextMessage message = (TextMessage) consumer.receive();
//                System.out.println("receive" );
//                if (null != message) {
//                    System.out.println("收到消息" + message.getText());
//                } else {
//                    break;
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                if (null != connection)
//                    connection.close();
//            } catch (Throwable ignore) {
//            }
        }
    }

    public static void main(String[] args) {

        new ReceiverTest();
        System.out.print("dcdf");
    }

    @Override
    public void onMessage(Message message) {
        TextMessage msg = (TextMessage) message;

        try {
            System.out.println(msg.getText());
//            System.out.println("requestReceived");
//            String group = msg.getStringProperty("JMSXGroupID");
//            if ("A".equals(group)){
//                System.out.println();
//                System.out.println(msg.getText());
//                msg.acknowledge();
//            }else{
//                System.out.println("ignore");
//            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
        try {
           Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}