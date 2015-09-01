package com.wisers.crawler;

import jackals.utils.SpringContextHolder;
import org.apache.solr.client.solrj.SolrServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/WEB-INF/config/test/spring/applicationContext.xml"})
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
