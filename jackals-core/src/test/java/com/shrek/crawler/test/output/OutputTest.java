package com.shrek.crawler.test.output;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.shrek.crawler.test.BaseTest;
import com.shrek.crawler.test.Shehui163Job;
import jackals.Constants;
import jackals.job.JobManager;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.mq.kafka.KafkaSender;
import jackals.output.OutputPipe;
import jackals.output.solr.SolrOutputPipe;
import jackals.page.HtmlExtratorImpl;
import jackals.simples.News163;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;


public class OutputTest extends BaseTest {

    @Test
    public void start() throws Exception {
        OutputPipe outputPipe = new SolrOutputPipe();
        JobInfo jobInfo = News163.job();
        String url = "http://news.163.com/15/0824/02/B1OISJ3A00014AED.html?f=jsearch";
        PageObj pageObj = new PageObj(new RequestOjb(url));
        Object obj = new HtmlExtratorImpl()
                .test(jobInfo.getOrders(), url);
        outputPipe.save(jobInfo, pageObj, obj);
    }

}