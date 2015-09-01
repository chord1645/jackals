package jackals.job;

import jackals.Constants;
import jackals.job.pojo.JobInfo;
import jackals.mq.kafka.KafkaReceiver;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by scott on 2015/7/6.
 */
public class KafkaSpiderJob extends SpiderJob {
    private Logger logger = LoggerFactory.getLogger(getClass());
    KafkaReceiver kafkaReceiver;
//    ConsumerConnector consumer;


    public KafkaSpiderJob(final JobInfo jobInfo) {
        super(jobInfo);
//        allocation = new KafkaAllocationImpl();
//        executor = new BlockExecutorPool(jobInfo.getJobThreadNum());
//        pageProcess = new DefaultPageProcessImpl();
//        urlFilter = new URLRedisFilter();
//        urlFilter.clean(jobInfo);
    }

   /* @Override
    public void onReceived(String message) {
        Request r = JSON.parseObject(new String(message), Request.class);
        executeRequest(r);
    }*/

    @Override
    public void run() {
        kafkaReceiver = new KafkaReceiver();
        kafkaReceiver.startListen(Constants.TopicJobPrefix + jobInfo.getId(), this);
    }

    @Override
    public void onReceived(MessageAndMetadata<byte[], byte[]> mnm) {
        String key = mnm.key() == null ? null : new String(mnm.key());
        logger.debug("partition {} {} {} {} ", mnm.topic(), mnm.partition(), key, new String(mnm.message()));
        String msg = new String(mnm.message());
        onReceived(msg);

    }

    @Override
    public void shutdown() {
        kafkaReceiver.storpListen();
        executor.shutdown();
        logger.info("shutdown shutdown----1");
        interrupt();
        logger.info("shutdown shutdown----2");
    }
}
