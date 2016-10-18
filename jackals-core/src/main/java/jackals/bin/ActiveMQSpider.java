package jackals.bin;

import jackals.Constants;
import jackals.job.ActiveMQSpiderJob;
import jackals.job.pojo.JobInfo;
import jackals.job.SpiderJob;
import jackals.model.RequestOjb;
import jackals.mq.activemq.ActiveMQReceiver;
import jackals.mq.activemq.ActiveMQSender;
import jackals.utils.LogbackConfigurer;
import jackals.utils.SpringContextHolder;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by scott on 2015/7/6.
 */
public class ActiveMQSpider extends SpiderBase implements MessageListener {
    ActiveMQReceiver receiver;
    ActiveMQSender sender;
    String queueId;
    String broker ;

    public static void main(String[] args) {
        new LogbackConfigurer();
        new ClassPathXmlApplicationContext("/jar/config/spring/applicationContext.xml");
        new ActiveMQSpider(args[0]).start();
//        ActiveMQSender commonSender = new ActiveMQSender(broker);
//        commonSender.sendOne("job1", publishPrefix + "1");

    }
    public ActiveMQSpider(String spiderId) {
        Properties properties = SpringContextHolder.getBean("mqConfig");
        receiver = new ActiveMQReceiver(properties.getProperty("mq.amq.broker"));
        sender = new ActiveMQSender(properties.getProperty("mq.amq.broker"));
        this.queueId = Constants.TopicSpiderPrefix + spiderId;
//        super(broker, queueId);
    }



    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConcurrentHashMap<String, SpiderJob> spiders = new ConcurrentHashMap<String, SpiderJob>();


    public void onMessage(Message message) {
        try {
            logger.info("requestReceived spider_{} ", message);
            TextMessage textMessage = (TextMessage) message;
            requestReceived(textMessage.getText());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void sendSeed(JobInfo jobInfo) {
        List<RequestOjb> requestOjbList = new ArrayList<RequestOjb>();
        for (String s : jobInfo.getSeed()) {
            requestOjbList.add(new RequestOjb(s,true));
        }
        if (jobInfo.getJobModel() == Constants.JobModel.startJob) {
            sender.sendBatchRequest(Constants.TopicJobPrefix + jobInfo.getId(),
                    requestOjbList);
            logger.info("spider send seed [{}] {} ", Constants.TopicJobPrefix + jobInfo.getId(), requestOjbList);
        }
    }

    @Override
    protected SpiderJob createSpiderJob(JobInfo jobInfo) {
        return new ActiveMQSpiderJob(jobInfo);
    }

    @Override
    public void start() {
        logger.info("start "+queueId);
        receiver.addListener(queueId, this);
    }

    public void requestReceived(MessageAndMetadata<byte[], byte[]> mnm) throws IOException {

    }
}
