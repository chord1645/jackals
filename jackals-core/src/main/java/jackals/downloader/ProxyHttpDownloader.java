package jackals.downloader;

import com.google.common.collect.Lists;
import jackals.job.pojo.JobInfo;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.utils.SpringContextHolder;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


@ThreadSafe
public class ProxyHttpDownloader extends HttpDownloader {
    private Logger logger = LoggerFactory.getLogger(getClass());

    ProxyPool proxyPool = new ProxyPool();

    public static void main(String[] args) {
        PageObj page = new ProxyHttpDownloader(3).download(
                new RequestOjb("http://finance.sina.com.cn/review/hgds/20150707/074122609107.shtml"),
                ReqCfg.deft().setTimeOut(30000));
        System.out.println(page.getRawText());
    }

    public ProxyHttpDownloader(int size) {
        super(size);
    }

    int MAX_RETRY = 3;

    @Override
    public PageObj download(RequestOjb request, ReqCfg cfg) {
        logger.debug("downloading page {}", request.getUrl());
        HttpHost proxy = proxyPool.getProxy();
        if (proxy == null) { //无代理，return
            logger.error("proxy pool empty!");
            return new PageObj(request);
        }
        CloseableHttpResponse httpResponse = null;
        JobInfo jobInfo = cfg.getJobInfo();
        PageObj page = null;
        for (int retry = 1; ; retry++) {
            long s = System.currentTimeMillis();
            try {
                logger.error("try[{}] {} {}", retry, proxy, request.getUrl());
                randomUa(cfg);
                //////////////////////////
                HttpUriRequest httpRequest = buildHttpRequest(request, cfg, null, proxy);
                CloseableHttpClient client = httpClientPool.createClient(cfg);
                httpResponse = client.execute(httpRequest);
                long cost = System.currentTimeMillis() - s;
                logger.info("rawtext {} {} {}ms", proxy, request.getUrl(), cost);
                page = handleResponse(request, httpResponse);
//                    page.setCost(cost);
                //////////////////////////
                boolean success = jobInfo.getValid().success(page);
                proxyPool.update(proxy, success, cost);
                if (success)
                    return page;
//                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
//                        if (page.getRawText().contains("好大夫在线")) {
//                            return page;
//                        }
//                    }
            } catch (Throwable e) {
                logger.error("try  error[" + retry + "]  " + request.getUrl() + " error: " + e.getMessage());
                proxyPool.update(proxy, false, System.currentTimeMillis() - s);
                if (retry >= MAX_RETRY)
                    return handleError(request, e);
            } finally {
                try {
                    if (httpResponse != null) EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException e) {
                    logger.warn("close response fail", e);
                }
            }
        }
    }

    public PageObj test(RequestOjb request, ReqCfg cfg) throws Exception {
        CloseableHttpResponse httpResponse = null;
        PageObj page;
        int retry = 0;
        for (; retry < 3; ) {//遍历所有代理
            try {
                HttpHost proxy = proxyPool.getProxy();
                if (proxy == null) break;
                logger.error("try[{}] {} {}", retry, proxy, request.getUrl());
                randomUa(cfg);
                //////////////////////////
                HttpUriRequest httpRequest = buildHttpRequest(request, cfg, null, proxy);
                CloseableHttpClient client = httpClientPool.createClient(cfg);
                httpResponse = client.execute(httpRequest);
                page = handleResponse(request, httpResponse);
                //////////////////////////
                logger.info("rawtext {} {} {}", proxy, request.getUrl(), page.getRawText().substring(0, Math.min(page.getRawText().length(), 30)));
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    if (page.getRawText().contains("好大夫在线")) {
                        return page;
                    }
                }
            } catch (Throwable e) {
                logger.error("try  error[" + retry + "]  " + request.getUrl() + " error" + e.getMessage());
            } finally {
                try {
                    if (httpResponse != null) EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException e) {
                    logger.warn("close response fail", e);
                }

                retry++;
            }
        }
        throw new Exception("retry timeout");
    }


}
