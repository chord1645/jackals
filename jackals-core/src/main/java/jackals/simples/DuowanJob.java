package jackals.simples;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jackals.Constants;
import jackals.job.JobManager;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class DuowanJob extends JobInfo {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("/jar/config/test/spring/test.xml");
        JobManager jobManager = new JobManager();
        JobInfo jobInfo = cnblogsJob();
        jobManager.startJob(jobInfo,
                ImmutableList.of("1")
        );
    }

    public DuowanJob(String id) {
        super(id);
    }
    public static JobInfo cnblogsJob() {
        //http://www.cnblogs.com/likehua/archive/2015/06.html
        JobInfo jobInfo = JobInfo.create("www.cnblogs.com");
        jobInfo.setMaxDepth(1);
        jobInfo.getSeed().add("http://www.cnblogs.com/zhuyaguang/archive/2015/08/04.html");
        Orders orders = new Orders();
        orders.setPathRegx("http://www.cnblogs.com/zhuyaguang/archive/2015/08/04.html");
        orders.setTargetRegx("http://www.cnblogs.com/zhuyaguang/p/\\d+.html");
        orders.setFields(ImmutableMap.of(
                "title",
                new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str),
                "content",
                new ExtratField("content", "(?is)<div[^>]*id=\"cnblogs_post_body\"[^>]*>(.*)</div>", 1,Constants.FmtType.str)
        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }

}