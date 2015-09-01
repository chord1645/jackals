package jackals.job;

import com.alibaba.fastjson.JSON;
import jackals.URLFilter;
import jackals.allocation.Allocation;
import jackals.job.pojo.JobInfo;
import jackals.model.RequestOjb;
import jackals.mq.MQListener;
import jackals.page.PageProcess;
import jackals.utils.BlockExecutorPool;
import jackals.utils.LogbackConfigurer;
import kafka.message.MessageAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by scott on 2015/7/6.
 */
abstract public class SpiderJob extends Thread implements MQListener {


    public static void main(String[] args) {
        new LogbackConfigurer();
    }

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

    public SpiderJob setPageProcess(PageProcess pageProcess) {
        this.pageProcess = pageProcess;
        return this;
    }

    public BlockExecutorPool getExecutor() {
        return executor;
    }

    public SpiderJob setExecutor(BlockExecutorPool executor) {
        this.executor = executor;
        return this;
    }

    public URLFilter getUrlFilter() {
        return urlFilter;
    }

    public SpiderJob setUrlFilter(URLFilter urlFilter) {
        this.urlFilter = urlFilter;
        return this;
    }

    public Allocation getAllocation() {
        return allocation;
    }

    public SpiderJob setAllocation(Allocation allocation) {
        this.allocation = allocation;
        return this;
    }

    public SpiderJob(JobInfo jobInfo) {
        this.jobInfo = jobInfo;
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

    public void executeRequest(final RequestOjb link) {
        if (executor.isShutdown()) {
            executor = new BlockExecutorPool(jobInfo.getJobThreadNum());
        }
        executor.execute(new Runnable() {
            @Override
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
                    s = System.currentTimeMillis();
                    allocation.allocate(jobInfo, list);
                    logger.info("allocate cost {} {}", (System.currentTimeMillis() - s), link.getUrl());
                    s = System.currentTimeMillis();
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

    abstract public void run();

    @Override
    abstract public void onReceived(MessageAndMetadata<byte[], byte[]> mnm);
//        long s = System.currentTimeMillis();
//        String key = mnm.key() == null ? null : new String(mnm.key());
//        logger.debug("partition {} {} {} {} ", mnm.topic(), mnm.partition(), key, new String(mnm.message()));
//        String msg = new String(mnm.message());
//        onReceived(msg);
//    }

    //    @Override
    public void onReceived(String message) {
        long s = System.currentTimeMillis();
//        logger.debug("onReceived job_{} {} {}", this.getJobInfo().getId(), executing.incrementAndGet(), message);
        RequestOjb request = JSON.parseObject(message, RequestOjb.class);
        logger.debug("000000000000000000000000000000000000000000000000");
        executeRequest(request);
        logger.info("executeRequest cost {} {}", (System.currentTimeMillis() - s), request.getUrl());
    }

    abstract public void shutdown();

    public void start(boolean filterClean) {
        if (filterClean) {
            urlFilter.clean(jobInfo);
        }
        start();
    }

    public void cleanFilter() {
        urlFilter.clean(jobInfo);
    }
}
