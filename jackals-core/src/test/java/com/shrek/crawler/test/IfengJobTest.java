package com.shrek.crawler.test;

import com.google.common.collect.ImmutableList;
import jackals.job.JobManager;
import jackals.job.pojo.JobInfo;
import jackals.mq.kafka.KafkaSender;
import jackals.page.HtmlExtratorImpl;
import jackals.simples.IfengNews;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import java.io.IOException;


public class IfengJobTest extends BaseTest {

    JobManager jobManager = JobManager.create(new KafkaSender());

    @Test
    public void test() {
        JobInfo jobInfo = IfengNews.job();
        Object obj = new HtmlExtratorImpl().test(
                jobInfo.getOrders(), "http://news.ifeng.com/a/20150827/44529280_0.shtml");

    }

    @Test
    public void update() throws IOException, SolrServerException {
        JobInfo jobInfo = IfengNews.job();
        jobManager.update(jobInfo,
//                ImmutableList.of("1", "2")
                ImmutableList.of("10", "30")
        );
    }

}