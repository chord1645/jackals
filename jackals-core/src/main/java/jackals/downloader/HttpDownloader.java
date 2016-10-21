package jackals.downloader;

import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.utils.SpringContextHolder;
import jackals.utils.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;


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
