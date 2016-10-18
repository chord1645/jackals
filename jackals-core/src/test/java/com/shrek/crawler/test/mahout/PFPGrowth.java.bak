package com.shrek.crawler.test.mahout;

import com.shrek.crawler.test.BaseTest;
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
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


public class PFPGrowth extends BaseTest {
    @Autowired
    SolrServer solrServer;

    public static void main(String[] args) throws IOException {


    }

    static String input = "D:\\work\\workspace\\crawler\\jackals\\src\\test\\java\\com\\shrek\\crawler\\test\\mahout\\dataset_0824.txt";
    static int minSupport = 1;
    static int maxHeapSize = 50;//top-k

    @Test
    public void test() throws IOException {
        buildData();
        fpGrowth();

    }

    @Test
    public void buildData() throws IOException {
        System.out.println("delete:" + new File(input).delete());

        for (int x = 1; x < 100; x++) {
            List list = sortList("jobId_t:news.163.com AND infoTime_dt:[2015-08-24T00:03:07.147Z TO 2015-08-24T17:03:07.147Z ]", x, 100, "saveTime_dt desc");
//            List list = sortList("jobId_t:news.163.com", x, 100, "saveTime_dt desc");
            System.out.println(x);
            if (CollectionUtils.isEmpty(list))
                break;
            for (Object obj : list) {
                process((Map<String, Object>) obj);

            }
        }
    }
    @Test
    public void fpGrowth() throws IOException {
        Set<String> features = new HashSet<String>();

        String pattern = " ";
        Charset encoding = Charset.forName("UTF-8");
        FPGrowth<String> fp = new FPGrowth<String>();
        String output = "D:\\work\\workspace\\crawler\\jackals\\src\\test\\java\\com\\shrek\\crawler\\test\\mahout\\output.txt";
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
            for (Pair<List<String>, Long> pair : pairList) {
                WordGroup group = new WordGroup(pair.getFirst(), pair.getSecond());
                if (pair.getFirst().size() < 2)
                    continue;
                if (wordGroupList.contains(group)) {
                    continue;
                } else {
                    wordGroupList.add(group);
                }
//                System.out.println(pair.getFirst() + "" + pair.getSecond()); // the frequent patterns meet minSupport and support
            }
        }

        System.out.print("\nthe end! ");
        Collections.sort(wordGroupList, new Comparator<WordGroup>() {
            @Override
            public int compare(WordGroup o1, WordGroup o2) {
                return (int) (o2.times - o1.times);
            }
        });
        for (WordGroup group : wordGroupList) {
            System.out.println(group.words + " " + group.times);
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
        title = title.replaceAll("网易新闻中心", "");
        IKAnalyzer analyzer = new IKAnalyzer(true);
        StringReader reader = new StringReader(title);
        StringBuffer buff = new StringBuffer();
        TokenStream ts = analyzer.tokenStream("", reader);
        ts.reset();
        CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);

        try {
            while (ts.incrementToken()) {
                String t = term.toString();
                if (t.length() < 2)
                    continue;
                buff.append(t).append(" ");
//                System.out.print(term.toString()+"|");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            reader.close();
        }
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



    static class WordGroup {
        List<String> words;
        Long times;

        public WordGroup(List<String> words, Long times) {
            this.words = words;
            this.times = times;
        }

        @Override
        public boolean equals(Object obj) {
            return this.words.equals(((WordGroup) obj).words);
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