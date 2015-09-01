package jackals.simples;

import com.google.common.collect.ImmutableMap;
import jackals.Constants;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.page.HtmlExtratorImpl;


public class IfengNews {
    public static void main(String[] args) {
        JobInfo jobInfo = job();
        Object obj = new HtmlExtratorImpl()
                .test(jobInfo.getOrders(), "http://news.ifeng.com/listpage/11502/0/1/rtlist.shtml");
    }

    public static JobInfo job() {
        JobInfo jobInfo = JobInfo.create("news.ifeng.com");
        jobInfo.setMaxDepth(2);
        jobInfo.getSeed().add("http://news.ifeng.com/listpage/11502/0/1/rtlist.shtml");
        Orders orders = new Orders();
        orders.setPathRegx("(^http://\\w+.ifeng.com/*$)|(http://\\w+.ifeng.com/\\w+/)");
//        http://news.ifeng.com/a/20150827/44529280_0.shtml
        orders.setTargetRegx("http://news.ifeng.com/\\w+/\\d+/[0-9_]+.(shtml|html|htm)");

//        <span itemprop="datePublished" class="ss01">2015年08月27日 11:58</span><br />
        ExtratField dateField = new ExtratField("infoTime_dt",
                "(?is)<span[^>]*?itemprop=\"datePublished\"[^>]*?>\\s*(.+?)\\s*</span>", 1,
                Constants.FmtType.date);
        dateField.setFmtStr("yyyy'年'MM'月'dd'日' HH:mm");
        orders.setFields(ImmutableMap.of(
                "title",
                new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str),
                "infoTime_dt",
                dateField
        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }

}