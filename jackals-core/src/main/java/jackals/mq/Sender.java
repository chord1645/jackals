package jackals.mq;

import com.alibaba.fastjson.JSONArray;
import jackals.Constants;
import jackals.model.RequestOjb;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Sender {
    private static final int SEND_NUMBER = 5;
    // ConnectionFactory ：连接工厂，JMS 用它创建连接
    ConnectionFactory connectionFactory;

    public Sender() {

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
        Sender s = new Sender();
//        for (int i = 0; i < 10; i++) {
        s.send(new ArrayList<RequestOjb>() {
            {
                add(new RequestOjb("1"));
//                add(new Request("2"));

            }
        }, "spider_" + 1);
//        }

    }

    public void send(List<RequestOjb> obj, String query) {
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
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            TextMessage message = session.createTextMessage(JSONArray.toJSONString(obj));
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