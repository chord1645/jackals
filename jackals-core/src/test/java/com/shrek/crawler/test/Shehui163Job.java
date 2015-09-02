package com.shrek.crawler.test;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import jackals.Constants;
import jackals.job.JobManager;
import jackals.job.pojo.JobInfo;
import jackals.mq.kafka.KafkaSender;
import jackals.mq.kafka.TopicManager;
import jackals.page.HtmlExtratorImpl;
import jackals.simples.News163;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;


public class Shehui163Job extends BaseTest {
    public static void main1(String[] args) {
        new ClassPathXmlApplicationContext("/jar/config/test/spring/test.xml");
        JobManager jobManager = new JobManager();
        JobInfo jobInfo = News163.job();
        jobManager.startJob(jobInfo,
                ImmutableList.of("1", "2", "3")
        );
//        new HtmlExtratorImpl().test(cnblogsJob(),"http://news.mtime.com/2015/08/12/1545670.html");
//        htmlExtrator.doExtrat()
    }

    JobManager jobManager = JobManager.create(new KafkaSender());

    //    JobManager jobManager = JobManager.create(new ActiveMQSender("tcp://localhost:61616"));
//    ActiveMQSender sender = new ActiveMQSender("tcp://localhost:61616");
    @Test
    public void start() throws IOException, SolrServerException {
//        SolrServer solrServer = SpringContextHolder.getBean(SolrServer.class);
//        solrServer.deleteByQuery("infoTime_dt:[2015-08-24T00:06:07.343Z TO *]");
//        solrServer.commit();
        JobInfo jobInfo = News163.job();
        jobManager.startJob(jobInfo,
//                ImmutableList.of("1")
                ImmutableList.of("10", "20", "30")
        );
    }

    @Test
    public void update() throws IOException, SolrServerException {
        JobInfo jobInfo = News163.job();
//        TopicManager.deleteTopic(Constants.TopicJobPrefix + jobInfo.getId());
        jobManager.update(jobInfo,
//                ImmutableList.of("1")
//                ImmutableList.of("10", "20", "30")
                ImmutableList.of("10")
        );
    }

    @Test
    public void addSpider() {
        JobInfo jobInfo = News163.job();

        jobInfo.setJobModel(Constants.JobModel.addSpider);
//        jobManager.addSpider(jobInfo, "1");
//        jobManager.addSpider(jobInfo, "10");
//        jobManager.addSpider(jobInfo, "20");
        jobManager.addSpider(jobInfo, "30");
    }

    @Test
    public void pushUrl() {
        JobInfo jobInfo = News163.job();
        jobManager.addUrl(jobInfo, "http://news.163.com/shehui/");
    }

    @Test
    public void stop() {
        JobInfo jobInfo = News163.job();
        TopicManager.deleteTopic(Constants.TopicJobPrefix + jobInfo.getId());
        jobManager.stopSpider(jobInfo,
                ImmutableList.of("10", "20", "30")
        );
    }

    @Test
    public void ExtratorImpl() {
        JobInfo jobInfo = News163.job();
        Object obj = new HtmlExtratorImpl()
                .test(jobInfo.getOrders(), "http://sports.163.com/15/0830/08/B28IREK100051C8V.html");

    }

    @Test
    public void testJson() {
        JobInfo jobInfo = News163.job();
        String s = JSON.toJSONString(jobInfo);
        JSON.parseObject(s, JobInfo.class);
    }


}