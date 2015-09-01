package com.shrek.crawler.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jackals.Constants;
import jackals.downloader.HttpDownloader;
import jackals.downloader.ReqCfg;
import jackals.job.JobManager;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.mq.activemq.ActiveMQSender;
import jackals.page.HtmlExtratorImpl;
import jackals.utils.LinkUtil;
import jackals.utils.SpringContextHolder;
import org.apache.solr.client.solrj.SolrServerException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class AmazonJob extends BaseTest {

    JobManager jobManager;

    @Before
    public void before() {
        Properties mqConfig = SpringContextHolder.getBean("mqConfig");
        jobManager = JobManager.create(new ActiveMQSender(mqConfig.getProperty("mq.amq.broker")));

    }

    @Test
    public void start() throws IOException, SolrServerException {
//        SolrServer solrServer = SpringContextHolder.getBean(SolrServer.class);
//        solrServer.deleteByQuery("*:*");
//        solrServer.commit();
        JobInfo jobInfo = job();
        jobManager.restartJob(jobInfo,
                ImmutableList.of("10", "20", "30")
//                ImmutableList.of("1","2","3")
        );
    }

    @Test
    public void ExtratorImpl() {
        JobInfo jobInfo = job();
        new HtmlExtratorImpl().test(
                jobInfo.getOrders(),
                "http://www.amazon.cn/%E9%AC%BC%E8%B0%B7%E5%AD%90-%E9%AC%BC%E8%B0%B7%E5%AD%90/dp/B00AA7KMGU/ref=sr_1_2?s=digital-text&ie=UTF8&qid=1440144527&sr=1-2");
    }

    public static List<String> seeds() {
        List<String> out = new ArrayList<String>();
        String url = "http://www.amazon.cn/Kindle%E7%94%B5%E5%AD%90%E4%B9%A6/b/ref=sa_menu_kindle_l2_116169071?ie=UTF8&node=116169071";
        HttpDownloader downloader = new HttpDownloader(1);
        PageObj page = downloader.download(new RequestOjb(url),
                ReqCfg.deft().setTimeOut(10000)
                        .setUserAgent("Windows / Firefox 29: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0"));
        Document doc = Jsoup.parse(page.getRawText(), page.getRequest().getUrl());
        Elements links = doc.getElementsByTag("a");

        for (Element a : links) {
            String href = LinkUtil.clean(a.attr("abs:href"));
            if (!StringUtils.hasText(href))
                continue;
            if (href.contains("lp_116169071_nr_p_36_")) {
//                http://www.amazon.cn/s/ref=lp_116169071_nr_p_36_0?fst=as%3Aoff&rh=n%3A116087071%2Cn%3A%21116088071%2Cn%3A116169071%2Cp_36%3A159125071&bbn=116169071&ie=UTF8&qid=1440309249&rnid=149573071
                System.out.println(href);
                out.add(href);
            }
        }
        return out;
    }

    public static JobInfo job() {
        JobInfo jobInfo = JobInfo.create("www.amazon.cn.kindle.book");
        jobInfo.setMaxDepth(10000);
        jobInfo.setJobThreadNum(3);
        jobInfo.setReset(true);
        jobInfo.setSleep(500);
//        jobInfo.getSeed().add("http://www.amazon.cn/s/ref=sr_pg_2/480-5673561-1434156?fst=as%3Aoff&rh=n%3A116087071%2Cn%3A%21116088071%2Cn%3A116169071%2Cp_36%3A149577071&page=2&bbn=116169071&ie=UTF8&qid=1440312291");
        jobInfo.setSeed(seeds());
        Orders orders = new Orders();
        //http://www.amazon.cn/gp/search/ref=sr_pg_\\d+[/?]{1}.*
        orders.setPathRegx("(http://www.amazon.cn/s/ref=sr_pg_\\d+[/?]{1}.*)|(http://www.amazon.cn/gp/search/ref=sr_pg_\\d+[/?]{1}.*)");
//        http://www.amazon.cn/%E%E8%80%85/dp/B00YMKG4OU/ref=sr_1_1?s=digital-text&ie=UTF8&qid=1440143007&sr=1-1
        orders.setTargetRegx("http://www.amazon.cn/[^/]+/dp/\\w+/ref=sr_\\d+_\\d+[/?]{1}.*");
        orders.setFields(ImmutableMap.of(
                "title",
                new ExtratField("title", "<title>《(.+)》.*</title>", 1, Constants.FmtType.str),
                "comment_i",
                new ExtratField("comment_i", "(?is)>(\\d+)\\s*条商品评论", 1, Constants.FmtType.str),
//                "html", new ExtratField("html", ".*(<body>.*?</body>).*", 1),
                //<p class="mt15 ml25 newstime ">2015-08-12 15:44:03 	<span class="ml15">
                "price_d",
                new ExtratField("price_d", "(?is)<b\\s*class=\"priceLarge\"\\s*>\\s*￥\\s*([^<]+)\\s*</b>", 1, Constants.FmtType.str)

        ));
        jobInfo.setOrders(orders);
        return jobInfo;
    }

}