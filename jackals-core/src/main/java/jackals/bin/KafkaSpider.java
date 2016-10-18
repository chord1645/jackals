package jackals.bin;

import jackals.Constants;
import jackals.URLRedisFilter;
import jackals.allocation.KafkaAllocationImpl;
import jackals.job.pojo.JobInfo;
import jackals.job.KafkaSpiderJob;
import jackals.job.SpiderJob;
import jackals.model.RequestOjb;
import jackals.mq.kafka.KafkaReceiver;
import jackals.mq.kafka.KafkaSender;
import jackals.page.DefaultPageProcessImpl;
import jackals.utils.BlockExecutorPool;
import jackals.utils.LogbackConfigurer;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by scott on 2015/7/6.
 */
public class KafkaSpider extends SpiderBase {
    public static void main(String[] args) {
        new LogbackConfigurer();
        new ClassPathXmlApplicationContext("/jar/config/spring/applicationContext.xml");
        new KafkaSpider(args[0]).start();
        System.out.println("###################################################");

    }

    public KafkaSpider(String spiderId) {
        this.spiderId = spiderId;
    }


    private Logger logger = LoggerFactory.getLogger(getClass());
    KafkaSender kafkaSender = new KafkaSender();
    KafkaReceiver kafkaReceiver = new KafkaReceiver();
    String spiderId;

    @Override
    public void sendSeed(JobInfo jobInfo) {
        List<RequestOjb> requestOjbList = new ArrayList<RequestOjb>();
        for (String s : jobInfo.getSeed()) {
            requestOjbList.add(new RequestOjb(s));
        }
        if (jobInfo.getJobModel() == Constants.JobModel.startJob
                ||jobInfo.getJobModel() == Constants.JobModel.update) {
            kafkaSender.sendBatchRequest(Constants.TopicJobPrefix + jobInfo.getId(),
                    requestOjbList);
            logger.info("spider send seed [{}] {} ", Constants.TopicJobPrefix + jobInfo.getId(), requestOjbList);
        }
    }

    @Override
    public SpiderJob createSpiderJob(JobInfo jobInfo) {
        return new KafkaSpiderJob(jobInfo)
                .setPageProcess(new DefaultPageProcessImpl(jobInfo.getJobThreadNum()))
                .setAllocation(new KafkaAllocationImpl())
                .setExecutor(new BlockExecutorPool(jobInfo.getJobThreadNum()))
                .setUrlFilter(new URLRedisFilter());
    }

    @Override
    public void start() {
        kafkaReceiver.startListen(Constants.TopicSpiderPrefix + spiderId, this);
    }

    public void requestReceived(MessageAndMetadata<byte[], byte[]> mnm) throws IOException {
        logger.info("requestReceived spider_{}", mnm);
        requestReceived(new String(mnm.message(), "utf-8"));
//        requestReceived(mnm.message());
    }
}
