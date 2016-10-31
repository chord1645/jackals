package com.shrek.crawler.test.single;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.shrek.crawler.test.BaseTest;
import jackals.Constants;
import jackals.allocation.KafkaAllocationImpl;
import jackals.job.JobManager;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.model.PageObj;
import jackals.output.OneFileOutputPipe;
import jackals.page.DefaultPageProcessImpl;
import jackals.page.HtmlExtratorImpl;
import jackals.utils.BlockExecutorPool;
import jackals.utils.LogbackConfigurer;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
        jobInfo.setJobThreadNum(1);
        jobInfo.setSleep(200L);
        jobInfo.setReset(true);
//        jobInfo.getSeed().add("http://www.haodf.com/hospital/DE4r0Fy0C9LuwWCOYx29oa1OdBHBTXzVa.htm");
        jobInfo.getSeed().add("http://www.haodf.com/yiyuan/hebei/list.htm");
        Orders orders = new Orders();
        orders.setPathRegx("http://www.haodf.com/yiyuan/hebei/list.htm");
        orders.setTargetRegx("^http://www.haodf.com/hospital/.+\\.htm$");
        orders.setFields(ImmutableMap.of(
                "title",
                new ExtratField("title", "<title>([^_]+).*</title>", 1, Constants.FmtType.str)
        ));
        jobInfo.setOrders(orders);
        jobInfo.setValid(new JobInfo.Valid() {
            @Override
            public boolean success(PageObj page) {
                return page.getStatusCode() == 200 && page.getRawText().contains("好大夫");
            }
        });
        return jobInfo;
    }

    @Test
    public void page() {
        JobInfo jobInfo = haodaifu();
        Object obj = new HtmlExtratorImpl()
                .testProxy(jobInfo.getOrders(), "http://www.haodf.com/yiyuan/hebei/list.htm");
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
}