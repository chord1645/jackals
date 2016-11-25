package jackals.output.solr;

import com.alibaba.fastjson.JSONObject;
import jackals.job.pojo.JobInfo;
import jackals.model.PageObj;
import jackals.output.OutputPipe;
import jackals.utils.SpringContextHolder;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 */
public class SolrOutputPipe implements OutputPipe {
    Logger logger = LoggerFactory.getLogger(SolrOutputPipe.class);

    private SolrClient solrServer;//TODO 升级到6.10后未测试

    public static void main(String[] args) {
        System.out.println(new File("http://www.cnblogs.com/zhuyaguang/archive/2015/08/04.html").getName());
    }

    public SolrOutputPipe() {
        solrServer = SpringContextHolder.getBean(SolrClient.class);
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

    @Override
    public void error(JobInfo job, PageObj page) {

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
