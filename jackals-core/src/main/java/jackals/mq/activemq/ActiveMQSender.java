package jackals.mq.activemq;

import com.alibaba.fastjson.JSON;
import jackals.Constants;
import jackals.model.RequestOjb;
import jackals.mq.CommonTextSender;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

public class ActiveMQSender extends CommonTextSender {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final int SEND_NUMBER = 5;
    // ConnectionFactory ：连接工厂，JMS 用它创建连接
    ConnectionFactory connectionFactory;
    // Connection ：JMS 客户端到JMS Provider 的连接
    Connection connection = null;
    // Session： 一个发送或接收消息的线程
    // Destination ：消息的目的地;消息发送给谁.
    Destination destination;
    // MessageProducer：消息发送者
    MessageProducer producer;
    Session session = null;

    // TextMessage message;
    // 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
    public ActiveMQSender(String broker) {
        try {
            connectionFactory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD,
                    broker);
            // 构造从工厂得到连接对象
            connection = connectionFactory.createConnection();
            // 启动
            connection.start();
            /*************************************/

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

    public static void main(String[] args) {
//        ActiveMQSender s = new ActiveMQSender(Constants.AMQ.broker);
////        for (int i = 0; i < 10; i++) {
//        s.sendOne("", "spider_" + 1);
////        }

    }

    public void _sendBatch(String topic, List<String> msg) {
        Session session = null;
        try {
            session = connection.createSession(Boolean.TRUE,
                    Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(topic);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            for (String obj : msg) {
                TextMessage message = session.createTextMessage(obj);
                producer.send(message);
            }
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != session)
                    session.close();
            } catch (Throwable ignore) {
            }
        }
    }

    public void sendBatchRequest(String topic, final List<RequestOjb> list) {
        List<String> rList = new ArrayList<String>() {
            {
                for (RequestOjb r : list) {
                    add(JSON.toJSONString(r));
                }
            }
        };
        _sendBatch(topic, rList);
    }

    public void sendOne(String topic, String msg) {
        long s = System.currentTimeMillis();
        Session session = null;
        try {
            session = connection.createSession(Boolean.TRUE,
                    Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(topic);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            TextMessage message = session.createTextMessage(msg);
            producer.send(message);
            session.commit();
            logger.info("cost: {}ms {}/{}",System.currentTimeMillis()-s ,topic,msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != session)
                    session.close();
            } catch (Throwable ignore) {
            }
        }
    }

    /////////////////

    public void _sendOne(String topic, String msg) {
        Session session = null;
        try {
            session = connection.createSession(Boolean.TRUE,
                    Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(topic);
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            TextMessage message = session.createTextMessage(JSON.toJSONString(msg));
            producer.send(message);
            session.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != session)
                    session.close();
            } catch (Throwable ignore) {
            }
        }
    }

//    public void producter(String topic) {
//        Session session = null;
//        try {
//            session = connection.createSession(Boolean.TRUE,
//                    Session.AUTO_ACKNOWLEDGE);
//            Destination destination = session.createQueue(topic);
//            MessageProducer producer = session.createProducer(destination);
//            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
//            TextMessage message = session.createTextMessage(JSON.toJSONString(msg));
//            producer.send(message);
//            session.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (null != session)
//                    session.close();
//            } catch (Throwable ignore) {
//            }
//        }
//    }
}