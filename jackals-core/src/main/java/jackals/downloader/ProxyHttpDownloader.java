package jackals.downloader;

import com.google.common.collect.Lists;
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

    @Override
    public PageObj download(RequestOjb request, ReqCfg cfg) {
        logger.debug("downloading page {}", request.getUrl());
        HttpHost proxy = null;
        try {
            long s = System.currentTimeMillis();
            PageObj page = test(request, cfg,proxy);
            logger.info("download cost {} {} [{}]", (System.currentTimeMillis() - s), request.getUrl());
            return page;
        } catch (Throwable e) {
            logger.error("download error {}" + request.getUrl() + " error", e);
            return handleError(request, e);
        }finally {

        }
    }

    public PageObj test(RequestOjb request, ReqCfg cfg, HttpHost proxy) throws Exception {
        CloseableHttpResponse httpResponse = null;
        PageObj page;
        int retry = 0;
        for (; retry<3; ) {//遍历所有代理
            try {
                proxy = proxyPool.getProxy();
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
