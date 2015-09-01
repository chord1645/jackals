package jackals.page;

import jackals.job.pojo.JobInfo;
import jackals.model.RequestOjb;
import org.apache.solr.client.solrj.SolrServerException;

import java.util.ArrayList;

/**
 */
public interface PageProcess {

    ArrayList<RequestOjb> process(RequestOjb link,JobInfo job) throws Exception;
}
