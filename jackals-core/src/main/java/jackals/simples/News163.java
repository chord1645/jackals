package jackals.simples;

import com.google.common.collect.ImmutableMap;
import jackals.Constants;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;


public class News163 {
    public static JobInfo job() {
        JobInfo jobInfo = JobInfo.create("all.163.com");
        jobInfo.setMaxDepth(2);
        jobInfo.setJobThreadNum(5);
        jobInfo.setSleep(200L);
        jobInfo.setReset(false);
//        jobInfo.getSeed().add("http://tech.163.com/");
//        jobInfo.getSeed().add("http://news.163.com/15/0821/04/B1H03V0O00014AED.html");
        jobInfo.getSeed().add("http://news.163.com/shehui/");
//        jobInfo.getSeed().add("http://news.163.com/15/0813/06/B0SJM5D100011229.html");
        Orders orders = new Orders();
        String dateStr = dateStr();
        orders.setPathRegx("(http://\\w+.163.com/\\w+/\\d+/\\w+.html.*)|(http://\\w+.163.com/\\w+/)|(http://\\w+.163.com)");
//        orders.setTargetRegx("http://\\w+.163.com/15/\\d+/\\d+/\\w+.html.*");
        orders.setTargetRegx("http://\\w+.163.com/15/" + dateStr + "/\\d+/\\w+.html.*");
        orders.setFields(ImmutableMap.of(
                "title",
                new ExtratField("title", "<title>([^<]+)</title>", 1, Constants.FmtType.str),
//                "html", new ExtratField("html", ".*(<body>.*?</body>).*", 1),
                //<p class="mt15 ml25 newstime ">2015-08-12 15:44:03 	<span class="ml15">
                "infoTime_dt",
                new ExtratField("infoTime", "(?is)<div\\s*class=\"ep-time-soure cDGray\">\\s*(.+?)[\\　\\s]*来源.*</div>", 1, Constants.FmtType.date)

        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }

    private static String dateStr() {
        String out = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
        Calendar c = Calendar.getInstance();
        out += "(" + sdf.format(c.getTime()) + ")|";
        c.add(Calendar.DAY_OF_MONTH, 1);
        out += "(" + sdf.format(c.getTime()) + ")|";
        c.add(Calendar.DAY_OF_MONTH, -2);
        out += "(" + sdf.format(c.getTime()) + ")";
        return out;
    }

    public static void main(String[] args) {
        JobInfo job = job();
        Pattern p = Pattern.compile(job.getOrders().getPathRegx());
        System.out.println(p.matcher("http://mobile.163.com").find());
    }

}