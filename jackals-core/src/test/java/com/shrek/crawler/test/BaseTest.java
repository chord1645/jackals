package com.shrek.crawler.test;

import jackals.utils.SpringContextHolder;
import org.apache.solr.client.solrj.SolrClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/jar/config/test/spring/test.xml"})
public class BaseTest {

    @Test
    public void start() throws Exception {//TODO 升级到6.10后未测试
        SolrClient solrServer = SpringContextHolder.getBean(SolrClient.class);
        while (true) {
            solrServer.deleteByQuery("*:*");
//            TimeUnit.MILLISECONDS.sleep(50);
            solrServer.commit();
        }
    }
}
