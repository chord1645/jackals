package jackals.downloader;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import jackals.utils.SpringContextHolder;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ProxyPool {//TODO later
    private Logger logger = LoggerFactory.getLogger(getClass());
    List<Proxy> proxyPool = null;

    class Proxy {
        HttpHost httpHost;
        long cost = 1000 * 60 * 30;
        int useful = 1;

        public Proxy(HttpHost httpHost) {
            this.httpHost = httpHost;
        }

        @Override
        public String toString() {
            return Joiner.on(" ").join(useful, cost, httpHost.toString());
        }
    }

    public ProxyPool() {
        Properties proxyConfig = SpringContextHolder.getBean("proxyConfig");
        proxyPool = proxyConfig.keySet().stream()
                .map(e -> {
                    Proxy proxy = new Proxy(new HttpHost(e.toString(), Integer.valueOf(proxyConfig.getProperty(e.toString()))));
                    return proxy;
                })
                .filter(e -> e.httpHost.getPort() == 80)
                .sorted(new Comparator<Proxy>() {//参数1小，返回负数
                    @Override
                    public int compare(Proxy o1, Proxy o2) {
                        if (o1.useful != o2.useful)     //状态不同，比状态
                            return o2.useful - o1.useful;
                        else                            //状态相同，比耗时
                            return (int) (o1.cost - o2.cost);
                    }
                })
                .collect(Collectors.toList());
        proxyPool.stream().forEach(e ->
                logger.info("proxy:" + e)
        );
    }

    /**
     * @param success 下载，验证是否成功
     * @param cost    耗时
     */
    synchronized public void update(HttpHost httpHost, boolean success, long cost) {
        logger.info("update proxy {}", httpHost);
        proxyPool.replaceAll(e -> {
            if (httpHost.equals(e.httpHost)) {
                e.cost = cost;
                e.useful = success ? 1 : 0;
            }
            return e;
        });
        proxyPool = proxyPool.stream()
                .sorted(new Comparator<Proxy>() {//参数1小，返回负数
                    @Override
                    public int compare(Proxy o1, Proxy o2) {
                        if (o1.useful != o2.useful)     //状态不同，比状态
                            return o2.useful - o1.useful;
                        else                            //状态相同，比耗时
                            return (int) (o1.cost - o2.cost);
                    }
                })
                .collect(Collectors.toList());
        proxyPool.stream().forEach(e ->
                logger.info("proxy:" + e)
        );
    }

    public HttpHost getProxy() {
        logger.info("proxy pool {}", proxyPool.size());
        if (CollectionUtils.isEmpty(proxyPool)) return null;
//        return proxyPool.get(0).httpHost;
        return proxyPool.get(RandomUtils.nextInt(proxyPool.size() / 3)).httpHost;
    }
}