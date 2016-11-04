package com.shrek.crawler.test.jobtest;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.shrek.crawler.test.BaseTest;
import jackals.Constants;
import jackals.job.JobManager;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.page.HtmlExtratorImpl;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;


public class Shehui163Job extends BaseTest {
    public static void main1(String[] args) {
        new ClassPathXmlApplicationContext("/jar/config/test/spring/test.xml");
        JobManager jobManager = new JobManager();
        JobInfo jobInfo = job();
        jobManager.startJob(jobInfo,
                ImmutableList.of("1", "2", "3")
        );
//        new HtmlExtratorImpl().test(cnblogsJob(),"http://news.mtime.com/2015/08/12/1545670.html");
//        htmlExtrator.doExtrat()
    }

    JobManager jobManager = new JobManager();

    @Test
    public void start()   {
        while (true) {
            JobInfo jobInfo = job();

//        jobManager.restartJob(jobInfo,
//                ImmutableList.of("1")
//        );
            jobManager.restartJob(jobInfo,
                    ImmutableList.of("10", "20", "30")
            );
            try {
                TimeUnit.SECONDS.sleep(60*5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void addSpider() {
        JobInfo jobInfo = job();

        jobInfo.setJobModel(Constants.JobModel.addSpider);
//        jobManager.addSpider(jobInfo, "1");
        jobManager.addSpider(jobInfo, "10");
        jobManager.addSpider(jobInfo, "20");
        jobManager.addSpider(jobInfo, "30");
    }

    @Test
    public void pushUrl() {
        JobInfo jobInfo = job();
        jobManager.addUrl(jobInfo, "http://news.163.com/shehui/");
    }

    @Test
    public void stop() {
        JobInfo jobInfo = job();

        jobManager.stopSpider(jobInfo,
//                ImmutableList.of("10","20","30"),
                ImmutableList.of("1")
        );
    }

    @Test
    public void ExtratorImpl() {
        JobInfo jobInfo = job();
        new HtmlExtratorImpl()
                .test(jobInfo.getOrders(), "http://news.163.com/15/0813/17/B0TS561Q0001124J.html#f=slist");

    }
@Test
    public void testJson() {
        JobInfo jobInfo = job();
        String s = JSON.toJSONString(jobInfo);
        JSON.parseObject(s, JobInfo.class);
    }

    public static JobInfo job() {
        JobInfo jobInfo = JobInfo.create("news.163.com");
        jobInfo.setMaxDepth(1);
        jobInfo.setJobThreadNum(1);
        jobInfo.setSleep(100L);
        jobInfo.setReset(true);
        jobInfo.getSeed().add("http://news.163.com/shehui/");
//        jobInfo.getSeed().add("http://news.163.com/15/0813/06/B0SJM5D100011229.html");
        Orders orders = new Orders();
        orders.setPathRegx("http://news.163.com/special/\\d+/shehuinews_\\d+.html.*");
        orders.setTargetRegx("http://news.163.com/15/\\d+/\\d+/\\w+.html.*");
//        orders.setFields(ImmutableMap.of(
//                "title",
//                new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str),
////                "html", new ExtratField("html", ".*(<body>.*?</body>).*", 1),
//                //<p class="mt15 ml25 newstime ">2015-08-12 15:44:03 	<span class="ml15">
//                "infoTime_dt",
//                new ExtratField("infoTime", "(?is)<div\\s*class=\"ep-time-soure cDGray\">(.+)[\\　\\s]*来源.*</div>", 1, Constants.FmtType.date)
//        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }

}