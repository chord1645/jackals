package com.shrek.crawler.test.jest;

import com.google.common.collect.Lists;
import com.shrek.crawler.test.BaseTest;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
import jackals.Constants;
import jackals.downloader.ProxyPool;
import jackals.downloader.Valid;
import jackals.filter.MemoryFilter;
import jackals.job.pojo.ExtratField;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.model.PageObj;
import jackals.output.OneFileOutputPipe;
import jackals.page.DefaultPageProcessImpl;
import jackals.page.HtmlExtratorImpl;
import jackals.single.SingleSpider;
import jackals.utils.BlockExecutorPool;
import jackals.utils.LogbackConfigurer;
import org.apache.http.conn.ssl.SSLContexts;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class JestTest  {

    JestClient client;

    @Before
    public void before() {
//        SSLContexts e;
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("http://localhost:9200")
                .multiThreaded(true)
                .build());
        client = factory.getObject();
    }
    @Test
    public void index() throws IOException {
        client.execute(new CreateIndex.Builder("index1").build());
    }
    @Test
    public void mapping() throws IOException {

//        "analyzer": "ik_max_word",
//                "search_analyzer": "ik_max_word",
        PutMapping putMapping = new PutMapping.Builder(
                "index1",
                "type1",
                "{ \"type1\" : { \"properties\" : { \"message\" : {" +
                        "\"type\" : \"string\"," +
                        " \"analyzer\": \"ik_max_word\""+
                        "\"search_analyzer\": \"ik_max_word\""+
                        " \"store\" : \"yes\"} } } }"
        ).build();
        client.execute(putMapping);
    }


}