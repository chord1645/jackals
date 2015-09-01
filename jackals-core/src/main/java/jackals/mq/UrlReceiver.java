package jackals.mq;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jackals.Constants;
import jackals.job.ActiveMQSpiderJob;
import jackals.model.RequestOjb;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.IOException;
import java.util.List;

@Deprecated
public class UrlReceiver implements MessageListener {
    private Logger logger = LoggerFactory.getLogger(getClass());
    ActiveMQSpiderJob spider;

    public UrlReceiver(ActiveMQSpiderJob spider) {
        this.spider = spider;
        // ConnectionFactory ：连接工厂，JMS 用它创建连接
        ConnectionFactory connectionFactory;
        // Connection ：JMS 客户端到JMS Provider 的连接
        Connection connection = null;
        // Session： 一个发送或接收消息的线程
        Session session;
        // Destination ：消息的目的地;消息发送给谁.
        Destination destination;
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
            session = connection.createSession(Boolean.FALSE,
                    Session.AUTO_ACKNOWLEDGE);
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            destination = session.createQueue("spider_" + spider.getJobInfo().getId());

            consumer = session.createConsumer(destination);

            consumer.setMessageListener(this);
//            while (true) {
//                ObjectMessage message = (ObjectMessage) consumer.receiveNoWait();
//                if (null != message) {
//                    System.out.println("收到消息" + message.getObject());
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
        new UrlReceiver(null);
    }

    @Override
    public void onMessage(Message message) {

        try {
            logger.info("onReceived spider_{} {}",spider.getJobInfo().getId(),message);
            TextMessage textMessage = (TextMessage) message;
//            logger.info(objectMessage.toString());
//            ObjectMapper mapper = new ObjectMapper();
//            List<RequestOjb> list = mapper.readValue(textMessage.getText(), new TypeReference<List<RequestOjb>>() {});
            List<RequestOjb> list = JSON.parseArray(textMessage.getText(), RequestOjb.class);
            for (RequestOjb r: list){
                spider.executeRequest(r);
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        try {
//            queue.push((Request) objectMessage.getObject());
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
    }
}