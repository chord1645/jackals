package jackals.downloader;

import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.page.Proxy;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


@ThreadSafe
abstract public class ProxyHttpDownloader extends HttpDownloader {
    private Logger logger = LoggerFactory.getLogger(getClass());


//    public static void main(String[] args) {
//        PageObj page = new ProxyHttpDownloader(3).download(
//                new RequestOjb("http://finance.sina.com.cn/review/hgds/20150707/074122609107.shtml"),
//                ReqCfg.deft().setTimeOut(30000));
//        System.out.println(page.getRawText());
//    }

    public ProxyHttpDownloader(int size) {
        super(size);
    }

    abstract protected Proxy getProxy();

    abstract protected void updateProxy(Proxy proxy, boolean success, long cost);

    @Override
    public PageObj download(RequestOjb request, ReqCfg cfg) {
        logger.debug("downloading page {}", request.getUrl());
        Proxy proxy = getProxy();
        if (proxy == null) {//无代理，return
            logger.error("proxy pool empty!");
            return new PageObj(request);
        }
        CloseableHttpResponse httpResponse = null;
        Valid valid = cfg.getValid();
        PageObj page = null;
        for (int retry = 1; retry <= getMaxRetry(); retry++) {
            long s = System.currentTimeMillis();
            try {
                logger.error("try[{}] {} {}", retry, proxy, request.getUrl());
                randomUa(cfg);
                //////////////////////////
                HttpUriRequest httpRequest = buildHttpRequest(request, cfg, null, proxy.httpHost);
                CloseableHttpClient client = httpClientPool.createClient(cfg);
                httpResponse = client.execute(httpRequest);
                long cost = System.currentTimeMillis() - s;
                logger.info("download <<<<<<<<<<<<<< {} {} {}ms", proxy, request.getUrl(), cost);
                page = handleResponse(request, httpResponse, valid);
//                    page.setCost(cost);
                //////////////////////////
                updateProxy(proxy, page.isSuccess(), cost);
                if (page.isSuccess())
                    return page;
            } catch (Throwable e) {
                logger.error("try  error[" + retry + "]  " + request.getUrl() + " error: " + e.getMessage());
                updateProxy(proxy, false, System.currentTimeMillis() - s);
                if (retry >= getMaxRetry())
                    return handleError(request, e);
            } finally {
                try {
                    if (httpResponse != null) EntityUtils.consume(httpResponse.getEntity());
                } catch (IOException e) {
                    logger.warn("close response fail", e);
                }
            }
        }
        return page;
    }

}
