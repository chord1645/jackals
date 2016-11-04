package jackals.downloader;

import jackals.model.PageObj;
import jackals.model.RequestOjb;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;

import java.util.Map;


@ThreadSafe
public class HttpDownloader extends Downloader {
    public static void main(String[] args) {
        PageObj page = new HttpDownloader(3).download(
                new RequestOjb("http://finance.sina.com.cn/review/hgds/20150707/074122609107.shtml"),
                ReqCfg.deft().setTimeOut(30000));
        System.out.println(page.getRawText());
    }

    public HttpDownloader(int size) {
        super(size);
    }

}
