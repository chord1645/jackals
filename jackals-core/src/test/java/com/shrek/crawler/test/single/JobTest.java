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
import jackals.output.OneFileOutputPipe;
import jackals.page.DefaultPageProcessImpl;
import jackals.page.HtmlExtratorImpl;
import jackals.utils.BlockExecutorPool;
import jackals.utils.LogbackConfigurer;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class JobTest extends BaseTest {
    public static JobInfo haodaifu() {
        //http://bbs.nga.cn/thread.php?fid=538&rand=356
        JobInfo jobInfo = JobInfo.create("www.haodf.com");
        jobInfo.setMaxDepth(2);
        jobInfo.setJobThreadNum(5);
        jobInfo.setSleep(200L);
        jobInfo.setReset(true);
//        jobInfo.getSeed().add("http://www.haodf.com/hospital/DE4r0Fy0C9LuwWCOYx29oa1OdBHBTXzVa.htm");
        jobInfo.getSeed().add("http://www.haodf.com/yiyuan/beijing/list.htm");
        Orders orders = new Orders();
        orders.setPathRegx("http://www.haodf.com/yiyuan/\\w+/list.htm");
        orders.setTargetRegx("^http://www.haodf.com/hospital/\\w+\\.htm$");
        orders.setFields(ImmutableMap.of(
                "title",
                new ExtratField("title", "<title>([^_]+).*</title>", 1, Constants.FmtType.str)
        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }
    @Test
    public void page() {
        JobInfo jobInfo = haodaifu();
        Object obj = new HtmlExtratorImpl()
                .test(jobInfo.getOrders(), "http://www.haodf.com/hospital/DE4r0Fy0C9LuwWCOYx29oa1OdBHBTXzVa.htm");
    }

    @Test
    public void start() {
        new LogbackConfigurer();
        new ClassPathXmlApplicationContext("/jar/config/test/spring/test.xml");
        JobInfo jobInfo = haodaifu();
        DefaultPageProcessImpl pageProces= new DefaultPageProcessImpl(jobInfo.getJobThreadNum());
        pageProces.setOutputPipe(new OneFileOutputPipe());
        new SingleSpider(jobInfo)
                .setPageProcess(pageProces)
                .setAllocation(new KafkaAllocationImpl())
                .setExecutor(new BlockExecutorPool(jobInfo.getJobThreadNum()))
                .setUrlFilter(new MemoryFilter())
                .start();
    }
}