package jackals.job;

import com.alibaba.fastjson.JSON;
import jackals.Constants;
import jackals.job.pojo.JobInfo;
import jackals.model.RequestOjb;
import jackals.mq.activemq.ActiveMQSender;
import jackals.mq.kafka.KafkaSender;
import jackals.mq.kafka.TopicManager;
import jackals.utils.SpringContextHolder;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Properties;

/**
 * Created by scott on 2015/7/6.
 */
public class AmqManager {
    ActiveMQSender commonSender;

    public AmqManager() {
        Properties mqConfig = SpringContextHolder.getBean("mqConfig");
        this.commonSender = new ActiveMQSender(mqConfig.getProperty("mq.amq.broker"));
    }

    public void startJob(JobInfo jobInfo, List<String> spiders) {

        jobInfo.setJobModel(Constants.JobModel.startJob);
        String json = JSON.toJSONString(jobInfo);
        if (!CollectionUtils.isEmpty(spiders)) {
            for (String spiderId : spiders) {
                commonSender.sendOne(Constants.TopicSpiderPrefix + spiderId, json);
            }
        }

    }

    public void restartJob(JobInfo jobInfo, List<String> spiders) {
        TopicManager.deleteTopic(Constants.TopicJobPrefix + jobInfo.getId());
        jobInfo.setJobModel(Constants.JobModel.startJob);
        jobInfo.setReset(true);
        String json = JSON.toJSONString(jobInfo);
        if (!CollectionUtils.isEmpty(spiders)) {
            for (String spiderId : spiders) {
                commonSender.sendOne(Constants.TopicSpiderPrefix + spiderId, json);
            }
        }

    }

    public void addSpider(JobInfo jobInfo, String spiderId) {
        jobInfo.setJobModel(Constants.JobModel.addSpider);
        String json = JSON.toJSONString(jobInfo);
        commonSender.sendOne(Constants.TopicSpiderPrefix + spiderId, json);
    }

    public void addUrl(JobInfo jobInfo, String s) {
        commonSender.sendOne(Constants.TopicJobPrefix + jobInfo.getId(),
                JSON.toJSONString(new RequestOjb(s)));
    }

    public void cleanJobQueue(JobInfo jobInfo) {
        TopicManager.deleteTopic(Constants.TopicJobPrefix + jobInfo.getId());
    }

    public void stopSpider(JobInfo jobInfo, List<String> spiders) {
        jobInfo.setJobModel(Constants.JobModel.stopJob);
        String json = JSON.toJSONString(jobInfo);
        if (!CollectionUtils.isEmpty(spiders)) {
            for (String spiderId : spiders) {
                commonSender.sendOne(Constants.TopicSpiderPrefix + spiderId, json);
            }
        }
    }
}