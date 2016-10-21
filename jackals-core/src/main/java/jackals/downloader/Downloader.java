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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


abstract public class Downloader {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected HttpClient httpClientPool;
    List<Object> uaList = new ArrayList<Object>();

    public Downloader(int size) {
        logger.info("HttpClientDownloader init");
        httpClientPool = new HttpClient(size);
        Properties uaConfig = SpringContextHolder.getBean("uaConfig");
        uaList.addAll(uaConfig.values());
    }


    public PageObj download(RequestOjb request, ReqCfg cfg) {
        Map<String, String> headers = null;
        logger.debug("downloading page {}", request.getUrl());
        CloseableHttpResponse httpResponse = null;
        try {
            long s = System.currentTimeMillis();
            randomUa(cfg);
            HttpUriRequest httpRequest = buildHttpRequest(request, cfg, headers,null);
            CloseableHttpClient client = httpClientPool.createClient(cfg);
            httpResponse = client.execute(httpRequest);
//            int statusCode = httpResponse.getStatusLine().getStatusCode();
            PageObj page = handleResponse(request, httpResponse);
            logger.info("download cost {} {}", (System.currentTimeMillis() - s), request.getUrl());
            return page;
        } catch (Throwable e) {
            logger.error("download error {}" + request.getUrl() + " error", e);
            return handleError(request, e);
        } finally {
            try {
                if (httpResponse != null) {
                    //ensure the connection is released back to pool
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
    }

    protected PageObj handleResponse(RequestOjb request, HttpResponse httpResponse) throws IOException {
        String content = IOUtils.toString(httpResponse.getEntity().getContent(), "iso-8859-1");
        //text/html; charset=gb2312
        String charset = StringUtil.regxGet("charset\\s*=\\s*['\"]*([^\\s;'\"]*)", 1, content);
        if (StringUtils.isEmpty(charset)) charset = "utf-8";
        if (charset.equals("gb2312")) charset = "gbk";
        content = new String(content.getBytes("iso-8859-1"), charset);
        PageObj page = new PageObj();
        page.setRawText(content);
        page.setRequest(request);
        page.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        return page;
    }

    protected void randomUa(ReqCfg cfg) {
        if (!CollectionUtils.isEmpty(uaList)) {
            int index = RandomUtils.nextInt(uaList.size());
            cfg.setUserAgent(uaList.get(index).toString());
        }
    }


    protected PageObj handleError(RequestOjb request, Throwable e) {
        PageObj page = new PageObj();
        page.setRequest(request);
        page.setRawText(e.toString());
        page.setStatusCode(-1);
        return page;
    }


    abstract protected HttpUriRequest buildHttpRequest(RequestOjb request, ReqCfg cfg, Map<String, String> headers, HttpHost proxy);



    class HttpClient {

        private PoolingHttpClientConnectionManager manager;

        public HttpClient(int size) {
            Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", SSLConnectionSocketFactory.getSocketFactory())
                    .build();
            manager = new PoolingHttpClientConnectionManager(reg);
            manager.setDefaultMaxPerRoute(100);
            manager.setMaxTotal(size);
        }

        public CloseableHttpClient createClient(ReqCfg cfg) {
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(manager);
            httpClientBuilder.setUserAgent(cfg.getUserAgent());
            //TODO gzip 重试 cookie
            SocketConfig socketCfg = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
            httpClientBuilder.setDefaultSocketConfig(socketCfg);
            return httpClientBuilder.build();
        }
    }


}
