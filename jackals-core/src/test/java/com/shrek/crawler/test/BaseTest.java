package com.shrek.crawler.test;

import jackals.utils.SpringContextHolder;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/jar/config/test/spring/test.xml"})
public class BaseTest {

    @Test
    public void start() throws Exception {
        SolrServer solrServer = SpringContextHolder.getBean(SolrServer.class);
        while (true) {
            solrServer.deleteByQuery("*:*");
//            TimeUnit.MILLISECONDS.sleep(50);
            solrServer.commit();
        }
    }
}
