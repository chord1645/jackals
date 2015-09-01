package com.shrek.crawler.test;

import com.google.common.collect.ImmutableList;
import jackals.job.JobManager;
import jackals.job.pojo.JobInfo;
import jackals.mq.kafka.KafkaSender;
import jackals.page.HtmlExtratorImpl;
import jackals.simples.TencentNews;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;

import java.io.IOException;


public class TencentJobTest extends BaseTest {

    JobManager jobManager = JobManager.create(new KafkaSender());

    @Test
    public void ExtratorImpl() {
        JobInfo jobInfo = TencentNews.job();
        Object obj = new HtmlExtratorImpl().test(
                jobInfo.getOrders(), "http://news.qq.com//");

    }

    @Test
    public void start() throws IOException, SolrServerException {
        JobInfo jobInfo = TencentNews.job();
        jobManager.update(jobInfo,
                ImmutableList.of("10")
//                ImmutableList.of("10", "20", "30")
        );
    }

}