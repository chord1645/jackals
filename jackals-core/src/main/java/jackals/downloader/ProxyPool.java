package jackals.downloader;

import com.google.common.base.Joiner;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.page.Proxy;
import jackals.utils.SpringContextHolder;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ProxyPool {
    private Logger logger = LoggerFactory.getLogger(getClass());
    List<Proxy> proxyPool = null;

    public static void main(String[] args) {
        new ProxyPool();
    }

    private class PoolFresh implements Runnable {
        Proxy proxy = null;
        Valid valid;
        OneProxyHttpDownloader downloader;

        public PoolFresh(Proxy proxy) {
            this.proxy = proxy;
            this.downloader = new OneProxyHttpDownloader(proxyPool.size(), null);
            downloader.setMaxRetry(1);
            valid = new Valid() {
                @Override
                public boolean success(PageObj page) {
                    return page.getStatusCode() == 200 && StringUtils.isNotEmpty(page.getRawText());
                }
            };
        }

        @Override
        public void run() {
            for (; ; ) {
                downloader.setProxy(proxy);
                RequestOjb request = new RequestOjb("https://www.baidu.com/");
//                RequestOjb request = new RequestOjb("http://www.haodf.com/");
                Long s = System.currentTimeMillis();
                PageObj page = downloader.download(request, ReqCfg.deft().setTimeOut(1000 * 10).setValid(valid));

                boolean success = page.isSuccess();
                long cost = System.currentTimeMillis() - s;
                proxy.cost = cost;
                proxy.useful = success ? 1 : 0;
                logger.info("proxy test:{}, {}, {}", success, cost, proxy.httpHost);
//                update(proxy, success, cost);
                sort();
                proxyPool.stream().forEach(e -> logger.info("proxy:" + e));
                try {
                    TimeUnit.SECONDS.sleep(60*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    synchronized private void sort() {
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
//        proxyPool.stream()
//                .map(e -> new Thread(new PoolFresh(e)))
//                .collect(Collectors.toList())
//                .forEach(e -> e.start());

    }

    /**
     * @param success 下载，验证是否成功
     * @param cost    耗时
     */
    synchronized public void update(Proxy proxy, boolean success, long cost) {
        logger.info("update proxy {}", proxy.httpHost);
        proxyPool.replaceAll(e -> {
            if (proxy.httpHost.equals(e.httpHost)) {
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

    }

    public Proxy getProxy() {
        logger.info("proxy pool {}", proxyPool.size());
        if (CollectionUtils.isEmpty(proxyPool)) return null;
//        return proxyPool.get(0).httpHost;
        return proxyPool.get(RandomUtils.nextInt(proxyPool.size() / 3));
//        return proxyPool.get(0);
    }
}