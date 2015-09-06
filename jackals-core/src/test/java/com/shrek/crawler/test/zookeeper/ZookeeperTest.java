package com.shrek.crawler.test.zookeeper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.shrek.crawler.test.BaseTest;
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
import org.apache.zookeeper.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ZookeeperTest extends BaseTest {

    @Autowired
    Properties mqConfig;
    ZooKeeper zk;

    @Before
    public void before() throws IOException, KeeperException, InterruptedException {
        String connection = "localhost:" + 3181;
//        ZooKeeper zk = new ZooKeeper(mqConfig.getProperty("mq.kafka.zookeeper"), 100000000, new Watcher() {
        zk = new ZooKeeper(connection, 100000000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.toString() + "=================");
            }
        });
        zk.exists("/test", new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(watchedEvent.toString() + "---------------------");

            }
        });
        try {
            zk.delete("/test/create", -1);
            zk.delete("/test", -1);
        } catch (Exception e) {

        }

    }

    /**
     * 节点必须一层层创建,删除不存在的节点会抛异常
     * @throws IOException
     * @throws SolrServerException
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Test
    public void create() throws IOException, SolrServerException, KeeperException, InterruptedException {
        System.out.println("create()");
        zk.create("/test", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("create \"/test");
        zk.create("/test/create", "createData".getBytes("utf-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("create \"/test/create");

        byte[] bt = zk.getData("/test/create",true,null);
        System.out.println("getData \"/test/create");

        Assert.assertEquals("createData", new String(bt, "utf-8"));
    }

    @After
    public void after() throws IOException, KeeperException, InterruptedException {
        zk.delete("/test/create", -1);
        zk.delete("/test", -1);
    }
}