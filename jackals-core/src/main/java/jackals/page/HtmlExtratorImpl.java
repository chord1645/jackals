package jackals.page;

import com.alibaba.fastjson.JSONObject;
import jackals.Constants;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.Orders;
import jackals.model.PageObj;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class HtmlExtratorImpl extends HtmlExtrator {
    Logger logger = LoggerFactory.getLogger(PageProcess.class);
    boolean autoContentExtrat = true;
    ContentExtrat contentExtrat = new ContentExtrat();

    @Override
    public Object doExtrat(PageObj page, Orders jobInfo) {
        JSONObject object = new JSONObject();
        for (Map.Entry<String, ExtratField> e : jobInfo.getFields().entrySet()) {
            try {
                String s = regexGet(page.getRawText(), e.getValue().getRegx(), e.getValue().getGroup());
                Object obj = format(s, e.getValue());
                object.put(e.getKey(), obj);
            } catch (Throwable ex) {
                logger.error("doExtrat Exception", ex);
            }
        }
        object.put("url", page.getRequest().getUrl());
        if (autoContentExtrat) {
            object.put("content_css", contentExtrat.process(page.getRawText(), page.getRequest().getUrl()));
        }
        return object;
    }


    private Object format(String s, ExtratField field) {
        try {
            if (StringUtils.isEmpty(s))
                return null;
            switch (field.getFmtType()) {
                case Constants.FmtType.str:
                    return s != null ? s.trim() : null;
                case Constants.FmtType.date:
                    SimpleDateFormat sdf;
                    if (!StringUtils.isEmpty(field.getFmtStr()))
                        sdf = new SimpleDateFormat(field.getFmtStr());
                    else
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return sdf.parse(s.trim());
                default:
                    return s;
            }
        } catch (Exception e) {
        }

        return null;
    }


    public static void regexAnalyz(String str, String reg) {
        Matcher m = Pattern.compile(reg,Pattern.DOTALL).matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                System.out.println(i + " >>> " + m.group(i));
            }
        }
    }

    public static String regexGet(String str, String reg, int group) {
        if (showAnalys)
            regexAnalyz(str, reg);
        Matcher m = Pattern.compile(reg,Pattern.DOTALL).matcher(str);
        if (m.find()) {
//            System.out.println(m.groupCount());
            if (group > m.groupCount())
                return null;
            else
                return m.group(group);
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        showAnalys = true;
        String s = "http://www.amazon.cn/s/ref=sr_pg_85/fst=as%3Aoff&rh=n%3A116087071%2Cn%3A%21116088071%2Cn%3A116169071%2Cp_36%3A149575071&page=85&bbn=116169071&ie=UTF8&qid=1440310835";
        System.out.println(regexGet(s, "(http://www.amazon.cn/s/ref=sr_pg_\\d+.*)|(http://www.amazon.cn/gp/search/ref=sr_pg_\\d+[/?]{1}.*)", 0));
//        JobInfo jobInfo = MTimeJob.job();
//        new HtmlExtratorImpl().test(jobInfo.getOrders(), "http://news.mtime.com/2015/08/12/1545670.html");
    }


}
