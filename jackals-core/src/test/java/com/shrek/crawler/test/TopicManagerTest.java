package com.shrek.crawler.test;

import jackals.mq.kafka.TopicManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TopicManagerTest extends BaseTest {
    private static Logger logger = LoggerFactory.getLogger(TopicManagerTest.class);

    @Test
    public void main() {
        TopicManager.deleteTopic("listener_job_all.163.com");
        TopicManager.allTopics();

    }


}