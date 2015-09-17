package jackals.lab;

import cn.nhorizon.commons.classfier.constant.Constants;
import cn.nhorizon.commons.classfier.service.VSMService;
import cn.nhorizon.commons.classfier.utils.FileTokenizer;
import edu.udo.cs.wvtool.main.WVTWordVector;
import jackals.solr.IndexDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrDocument;
import org.springframework.beans.factory.annotation.Autowired;
import smile.clustering.SpectralClustering;
import smile.math.Math;
import smile.math.matrix.EigenValueDecomposition;

import java.io.File;
import java.util.*;

/**
 * CHNWVTTokenizer 读文件的方法
 */
public class SimilarFilter {
    String hr = "---------------------------------------------------------\n";
    VSMService service;
    List<Doc> list = new ArrayList<Doc>();
    Boolean filter = true;
    File root1 = new File("D:\\work\\tmp\\CORPUS1");
    File root = new File("D:\\work\\tmp\\CORPUS");
    File output = new File("D:\\work\\tmp\\output");
    SpectralClustering result;
    @Autowired
    IndexDao indexDao;

    //分类数最大一次,然后根据分数在分一次,两次ok
    public static void main(String[] args) {
        SimilarFilter scTest = new SimilarFilter();
        scTest.init();
        scTest.main();
    }

    public void init() {
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("DF_MIN", Constants.DF_MIN);
        config.put("DF_MAX", Constants.DF_MAX);
        config.put("THERSAUS", "");// 同义词库
        config.put("WEIGHT", "Lucene");
        config.put("CORPUS", root1.getPath());
        config.put("ENCODE_TRAIN", "utf-8");
        config.put("ENCODE_RECOGNIZE", "utf-8");
        VSMService.build(config, new FileTokenizer());
        service = VSMService.getInstance();
//        _dimension();
//        config.put("CORPUS", root.getPath());
//        VSMService.build(config, new FileTokenizer());
//        service = VSMService.getInstance();
    }

    private void _dimension() {
        FileUtil.clean(root);
        int x = 0;
        for (File file : root1.listFiles()) {
            Doc doc = buildDoc(x, file);
//            System.out.println(doc.vector.getTFIDFValues());
            Iterator<Map.Entry<String, Double>> it = doc.vector.getTFIDFValues().entrySet().iterator();
            for (; it.hasNext(); ) {
                if (it.next().getValue() < 0.05) {
                    it.remove();
                }
            }
            String str = StringUtils.join(doc.vector.getTFIDFValues().keySet().iterator(), " ");
            str = doc.url + "\n" + doc.title + "\n" + str + "\n" + doc.text;
            FileUtil.write(new File(root, file.getName()), str, false);
            x++;
        }
    }

    public void main() {
        double[][] data = buildData();
        SpectralClustering clustering = cluster(data, data.length - 1);
        double[] eigenValues = clustering.getEigen().getEigenValues();
        int count = bestCount(eigenValues);
//        for (;count<100;){
        clustering = cluster(data, count);
//        }
//        SpectralClustering clustering = cluster(data, 379);
        print(clustering);
        writeFile(clustering);
        writeSolr(clustering);
        System.currentTimeMillis();
    }


    private int bestCount(double[] lab) {
        double amount = 0;
        for (int i = 0; i < lab.length; i++) {
            if (lab[i] < 0)
                break;
            amount += lab[i];
        }
        double sum = 0;
        for (int i = 0; i < lab.length; i++) {
            sum += lab[i];
            if (sum / amount > .95)
                return i;
        }
        return 0;
    }

    private void print(SpectralClustering cluster) {
        Map<Integer, List<Doc>> map = new HashMap<Integer, List<Doc>>();
        int[] lab = cluster.getClusterLabel();
        for (int x = 0; x < lab.length; x++) {
            final Doc doc = list.get(x);
            if (!map.containsKey(new Integer(lab[x]))) {
                map.put(new Integer(lab[x]), new ArrayList<Doc>() {
                    {
                        add(doc);
                    }
                });
            } else {
                map.get(new Integer(lab[x])).add(doc);
            }
        }
        for (Map.Entry<Integer, List<Doc>> e : map.entrySet()) {
            System.out.println("type:" + e.getKey());
            if (e.getValue().size() < 10) {
                for (Doc d : e.getValue()) {
                    System.out.println("-------------------------------------");
                    System.out.println("url:" + d.url);
                    System.out.println("title:" + d.title);
                    System.out.println("doc:" + d.text);
                }
            }
            System.out.println("==============================");

        }
    }

    private double[][] buildData() {
        int x = 0;
        double[][] data = new double[root1.list().length][];

        for (File file : root1.listFiles()) {
            Doc doc = buildDoc(x, file);
//            System.out.println(doc.vector.getTFIDFValues());
            data[x] = doc.vector.getValues();
            list.add(doc);
            x++;
        }
        return data;
    }

    private void writeFile(SpectralClustering cluster) {
        FileUtil.clean(output);
        int[] lab = cluster.getClusterLabel();
        for (int x = 0; x < lab.length; x++) {
            final Doc doc = list.get(x);
            String one = doc.url + "\n" + doc.title + "\n" + doc.text + "\n" + hr;
            FileUtil.write(new File(output, lab[x] + ".txt"), one, true);
        }
    }

    private void writeSolr(SpectralClustering cluster) {
        Map<Integer, List<Doc>> map = new HashMap<Integer, List<Doc>>();
        int[] lab = cluster.getClusterLabel();
        for (int x = 0; x < lab.length; x++) {
            final Doc doc = list.get(x);
            if (!map.containsKey(new Integer(lab[x]))) {
                map.put(new Integer(lab[x]), new ArrayList<Doc>() {
                    {
                        add(doc);
                    }
                });
            } else {
                map.get(new Integer(lab[x])).add(doc);
            }
        }
        for (Map.Entry<Integer, List<Doc>> e : map.entrySet()) {
            System.out.println("type:" + e.getKey());

            if (e.getValue().size() < 10) {
                StringBuffer query = new StringBuffer();
                for (Doc doc : e.getValue()) {
                    query.append("url:\"").append(doc.url).append("\"");
                    query.append(" OR ");
                }
                query.delete(query.lastIndexOf("OR"), query.length());
                String groupId = UUID.nameUUIDFromBytes(query.toString().getBytes()).toString();
                List<Map<String, Object>> toSave = new ArrayList<Map<String, Object>>();
                System.out.println("group:" + groupId + " = " + e.getValue().size());
                List<SolrDocument> list = (List<SolrDocument>) indexDao.sortList(query.toString(), 1, 100, "infoTime_dt desc");
                Date newest = new Date(0);
                int useful=1;
                for (SolrDocument doc : list) {
                    Date date = (Date) doc.get("infoTime_dt");
                    if (date.getTime() > newest.getTime()) {

                    }
                    Map<String, Object> inputDoc = new HashMap<String, Object>(doc);
                    inputDoc.put("useful_i", useful);
                    inputDoc.put("sim_i", list.size());
                    inputDoc.put("group_s", groupId);
                    toSave.add(inputDoc);
                    useful=0;
                }
                indexDao.addIndex(toSave);
            }
        }
    }

    private SpectralClustering cluster(double[][] data, int k) {
        long clock = System.currentTimeMillis();
        SpectralClustering cluster = new SpectralClustering(data, k, 0.27);
        System.out.format("DBSCAN clusterings %d samples in %dms\n", data.length, System.currentTimeMillis() - clock);
        System.out.println("getNumClusters:" + cluster.getNumClusters());
        System.out.println("getClusterSize:" + cluster.getClusterSize());
//        System.out.println(JSON.toJSONString(dbscan.getClusterSize()));
        System.out.println("toString:" + cluster.toString());
        return cluster;
    }

    private void _cluster(double[][] data, int k) {
        long clock = System.currentTimeMillis();
        SpectralClustering cluster = new SpectralClustering(data, k, 0.355);
        System.out.format("DBSCAN clusterings %d samples in %dms\n", data.length, System.currentTimeMillis() - clock);
        System.out.println("getNumClusters:" + cluster.getNumClusters());
        System.out.println("getClusterSize:" + cluster.getClusterSize());
//        System.out.println(JSON.toJSONString(dbscan.getClusterSize()));
        System.out.println("toString:" + cluster.toString());
        /***************************************************************/
        boolean more = true;
        EigenValueDecomposition eigen = cluster.getEigen();
        double[] lab = eigen.getEigenValues();
        double sd = smile.math.Math.sd(eigen.getEigenValues());

        System.out.println("sd(eigen.getEigenValues()):" + sd);
        if (Math.min(eigen.getEigenValues()) > 0.3) {
            result = cluster;
            cluster(data, k + 1);
        } else {
            return;
        }
    }

    class WordSort {
        String word;
        Double score;

        public WordSort(String word, double score) {
            this.word = word;
            this.score = score;
        }
    }

    private Doc buildDoc(int x, File file) {
        double minScore = 0.05;
        String s = FileUtil.read(file);
        String[] arr = s.split("\n");
        WVTWordVector vector = service.getNativeVector(s);
        if (filter) {
            Map<String, Double> newMap = new HashMap<String, Double>();
            for (Map.Entry<String, Double> entry : vector.getTFIDFValues().entrySet()) {
                if (entry.getValue() < minScore) {
                    newMap.put(entry.getKey(), 0D);
                }
            }
            double[] values = vector.getValues();
            for (int i = 0; i < values.length; i++) {
                if (values[i] < minScore) {
                    values[i] = 0D;
                }
            }
        }

        return new Doc(vector, file, x, arr[2], arr[0], arr[1]);
    }

    public static class Doc {
        File file;
        int id;
        String text;
        String url;
        String title;
        WVTWordVector vector;

        public Doc(WVTWordVector vector, File file, int id, String s, String url, String title) {
            this.vector = vector;
            this.file = file;
            this.id = id;
            this.text = s;
            this.url = url;
            this.title = title;
        }
    }
}
