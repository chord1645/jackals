package com.shrek.crawler.test.tmp;

import com.shrek.crawler.test.BaseTest;
import jackals.Constants;
import jackals.job.JobManager;
import jackals.job.pojo.JobInfo;
import jackals.mq.kafka.KafkaSender;
import jackals.mq.kafka.TopicManager;
import jackals.simples.News163;
import org.junit.Test;

public class DeleteTopic extends BaseTest {

    JobManager jobManager = JobManager.create(new KafkaSender());

    @Test
    public void test() {
        //删除消息
        JobInfo jobInfo = News163.job();
        TopicManager.deleteTopic(Constants.TopicJobPrefix + jobInfo.getId());
    }

}