package com.shrek.crawler.test.jobtest;

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


public class PcgamesJob extends BaseTest {
    JobManager jobManager = new JobManager();

    @Test
    public void start() {
        JobInfo jobInfo = job();

        jobManager.restartJob(jobInfo,
                ImmutableList.of("10","20","30")
//                ImmutableList.of("30")
        );
    }

    @Test
    public void addSpider() {
        JobInfo jobInfo = job();

        jobInfo.setJobModel(Constants.JobModel.addSpider);
//        jobManager.addSpider(jobInfo, "1");
    }

    @Test
    public void stop() {
        JobInfo jobInfo = job();
        new HtmlExtratorImpl()
                .test(jobInfo.getOrders(), "http://sc2.pcgames.com.cn/539/5396143.html");

    }

    @Test
    public void pushUrl() {
        JobInfo jobInfo = job();
        jobManager.addUrl(jobInfo, "http://news.mtime.com/movie/2/index.html#nav");
    }

    public static JobInfo job() {
        JobInfo jobInfo = JobInfo.create("sc2.pcgames.com.cn");
        jobInfo.setMaxDepth(10);
        jobInfo.setJobThreadNum(5);
        jobInfo.setReset(true);
        jobInfo.getSeed().add("http://sc2.pcgames.com.cn/news/index.html");
        Orders orders = new Orders();
        orders.setPathRegx("http://sc2.pcgames.com.cn/news/index_\\d+.html");
        orders.setTargetRegx("http://sc2.pcgames.com.cn/\\d+/[\\d_]+.html");
//        orders.setFields(ImmutableMap.of(
//                "title",
//                new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str),
//                "html_css",
////                <div class="artArea" id="artArea">
//                new ExtratField("html_css", "(?is)<div\\s+class=\"artArea\"\\s+id=\"artArea\">(.+?)</div>", 1, Constants.FmtType.str)
////                <p class="mt15 ml25 newstime ">2015-08-12 15:44:03 	<span class="ml15">
////                "infoTime_dt",
////                new ExtratField("infoTime", "<p\\s*class=\".*newstime\\s*\">([^<]+).*</p>", 1, Constants.FmtType.date)
//        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }

}