package jackals.bin;

import com.alibaba.fastjson.JSON;
import jackals.Constants;
import jackals.job.SpiderJob;
import jackals.job.pojo.JobInfo;
import jackals.mq.MQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by scott on 2015/7/6.
 */
abstract public class SpiderBase implements MQListener {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private ConcurrentHashMap<String, SpiderJob> jobMap = new ConcurrentHashMap<String, SpiderJob>();

    public static void main(String[] args) {

        System.out.println("###################################################");

    }

    /**
     * 删除或新增任务监听
     *
     * @param message
     */
    @Override
    synchronized public void onReceived(String message) throws IOException {
//        ThreadLocal<KafkaSpiderJob> threadLocal = new ThreadLocal<KafkaSpiderJob>();
        //取得监听的任务ID
        logger.info("onReceived spider_{} ", message);
//        ObjectMapper mapper = new ObjectMapper();
//        JobInfo jobInfo = mapper.readValue(message, JobInfo.class);
        JobInfo jobInfo = JSON.parseObject(message, JobInfo.class);
        //启动线程
        SpiderJob spider = jobMap.get(jobInfo.getId());
        if (spider == null) {
            if (Constants.JobModel.stopJob == jobInfo.getJobModel())
                return;
            spider = createSpiderJob(jobInfo);
            spider.start(jobInfo.getReset());
            jobMap.put(jobInfo.getId(), spider);
            logger.info("spider [{}] stand ready", jobInfo.getId());
            switch (jobInfo.getJobModel()) {
                case Constants.JobModel.addSpider:
                    break;
                case Constants.JobModel.update:
                    spider.cleanFilter();
                    sendSeed(jobInfo);
                    break;
                case Constants.JobModel.startJob:
                    sendSeed(jobInfo);
                    break;
                default:
                    break;
            }
        } else {
            switch (jobInfo.getJobModel()) {
                case Constants.JobModel.update:
                    logger.info("spider [{}] update", jobInfo.getId());
                    spider.setJobInfo(jobInfo);
                    spider.cleanFilter();
                    sendSeed(jobInfo);
                    break;
                case Constants.JobModel.stopJob:
                    logger.info("spider [{}] shutdown", jobInfo.getId());
                    spider.shutdown();
                    jobMap.remove(jobInfo.getId());
                    break;
                default:
                    break;
            }
        }
    }

    protected abstract void sendSeed(JobInfo jobInfo);

    protected abstract SpiderJob createSpiderJob(JobInfo jobInfo);

    protected abstract void start();

}
