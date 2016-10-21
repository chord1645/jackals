package jackals.page;

import com.alibaba.fastjson.JSONObject;
import jackals.downloader.Downloader;
import jackals.downloader.HttpDownloader;
import jackals.downloader.ProxyHttpDownloader;
import jackals.downloader.ReqCfg;
import jackals.job.pojo.Orders;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.utils.LinkUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 */
abstract public class HtmlExtrator {
    protected static boolean showAnalys = false;
    Logger logger = LoggerFactory.getLogger(PageProcess.class);

    final public Object extrat(PageObj page, Orders jobInfo) {
        Object result = doExtrat(page, jobInfo);
//        AfterExtract afterExtract = jobInfo.getAfterExtract();
//        if (afterExtract != null)
//            afterExtract.afterExtract(result);
        return result;
    }

    abstract public Object doExtrat(PageObj page, Orders jobInfo);

    public Object test(Orders jobInfo, String url) {
        showAnalys = true;
        return _test(jobInfo, url, new HttpDownloader(1));
    }
    public Object testProxy(Orders jobInfo, String url) {
        showAnalys = true;
        return _test(jobInfo, url, new ProxyHttpDownloader(1));
    }
    public Object _test(Orders jobInfo, String url, Downloader downloader) {
        showAnalys = true;
        Pattern path = Pattern.compile(jobInfo.getPathRegx());
        Pattern target = Pattern.compile(jobInfo.getTargetRegx());
        PageObj page = downloader.download(new RequestOjb(url),
                ReqCfg.deft().setTimeOut(10000)
                        .setUserAgent("Windows / Firefox 29: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0"));
//        System.out.println(page.getRawText());
        Document doc = Jsoup.parse(page.getRawText(), page.getRequest().getUrl());
        Elements links = doc.getElementsByTag("a");

        for (Element a : links) {
            String href = LinkUtil.clean(a.attr("abs:href"));
            if (!StringUtils.hasText(href))
                continue;
            logger.info("path={},target={},href={}", path.matcher(href).find(), target.matcher(href).find(), href);
        }
        JSONObject obj = (JSONObject) extrat(page, jobInfo);
        System.out.println("==================================================");
        System.out.println(obj);
//        System.out.println(obj.get("content_css").toString().replaceAll("\\s*",""));

        return obj;
    }
}
