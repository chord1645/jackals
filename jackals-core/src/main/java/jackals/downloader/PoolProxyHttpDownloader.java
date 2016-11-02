package jackals.downloader;

import jackals.job.pojo.JobInfo;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.page.Proxy;
import org.apache.http.HttpHost;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


@ThreadSafe
public class PoolProxyHttpDownloader extends ProxyHttpDownloader {
    private Logger logger = LoggerFactory.getLogger(getClass());
    ProxyPool proxyPool;

    public PoolProxyHttpDownloader(int size) {
        super(size);
        this.proxyPool = new ProxyPool();
    }

    @Override
    protected Proxy getProxy() {
        return proxyPool.getProxy();
    }

    @Override
    protected void updateProxy(Proxy proxy, boolean success, long cost) {
        proxyPool.update(proxy, success, cost);
    }
}
