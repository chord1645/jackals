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

    @Override
    protected HttpUriRequest buildHttpRequest(RequestOjb request, ReqCfg cfg, Map<String, String> headers,HttpHost proxy) {
        RequestBuilder builder = RequestBuilder.get().setUri(request.getUrl());
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                builder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setProxy(proxy)
                .setConnectionRequestTimeout(cfg.getTimeOut())
                .setSocketTimeout(cfg.getTimeOut())
                .setConnectTimeout(cfg.getTimeOut())
                .setCookieSpec(CookieSpecs.BEST_MATCH);
        builder.setConfig(requestConfigBuilder.build());
        return builder.build();
    }


}
