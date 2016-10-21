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

    List<HttpHost> proxyPool = Lists.newArrayList();

    public static void main(String[] args) {
        PageObj page = new ProxyHttpDownloader(3).download(
                new RequestOjb("http://finance.sina.com.cn/review/hgds/20150707/074122609107.shtml"),
                ReqCfg.deft().setTimeOut(30000));
        System.out.println(page.getRawText());
    }

    public ProxyHttpDownloader(int size) {
        super(size);
        Properties proxyConfig = SpringContextHolder.getBean("proxyConfig");
        proxyPool = proxyConfig.keySet().stream()
                .map(e -> {
                    HttpHost proxy = new HttpHost(e.toString(), Integer.valueOf(proxyConfig.getProperty(e.toString())));
                    return proxy;
                })
                .filter(e -> e.getPort() == 80)
                .collect(Collectors.toList());
        proxyPool.stream().forEach(e ->
                logger.info("proxy:" + e)
        );
    }

    @Override
    public PageObj download(RequestOjb request, ReqCfg cfg) {
        logger.debug("downloading page {}", request.getUrl());
        try {
            long s = System.currentTimeMillis();
            PageObj page = test(request, cfg);
            logger.info("download cost {} {} [{}]", (System.currentTimeMillis() - s), request.getUrl());
            return page;
        } catch (Throwable e) {
            logger.error("download error {}" + request.getUrl() + " error", e);
            return handleError(request, e);
        }
    }

    public PageObj test(RequestOjb request, ReqCfg cfg) throws Exception {
        CloseableHttpResponse httpResponse = null;
        HttpHost proxy = null;
        PageObj page;
        int retry = 0;
        for (; ; ) {//遍历所有代理
            try {
                proxy = getProxy();
                if (proxy == null) break;
                logger.error("try[{}] {} {}", retry, proxy, request.getUrl());
                randomUa(cfg);
                HttpUriRequest httpRequest = buildHttpRequest(request, cfg, null, proxy);
                CloseableHttpClient client = httpClientPool.createClient(cfg);
                httpResponse = client.execute(httpRequest);
                page = handleResponse(request, httpResponse);
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
            }
            dropProxy(proxy);
            retry++;
        }
        throw new Exception("retry timeout");
    }

    private void dropProxy(HttpHost proxy) {
        logger.info("drop proxy {}", proxy);
        proxyPool.remove(proxy);
    }


    public HttpHost getProxy() {
        logger.info("proxy pool {}", proxyPool.size());
        if (proxyPool.size() < 1) return null;
        return proxyPool.get(RandomUtils.nextInt(proxyPool.size()));
    }
}
