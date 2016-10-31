package jackals.downloader;

import com.google.common.collect.Lists;
import jackals.utils.SpringContextHolder;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ProxyPool1 {//TODO later
    private Logger logger = LoggerFactory.getLogger(getClass());
    List<HttpHost> proxyPool = Lists.newArrayList();
    public ProxyPool1(){
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

    /**
     *
     * @param proxy
     * @param success 下载，验证是否成功
     * @param cost 耗时
     */
    public void update(HttpHost proxy,boolean success,long cost) {
        logger.info("drop proxy {}", proxy);
        proxyPool.remove(proxy);
    }

    public HttpHost getProxy() {
        logger.info("proxy pool {}", proxyPool.size());
        if (proxyPool.size() < 1) return null;
        return proxyPool.get(RandomUtils.nextInt(proxyPool.size()));
    }
}