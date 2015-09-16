package jackals.solr;

import jackals.web.pojo.News;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 搜索接口
 */
public class SolrDaoImpl implements IndexDao {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private SolrServer solrServer;

    public SolrServer getSolrServer() {
        return solrServer;
    }

    public void setSolrServer(SolrServer solrServer) {
        this.solrServer = solrServer;
    }

    public void addIndex(List<Map<String, Object>> list) {
        try {
            if (list == null || list.size() < 1) {
                log.warn("list is empty");
                return;
            }
            log.info("add index from map");
            log.info("list size " + list.size());
            Collection<SolrInputDocument> docs = new HashSet<SolrInputDocument>();
            for (Map<String, Object> weibo : list) {
                SolrInputDocument doc = new SolrInputDocument();
                for (String key : weibo.keySet()) {
                    doc.addField(key, weibo.get(key));
                }
                docs.add(doc);
            }
            solrServer.add(docs);
            solrServer.commit();
        } catch (Exception e) {
            log.error("addIndex IOException", e);
        }

    }
    public List<News> sortListObj(String queryStr, int pageNum, int size, String orderBy) {
        try {
            SolrQuery query = new SolrQuery();
            query.setQuery(queryStr);
//            query.set("fl", "id,title,infoTime_dt,content_css,useful_i,sim_i,group_s");
            query.set("q.op", "AND");
            query.set("wt", "json");
            query.set("sort", orderBy);
            query.setStart((pageNum - 1) * size);
            query.setRows(size);
            long s = System.currentTimeMillis();
            log.info("sortList: /select?" + query.toString());
            QueryResponse rsp = solrServer.query(query);
            System.out.println(System.currentTimeMillis()-s);
            return rsp.getBeans(News.class);
        } catch (Exception e) {
            log.error("listBySolrQuery error", e);
        }
        return new ArrayList();
    }
    public List<? extends Map> sortList(String queryStr, int pageNum, int size, String orderBy) {
        try {
            SolrQuery query = new SolrQuery();
            query.setQuery(queryStr);
//            query.set("fl", "id,title,infoTime_dt,content_css");
            query.set("q.op", "AND");
            query.set("wt", "json");
            query.set("sort", orderBy);
            query.setStart((pageNum - 1) * size);
            query.setRows(size);
            long s = System.currentTimeMillis();
            log.info("sortList: /select?" + query.toString());

            QueryResponse rsp = solrServer.query(query);
            System.out.println(System.currentTimeMillis()-s);
            SolrDocumentList sdl = rsp.getResults();
            // out.addAll(sdl);
//            for (SolrDocument sd : sdl) {
//                Object scoreObj = sd.getFieldValue("score");
//                float score = scoreObj != null ? Float.parseFloat(scoreObj.toString()) : 1F;
//                list.add(new IndexObj(sd, score));
//            }
            solrServer.commit();
            return sdl;
        } catch (Exception e) {
            log.error("listBySolrQuery error", e);
        }
        return new ArrayList();
    }

    public void delete(String q) {
        try {
            UpdateResponse resp = solrServer.deleteByQuery(q);
            // new UpdateRequest().deleteByQuery( q ).process( this );
            solrServer.commit();
        } catch (Exception e) {
            log.error("listBySolrQuery error", e);
        }
    }
}
