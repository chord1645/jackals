package jackals.downloader;

import jackals.page.Proxy;
import org.apache.http.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ThreadSafe
public class OneProxyHttpDownloader extends ProxyHttpDownloader {
    private Logger logger = LoggerFactory.getLogger(getClass());
  Proxy proxy;

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public OneProxyHttpDownloader(int size, Proxy proxy) {
        super(size);
        this.proxy = proxy;
    }

    @Override
    protected Proxy getProxy() {
        return proxy;
    }

    @Override
    protected void updateProxy(Proxy proxy, boolean success, long cost) {
    }
}
