package jackals.lab;

import com.alibaba.fastjson.JSON;
import jackals.model.WordGroup;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.iterator.FileLineIterable;
import org.apache.mahout.common.iterator.StringRecordIterator;
import org.apache.mahout.fpm.pfpgrowth.convertors.ContextStatusUpdater;
import org.apache.mahout.fpm.pfpgrowth.convertors.SequenceFileOutputCollector;
import org.apache.mahout.fpm.pfpgrowth.convertors.string.StringOutputConverter;
import org.apache.mahout.fpm.pfpgrowth.convertors.string.TopKStringPatterns;
import org.apache.mahout.fpm.pfpgrowth.fpgrowth.FPGrowth;
import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;


public class Cluster {
    static String input = "/usr/appdir/jackals-web/cluster_1.txt";
    static String output = "/usr/appdir/jackals-web/cluster_2.txt";


    public void buildData() throws IOException {
        System.out.println("delete:" + new File(input).delete());
        String query = buildQuery();
//        String query = "title:(三大 运营商)";
        for (int x = 1;x<=1 ; x++) {
            List list = sortList(query, x, 10, "saveTime_dt desc");
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
        String title = (String) obj.get("content_css");
        //分词
//        if (CollectionUtils.isEmpty(title))
//            return;
//        String tokens = tokens(title.get(0));
        //写文件
        System.out.println(title.replaceAll("<.+?>",""));
        write(title);
    }

    private void write(String s) {
        File file = new File(input);
        file.getParentFile().mkdirs();
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file, true)));
//            bw.write(Entropy.cal(hd.e.text())+"\n");
            bw.write(s + "\n");
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Autowired
    SolrServer solrServer;

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

    private String buildQuery() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        StringBuffer query = new StringBuffer();
//        query.append("jobId_t:all.163.com");
//        query.append(" AND ");
        query.append("infoTime_dt:[").append(sdf.format(c.getTime())).append(" TO ").append(sdf.format(new Date())).append("]");
        return query.toString();
    }

    public void transformToSequenceFile() {
        //SequenceFilesFromDirectory 实现将某个文件目录下的所有文件写入一个 SequenceFiles 的功能
        // 它其实本身是一个工具类，可以直接用命令行调用，这里直接调用了它的 main 方法
        String[] args = {"-c", "UTF-8", "-i", input, "-o", output};
        // 解释一下参数的意义：
        // 	 -c: 指定文件的编码形式，这里用的是"UTF-8"
        // 	 -i: 指定输入的文件目录，这里指到我们刚刚导出文件的目录
        // 	 -o: 指定输出的文件目录

        try {
            SequenceFilesFromDirectory.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}