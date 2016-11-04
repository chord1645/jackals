package com.shrek.crawler.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jackals.Constants;
import jackals.job.JobManager;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.page.HtmlExtratorImpl;
import org.junit.Test;


public class XiachufangJob extends BaseTest {
    JobManager jobManager = new JobManager();

    @Test
    public void start() {
        JobInfo jobInfo = job();
        jobManager.restartJob(jobInfo,
                ImmutableList.of("10", "20", "30")
        );
    }

    @Test
    public void ExtratorImpl() {
        JobInfo jobInfo = job();
        new HtmlExtratorImpl()
                .test(jobInfo.getOrders(), "http://www.xiachufang.com/recipe/100350106/");

    }

    public static JobInfo job() {
        JobInfo jobInfo = JobInfo.create("www.xiachufang.com");
        jobInfo.setMaxDepth(10);
        jobInfo.setJobThreadNum(5);
        jobInfo.setSleep(200L);
        jobInfo.setReset(true);
        jobInfo.getSeed().add("http://www.xiachufang.com/");
        Orders orders = new Orders();
        orders.setPathRegx("http://www.xiachufang.com/category/\\d+/$");
        orders.setTargetRegx("http://www.xiachufang.com/recipe/\\d+/$");
//        orders.setFields(ImmutableMap.of(
//                "title", new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str),
////                <span class="number" itemprop="ratingValue">7.5</span>
//                "rating_d", new ExtratField("rating_d", "(?is)<span class=\"number\" itemprop=\"ratingValue\">([0-9.]+)</span>", 1, Constants.FmtType.str),
//                "pop_i", new ExtratField("pop_i", "(?is)<span class=\"number\">(\\d+)</span>", 1, Constants.FmtType.str)
//
//        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }

}