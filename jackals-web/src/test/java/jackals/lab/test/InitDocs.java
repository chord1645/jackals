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

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class InitDocs extends BaseTest {
    @Autowired
    SolrServer solrServer;

    static String input = "D:\\work\\tmp\\CORPUS1";

    @Autowired
    SimilarFilter similarFilter;
    int num = 0;

    public String query(int days) {
        String query = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, -days);
        query += " infoTime_dt:[" + sdf.format(c.getTime()) + " TO *]";
        query += " AND -title:北京 ";
        return query;
    }
    @Test
    public void similarFilter() throws IOException {
        buildData();
        similarFilter.init();
        similarFilter.main();
    }

    @Test
    public void buildData() throws IOException {
        FileUtil.clean(new File(input));
//        System.out.println("delete:" + new File(input).delete());
        String query =query(12);
//        String query = "title:(三大 运营商)";
        for (int x = 1; ; x++) {
            List list = sortList(query, x, 10, "infoTime_dt desc");
//            List list = sortList("jobId_t:news.163.com", x, 100, "saveTime_dt desc");
            System.out.println(x);
            if (CollectionUtils.isEmpty(list))
                break;
            for (Object obj : list) {
                Map<String, Object> map = (Map<String, Object>) obj;
                //取出title
                ArrayList<String> title = (ArrayList) map.get("title");
                String content = (String) map.get("content_css");
                String url = (String) map.get("id");
                //写文件
                content = content.replaceAll("(?is)<.*?>", "").replaceAll("\n|　", "").trim();
                File file = new File(input, (num++) + ".txt");
                if (StringUtils.isNotEmpty(content))
                    FileUtil.write(file, url + "\n" + title.get(0) + "\n" + content, false);
            }
        }
    }


    public List<? extends Map> sortList(String queryStr, int pageNum, int size, String orderBy) {
        try {
            SolrQuery query = new SolrQuery();
            query.setQuery(queryStr);
//            query.set("fl", "score");
            query.set("q.op", "AND");
            query.set("wt", "json");
            query.set("sort", orderBy);
            query.setStart((pageNum - 1) * size);
            query.setRows(size);
            QueryResponse rsp = solrServer.query(query);
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
            e.printStackTrace();
        }
        return new ArrayList<Map>();
    }
}
