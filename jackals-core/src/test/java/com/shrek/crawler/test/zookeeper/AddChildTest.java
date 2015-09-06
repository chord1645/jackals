package com.shrek.crawler.test.zookeeper;

import com.shrek.crawler.test.BaseTest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


public class AddChildTest extends BaseTest {

    @Autowired
    Properties mqConfig;
    ZooKeeper zk;
    static String node;
    private static Watcher getExistsWatcher(final ZooKeeper zkp) {
        return new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("------------------------------------" + event);
                try {
                    Stat stat = zkp.exists(node, this);// 再次注册监听
                    TimeUnit.SECONDS.sleep(1);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
    }
    @Before
    public void before() throws IOException, KeeperException, InterruptedException {
        String connection = "localhost:" + 3181;
        node="/test/job1";
        zk = new ZooKeeper(connection, 100000000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {

            }
        });
//        zk = new ZooKeeper(connection, 100000000, new Watch1());
        try {
//            zk.create("/test", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//            zk.create("/test/create", "createData".getBytes("utf-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        } catch (Exception e) {

        }
    }

    @Test
    public void add() throws IOException, SolrServerException, KeeperException, InterruptedException {
        String jobinfo = "jobinfo" + new Date().getTime();
        zk.create("/test/job1", jobinfo.getBytes("utf-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        Thread.sleep(500);
        zk.exists(node,getExistsWatcher(zk));
        System.out.println("create:/test/job1");
        TimeUnit.MILLISECONDS.sleep(10);
        jobinfo = "jobinfo" + new Date().getTime();
//        zk.create("/test/job1", jobinfo.getBytes("utf-8"), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.setData("/test/job1", jobinfo.getBytes("utf-8"),-1);
        TimeUnit.MILLISECONDS.sleep(10);
        zk.setData("/test/job1", jobinfo.getBytes("utf-8"),-1);
        TimeUnit.MILLISECONDS.sleep(10);
        zk.setData("/test/job1", jobinfo.getBytes("utf-8"),-1);
        TimeUnit.MILLISECONDS.sleep(10);
        zk.setData("/test/job1", jobinfo.getBytes("utf-8"),-1);
//        zk.delete("/test/job1",-1);
        System.out.println("setData:/test/job1");
    }

    @After
    public void after() throws IOException, KeeperException, InterruptedException {
        List<String> list = zk.getChildren("/test", true);
        for (String s : list) {
            System.out.println("delete:" + s);
            zk.delete("/test/" + s, -1);
        }

    }
}