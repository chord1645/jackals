package jackals.job;

import com.alibaba.fastjson.JSON;
import jackals.Constants;
import jackals.URLRedisFilter;
import jackals.allocation.AmqAllocation;
import jackals.mq.MQListener;
import jackals.job.pojo.JobInfo;
import jackals.model.RequestOjb;
import jackals.mq.activemq.ActiveMQReceiver;
import jackals.page.DefaultPageProcessImpl;
import jackals.utils.BlockExecutorPool;
import jackals.utils.LogbackConfigurer;
import jackals.utils.SpringContextHolder;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by scott on 2015/7/6.
 */
public class ActiveMQSpiderJob extends SpiderJob {

    public static void main(String[] args) {

        new LogbackConfigurer();
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    Properties mqConfig;
    public ActiveMQSpiderJob(JobInfo job) {
        super(job);
        mqConfig = SpringContextHolder.getBean("mqConfig");
        executor = new BlockExecutorPool(job.getJobThreadNum());
        pageProcess = new DefaultPageProcessImpl(job.getJobThreadNum());
        allocation = new AmqAllocation();
        executor = new BlockExecutorPool(jobInfo.getJobThreadNum());
        urlFilter = new URLRedisFilter();
        //        new KafkaSpiderJob(jobInfo)
//                .setPageProcess(new DefaultPageProcessImpl(jobInfo.getJobThreadNum()))
//                .setAllocation(new KafkaAllocationImpl())
//                .setExecutor(new BlockExecutorPool(jobInfo.getJobThreadNum()))
//                .setUrlFilter(new URLRedisFilter());
//        commonSender = new ActiveMQSender(broker);

    }

    class JobReceiver extends ActiveMQReceiver {
        MQListener callback;

        public JobReceiver(String broker, String queueId, MQListener callback) {
            super(broker, queueId);
            this.callback = callback;
            logger.info("job listen "+queueId);
        }

        @Override
        public void onMessage(Message message) {
            try {
                //取得监听的任务ID
                logger.info("onReceived job_{} ", message);
                TextMessage textMessage = (TextMessage) message;
                callback.onReceived(textMessage.getText());
            } catch (JMSException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        new JobReceiver(mqConfig.getProperty("mq.amq.broker"),  Constants.TopicJobPrefix + jobInfo.getId(), this);
    }

//    @Override
//    public void onReceived(Message message) {
//        try {
//            logger.info("onReceived spider_{} {}", this.num, message);
//            TextMessage textMessage = (TextMessage) message;
//            Request request = JSON.parseObject(textMessage.getText(), Request.class);
//            startListen(request);
//        } catch (JMSException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onReceived(String message) {
        logger.info("onReceived job_{} {}", this.getJobInfo().getId(), message);
//            logger.info(objectMessage.toString());
        RequestOjb request = JSON.parseObject(message, RequestOjb.class);
        executeRequest(request);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        interrupt();
    }

    @Override
    public void onReceived(MessageAndMetadata<byte[], byte[]> mnm) {

    }
}
