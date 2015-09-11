package jackals.lab.test;

import com.wisers.crawler.BaseTest;
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

/**
 * 文本识别
 *
 * @author huang_pc
 */
public class InitDocs extends BaseTest {
    @Autowired
    SolrServer solrServer;

    static String input = "D:\\work\\tmp\\CORPUS1";

    private String buildQuery() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -3);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        StringBuffer query = new StringBuffer();
//        query.append("jobId_t:all.163.com");
//        query.append(" AND ");
        query.append("infoTime_dt:[").append(sdf.format(c.getTime())).append(" TO ").append(sdf.format(new Date())).append("]");
        return query.toString();
    }

    int num = 0;

    @Test
    public void buildData() throws IOException {

//        System.out.println("delete:" + new File(input).delete());
        String query = buildQuery();
//        String query = "title:(三大 运营商)";
        for (int x = 1;x<=5 ; x++) {
            List list = sortList(query, x, 100, "saveTime_dt desc");
//            List list = sortList("jobId_t:news.163.com", x, 100, "saveTime_dt desc");
            System.out.println(x);
            if (CollectionUtils.isEmpty(list))
                break;
            for (Object obj : list) {
                process((Map<String, Object>) obj);

            }
        }
    }

    private void process(Map<String, Object> obj) throws IOException {
        //取出title
        ArrayList<String> title = (ArrayList) obj.get("title");
        String content = (String) obj.get("content_css");
        //写文件
        content = content.replaceAll("(?is)<.*?>", "").replaceAll("\n|　", "").trim();

        write(title.get(0), content);
    }

    private void write(String title, String s) {
        File file = new File(input, (num++) + ".txt");
        file.getParentFile().mkdirs();
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file, false)));
//            bw.write(Entropy.cal(hd.e.text())+"\n");
//            bw.write(title);
            bw.write(title.replaceAll("_.*",""));
//            bw.write(s);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
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
