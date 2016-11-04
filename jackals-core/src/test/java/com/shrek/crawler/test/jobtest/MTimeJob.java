package com.shrek.crawler.test.jobtest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.shrek.crawler.test.BaseTest;
import jackals.Constants;
import jackals.job.JobManager;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class MTimeJob extends BaseTest {
    public static void main1(String[] args) {
        new ClassPathXmlApplicationContext("/jar/config/test/spring/test.xml");
        JobManager jobManager = new JobManager();
        JobInfo jobInfo = job();
        jobManager.startJob(jobInfo,
                ImmutableList.of("1","2","3")
        );
//        new HtmlExtratorImpl().test(cnblogsJob(),"http://news.mtime.com/2015/08/12/1545670.html");
//        htmlExtrator.doExtrat()
    }

    JobManager jobManager = new JobManager();

    @Test
    public void start() {
        JobInfo jobInfo = job();

        jobManager.restartJob(jobInfo,
//                ImmutableList.of("10","20","30"),
                ImmutableList.of("1")
        );
    }

    @Test
    public void addSpider() {
        JobInfo jobInfo = job();

        jobInfo.setJobModel(Constants.JobModel.addSpider);
//        jobManager.addSpider(jobInfo, "1");
        jobManager.addSpider(jobInfo, "1");
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
    public void pushUrl() {
        JobInfo jobInfo = job();
        jobManager.addUrl(jobInfo, "http://news.mtime.com/movie/2/index.html#nav");
    }

    public static JobInfo job() {
        JobInfo jobInfo = JobInfo.create("news.mtime.com_1006");
        jobInfo.setMaxDepth(3);
        jobInfo.setJobThreadNum(1);
        jobInfo.setReset(false);
        jobInfo.getSeed().add("http://news.mtime.com/movie/2/index.html#nav");
        Orders orders = new Orders();
        orders.setPathRegx("http://news.mtime.com/movie/2/index.html(#nav)*");
        orders.setTargetRegx("http://news.mtime.com/2015/\\d+/\\d+/\\d+.html");
//        orders.setFields(ImmutableMap.of(
//                "title",
//                new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str),
////                "html", new ExtratField("html", ".*(<body>.*?</body>).*", 1),
//                //<p class="mt15 ml25 newstime ">2015-08-12 15:44:03 	<span class="ml15">
//                "infoTime_dt",
//                new ExtratField("infoTime", "<p\\s*class=\".*newstime\\s*\">([^<]+).*</p>", 1, Constants.FmtType.date)
//
//        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }

}