package com.shrek.crawler.test.single;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.shrek.crawler.test.BaseTest;
import jackals.Constants;
import jackals.downloader.ProxyPool;
import jackals.downloader.Valid;
import jackals.filter.MemoryFilter;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.model.PageObj;
import jackals.output.OneFileOutputPipe;
import jackals.page.DefaultPageProcessImpl;
import jackals.page.HtmlExtratorImpl;
import jackals.single.SingleSpider;
import jackals.utils.BlockExecutorPool;
import jackals.utils.LogbackConfigurer;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


public class JobTest extends BaseTest {
    class HaodaifuJob extends JobInfo {
        @Override
        public boolean useful(PageObj pageObj) {
            return false;
        }
    }

    public static JobInfo haodaifu() {
        //http://bbs.nga.cn/thread.php?fid=538&rand=356
        JobInfo jobInfo = JobInfo.create("www.haodf.com");
        jobInfo.setMaxDepth(1);
        jobInfo.setJobThreadNum(3);
        jobInfo.setSleep(200L);
        jobInfo.setReset(true);
//        jobInfo.getSeed().add("http://www.haodf.com/hospital/DE4r0Fy0C9LuwWCOYx29oa1OdBHBTXzVa.htm");
//        jobInfo.getSeed().add("http://www.haodf.com/yiyuan/hebei/list.htm");
        jobInfo.getSeed().add("http://www.haodf.com/yiyuan/beijing/list.htm");
        Orders orders = new Orders();
        orders.setPathRegx("http://www.haodf.com/yiyuan/.*?/list.htm");
        orders.setTargetRegx("^http://www.haodf.com/hospital/.+\\.htm$");
        orders.setFields(Lists.newArrayList(
                new ExtratField("name", "\"keywords\" content=\"(.*?),", 1, Constants.FmtType.str),
                new ExtratField("address", "地址:(.*?);", 1, Constants.FmtType.str)
        ));
//        orders.setFields(ImmutableMap.of(
////                "title",
////                new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str),
//                "name",
//                new ExtratField("name", "\"keywords\" content=\"(.*?),", 1, Constants.FmtType.str),
//                "address",
//                new ExtratField("address", "地址:(.*?);", 1, Constants.FmtType.str)
////                "source",
////                new ExtratField("source", "(.*?)", 1, Constants.FmtType.str)
//        ));
        jobInfo.setOrders(orders);
        jobInfo.setValid(new Valid() {
            @Override
            public boolean success(PageObj page) {
                return page.getStatusCode() == 200 && !page.getRawText().contains("404啦-页面没找哦");
            }
        });
        return jobInfo;
    }

    @Test
    public void page() {
        JobInfo jobInfo = haodaifu();
        Object obj = new HtmlExtratorImpl()
                .testProxy(jobInfo.getOrders(), "http://www.haodf.com/hospital/339-maonangyan-servicestar.htm");
//        Object obj = new HtmlExtratorImpl()
//                .test(jobInfo.getOrders(), "http://www.haodf.com/yiyuan/hebei/list.htm");
    }

    @Test
    public void youtube() {
        JobInfo jobInfo = haodaifu();
        Object obj = new HtmlExtratorImpl()
                .test(jobInfo.getOrders(), "http://www.google.com");
    }

    @Test
    public void start() throws InterruptedException {
        new LogbackConfigurer();
        JobInfo jobInfo = haodaifu();
        DefaultPageProcessImpl pageProces = new DefaultPageProcessImpl(jobInfo.getJobThreadNum());
        pageProces.setOutputPipe(new OneFileOutputPipe());
        new SingleSpider(jobInfo)
                .setPageProcess(pageProces)
                .setExecutor(new BlockExecutorPool(jobInfo.getJobThreadNum()))
                .setUrlFilter(new MemoryFilter())
                .run();
        System.out.println("======================================");
//        TimeUnit.MINUTES.sleep(30);
    }

    @Test
    public void proxyPool() throws InterruptedException {
        new ProxyPool();
        TimeUnit.MINUTES.sleep(30);
    }


}