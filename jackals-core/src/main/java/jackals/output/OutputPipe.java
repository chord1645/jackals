package jackals.output;

import jackals.job.pojo.JobInfo;
import jackals.model.PageObj;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;

/**
 */
public interface OutputPipe {
    void save(JobInfo spiderJob, PageObj page, Object e) throws IOException, SolrServerException, Exception;

    void error(JobInfo job, PageObj page);
}
