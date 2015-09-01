package jackals.mq.activemq;

import jackals.bin.ActiveMQSpider;
import jackals.mq.MQListener;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

public class ActiveMQReceiver implements MessageListener {
    private Logger logger = LoggerFactory.getLogger(getClass());
    ConnectionFactory connectionFactory;
    Connection connection = null;
    Session session;
    Queue queue;
    MessageConsumer consumer;

    public ActiveMQReceiver(String broker) {
        try {
            connectionFactory = new ActiveMQConnectionFactory(
                    ActiveMQConnection.DEFAULT_USER,
                    ActiveMQConnection.DEFAULT_PASSWORD,
                    broker);
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public ActiveMQReceiver(String broker, String queueId) {
        this(broker);
        try {
            session = connection.createSession(Boolean.FALSE,
                    Session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue(queueId);
//            consumer = session.createConsumer(queue,"type='2'");
            consumer = session.createConsumer(queue);
            consumer.setMessageListener(this);
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

    public void shutdown() {
        try {
            consumer.close();
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

    }


    @Override
    public void onMessage(Message message) {
        logger.info("nothing");
    }

    public void addListener(String topic,MessageListener listener) {
        try {
            session = connection.createSession(Boolean.FALSE,
                    Session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue(topic);
//            consumer = session.createConsumer(queue,"type='2'");
            consumer = session.createConsumer(queue);
            consumer.setMessageListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}