package jackals.simples;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jackals.Constants;
import jackals.job.JobManager;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.page.HtmlExtratorImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TencentNews {
    public static void main(String[] args) {
        JobInfo jobInfo = job();
        Object obj = new HtmlExtratorImpl()
                .test(jobInfo.getOrders(), "http://news.qq.com/a/20150825/047401.htm");
    }

    public static JobInfo job() {
        //http://www.cnblogs.com/likehua/archive/2015/06.html
        JobInfo jobInfo = JobInfo.create("news.qq.com");
        jobInfo.setMaxDepth(2);
        jobInfo.getSeed().add("http://news.qq.com/newsgn/gdxw/gedixinwen.htm");
        Orders orders = new Orders();
        orders.setPathRegx("(^http://\\w+.\\w+.qq.com/*$)|(^http://\\w+.qq.com/([a-zA-Z]+/)*\\w+.s*html*$)");
        orders.setTargetRegx("http://news.qq.com/a/\\d+/\\d+.htm");
        ExtratField dateField = new ExtratField("infoTime_dt",
                "(?is)<span\\s*class=\"article-time\"\\s*>\\s*(.+?)\\s*</span>", 1,
                Constants.FmtType.date);
        dateField.setFmtStr("yyyy-MM-dd HH:mm");
//        orders.setFields(ImmutableMap.of(
//                "title",
//                new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str),
//                "infoTime_dt",
//                dateField
//        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }

}