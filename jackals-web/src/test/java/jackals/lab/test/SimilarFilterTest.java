package jackals.lab.test;

import com.wisers.crawler.BaseTest;
import jackals.lab.FileUtil;
import jackals.lab.SimilarFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class SimilarFilterTest extends BaseTest {
    @Autowired
    SimilarFilter similarFilter;


    @Test
    public void similarFilter() throws IOException {
        similarFilter.init();
        similarFilter.main();
    }


}
