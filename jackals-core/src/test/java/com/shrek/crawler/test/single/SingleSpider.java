package com.shrek.crawler.test.single;

import com.alibaba.fastjson.JSON;
import jackals.URLFilter;
import jackals.allocation.Allocation;
import jackals.job.pojo.JobInfo;
import jackals.model.RequestOjb;
import jackals.page.PageProcess;
import jackals.utils.BlockExecutorPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 2016/10/17
 *
 * @author Scott Lee
 */
public class SingleSpider extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());


    protected PageProcess pageProcess;
    protected BlockExecutorPool executor;
    protected URLFilter urlFilter;
    //    private Integer num;
    protected JobInfo jobInfo;
    protected Allocation allocation;
    protected AtomicInteger executing = new AtomicInteger(0);

    public PageProcess getPageProcess() {
        return pageProcess;
    }

    public SingleSpider setPageProcess(PageProcess pageProcess) {
        this.pageProcess = pageProcess;
        return this;
    }

    public BlockExecutorPool getExecutor() {
        return executor;
    }

    public SingleSpider setExecutor(BlockExecutorPool executor) {
        this.executor = executor;
        return this;
    }

    public URLFilter getUrlFilter() {
        return urlFilter;
    }

    public SingleSpider setUrlFilter(URLFilter urlFilter) {
        this.urlFilter = urlFilter;
        return this;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    public SingleSpider setAllocation(Allocation allocation) {
        this.allocation = allocation;
        return this;
    }

    public SingleSpider(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
        List<RequestOjb> out = jobInfo.getSeed().stream().map(e -> {
            RequestOjb requestOjb = new RequestOjb(e);
            return requestOjb;
        }).collect(Collectors.toList());
        queue.addAll(out);
    }

    public JobInfo getJobInfo() {
        return jobInfo;
    }


    public void setJobInfo(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
    }

    public boolean isRunning() {
        return executor != null && executor.getThreadAlive() != 0;
    }

    LinkedBlockingQueue<RequestOjb> queue = new LinkedBlockingQueue<RequestOjb>();

    public void executeRequest(final RequestOjb link) {
        if (executor.isShutdown()) {
            executor = new BlockExecutorPool(jobInfo.getJobThreadNum());
        }
        executor.execute(new Runnable() {
            public void run() {
                try {
                    executing.incrementAndGet();
                    if (urlFilter.exist(jobInfo, link.getUrl())) {
                        logger.info("ignore :" + link.getUrl());
                        return;
                    } else {
                        urlFilter.add(jobInfo, link.getUrl());

                    }
                    long s = System.currentTimeMillis();
                    ArrayList<RequestOjb> list = pageProcess.process(link, jobInfo);
                    logger.info("process cost {} {}", (System.currentTimeMillis() - s), link.getUrl());
//                    s = System.currentTimeMillis();
//                    allocation.allocate(jobInfo, list);
//                    logger.info("allocate cost {} {}", (System.currentTimeMillis() - s), link.getUrl());
                    s = System.currentTimeMillis();
                    queue.addAll(list);
                    TimeUnit.MILLISECONDS.sleep(jobInfo.getSleep());//TODO 定制休眠
                    logger.info("sleep cost {} {}", (System.currentTimeMillis() - s), link.getUrl());
                } catch (Throwable e) {
                    logger.error("Executor Exception " + link.getUrl() + " > " + e.toString(), e);
                } finally {
                    if (executing.decrementAndGet() == 0) {
                        logger.info("job stoped {} {} {}", jobInfo.getId());
                    }
                }

            }
        });
    }


    //    @Override
    public void requestReceived(String message) {
        long s = System.currentTimeMillis();
//        logger.debug("requestReceived job_{} {} {}", this.getJobInfo().getId(), executing.incrementAndGet(), message);
        RequestOjb request = JSON.parseObject(message, RequestOjb.class);
        logger.debug("000000000000000000000000000000000000000000000000");
        executeRequest(request);
        logger.info("executeRequest cost {} {}", (System.currentTimeMillis() - s), request.getUrl());
    }

    public void start(boolean filterClean) {
        if (filterClean) {
            urlFilter.clean(jobInfo);
        }
        start();
    }

    @Override
    public void run() {
        RequestOjb requestOjb = null;
        try {
            while ((requestOjb = queue.poll(10, TimeUnit.MINUTES)) != null)
                executeRequest(requestOjb);
        } catch (InterruptedException e) {
            logger.error("run ex", e);
        }
        logger.info("job done");
    }

    public void cleanFilter() {
        urlFilter.clean(jobInfo);
    }


//    public static JobInfo job() {
//        //http://bbs.nga.cn/thread.php?fid=538&rand=356
//        JobInfo jobInfo = JobInfo.create("bbs.nga.cn");
//        jobInfo.setMaxDepth(1);
//        jobInfo.setJobThreadNum(5);
//        jobInfo.setSleep(200L);
//        jobInfo.setReset(true);
//        jobInfo.getSeed().add("http://bbs.nga.cn/thread.php?fid=459");
//        Orders orders = new Orders();
//        orders.setPathRegx("http://bbs.nga.cn/thread.php\\?fid=[0-9]+");
//        orders.setTargetRegx("^http://bbs.nga.cn/read.php\\?tid=[0-9]+.*$");
//        orders.setFields(ImmutableMap.of(
//                "title",
//                new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str)
//        ));
//        jobInfo.setOrders(orders);
//        return jobInfo;
//    }

}
