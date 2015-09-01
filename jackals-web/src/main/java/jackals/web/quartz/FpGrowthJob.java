package jackals.web.quartz;

import com.alibaba.fastjson.JSON;
import jackals.model.WordGroup;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
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


public class FpGrowthJob {
    @Autowired
    SolrServer solrServer;
    @Autowired
    StringRedisTemplate redisTemplate;

    String output = "/usr/appdir/jackals-web/output.txt";
    static String input = "/usr/appdir/jackals-web/dataset_0824.txt";
    static int minSupport = 5;
    static int maxHeapSize = 30;//top-k
    public static String fpgrowthKey = "fpgrowth_key";

    public void execute() throws IOException {
        buildData();
        List<WordGroup> list = fpGrowth();
        redisTemplate.delete(fpgrowthKey);
        for (WordGroup wordGroup : list) {
            redisTemplate.opsForZSet().add(fpgrowthKey, JSON.toJSONString(wordGroup), wordGroup.getTimes());
        }
    }

    public void buildData() throws IOException {
        System.out.println("delete:" + new File(input).delete());
        String query = buildQuery();
//        String query = "title:(三大 运营商)";
        for (int x = 1; ; x++) {
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

    //    @Test
    public List<WordGroup> fpGrowth() throws IOException {
        Set<String> features = new HashSet<String>();
        String pattern = " ";
        Charset encoding = Charset.forName("UTF-8");
        FPGrowth<String> fp = new FPGrowth<String>();
        Path path = new Path(output);
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        SequenceFile.Writer writer = new SequenceFile.Writer(fs, conf, path, Text.class, TopKStringPatterns.class);

        fp.generateTopKFrequentPatterns(
                new StringRecordIterator(new FileLineIterable(new File(input), encoding, false), pattern),
                fp.generateFList(
                        new StringRecordIterator(new FileLineIterable(new File(input), encoding, false), pattern),
                        minSupport),
                minSupport,
                maxHeapSize,
                features,
                new StringOutputConverter(new SequenceFileOutputCollector<Text, TopKStringPatterns>(writer)),
                new ContextStatusUpdater(null));
        writer.close();
//        Set<List<String>> set = new HashSet<List<String>>();
        List<WordGroup> wordGroupList = new ArrayList<WordGroup>();
        List<Pair<String, TopKStringPatterns>> frequentPatterns = FPGrowth.readFrequentPattern(conf, path);
        for (Pair<String, TopKStringPatterns> entry : frequentPatterns) {

            TopKStringPatterns sec = entry.getSecond();
            List<Pair<List<String>, Long>> pairList = sec.getPatterns();
            pairList.get(pairList.size() - 1);
            for (Pair<List<String>, Long> pair : pairList) {
//                System.out.println(pair.getFirst() + "" + pair.getSecond()); // the frequent patterns meet minSupport and support
                WordGroup group = new WordGroup(pair.getFirst(), pair.getSecond());
                if (pair.getFirst().size() < 2)
                    continue;
                if (wordGroupList.contains(group)) {
                    continue;
                } else {
                    wordGroupList.add(group);
                }
            }
        }
        filter(wordGroupList);
        System.out.println("the end! ");
        Collections.sort(wordGroupList, new Comparator<WordGroup>() {
            @Override
            public int compare(WordGroup o1, WordGroup o2) {
                return (int) (o2.getTimes() - o1.getTimes());
            }
        });
        for (WordGroup group : wordGroupList) {
            System.out.println(group.getWords() + " " + group.getTimes());
        }
        return wordGroupList;
    }

    private void filter(List<WordGroup> wordGroupList) {
        List<WordGroup> tmp = new ArrayList<WordGroup>(wordGroupList);
//        Collections.copy(tmp, wordGroupList);
        go1:
        for (Iterator<WordGroup> it = wordGroupList.iterator(); it.hasNext(); ) {
            WordGroup w1 = it.next();
            for (WordGroup w2 : tmp) {
                if (w1.getWords().size() < w2.getWords().size()
                        && w2.getWords().containsAll(w1.getWords())) {
                    it.remove();
                    continue go1;
                }
            }
        }

    }

    private void process(Map<String, Object> obj) throws IOException {
        //取出title
        ArrayList<String> title = (ArrayList) obj.get("title");
        //分词
        if (CollectionUtils.isEmpty(title))
            return;
        String tokens = tokens(title.get(0));
        //写文件
        write(tokens);
    }

    private String tokens(String title) throws IOException {
        title = title.replaceAll("_.*", "");
        IKAnalyzer analyzer = new IKAnalyzer(true);
        StringReader reader = new StringReader(title);
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        TokenStream ts = analyzer.tokenStream("", reader);
        ts.reset();
        CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);

        try {
            while (ts.incrementToken()) {
                String t = term.toString();
                if (t.length() < 2)
                    continue;
                set.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
        StringBuffer buff = new StringBuffer();
        for (String s : set)
            buff.append(s).append(" ");
        return buff.toString();
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