package jackals.output.solr;

import com.alibaba.fastjson.JSONObject;
import jackals.output.OutputPipe;
import jackals.job.pojo.JobInfo;
import jackals.model.PageObj;
import jackals.utils.SpringContextHolder;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 */
public class SolrOutputPipe implements OutputPipe {
    Logger logger = LoggerFactory.getLogger(SolrOutputPipe.class);

    private SolrServer solrServer;

    public static void main(String[] args) {
        System.out.println(new File("http://www.cnblogs.com/zhuyaguang/archive/2015/08/04.html").getName());
    }

    public SolrOutputPipe() {
        solrServer = SpringContextHolder.getBean(SolrServer.class);
    }

    @Override
    public void save(JobInfo spiderJob, PageObj page, Object obj) throws Exception {
        logger.info("saved : {} {} ++++++++++++++++++++++++++++++++++++" ,spiderJob.getId(), page.getRequest().getUrl());
        JSONObject jsonObject = (JSONObject) obj;
        SolrInputDocument doc = buildDoc(spiderJob, page, jsonObject);
        solrServer.add(doc, 1000 * 30);
        logger.info("saved : {} {} --------------------------------------" ,spiderJob.getId(), page.getRequest().getUrl());
//        solrServer.commit();
    }

    private SolrInputDocument buildDoc(JobInfo spiderJob, PageObj page, JSONObject jsonObject) {
        SolrInputDocument doc = new SolrInputDocument();
        for (Map.Entry<String, Object> e : jsonObject.entrySet()) {
            doc.addField(e.getKey(), e.getValue());
        }
        doc.addField("id", page.getRequest().getUrl());
//        doc.addField("html_css", page.getRawText());
        doc.addField("saveTime_dt", new Date());
        doc.addField("jobId_t", spiderJob.getId());

        return doc;
    }

}
