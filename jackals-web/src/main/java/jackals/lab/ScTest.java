package jackals.lab;

import cn.nhorizon.commons.classfier.constant.Constants;
import cn.nhorizon.commons.classfier.service.VSMService;
import cn.nhorizon.commons.classfier.utils.CHNWVTTokenizer;
import cn.nhorizon.commons.classfier.utils.FileTokenizer;
import edu.udo.cs.wvtool.main.WVTWordVector;
import smile.clustering.SpectralClustering;
import smile.math.*;
import smile.math.Math;
import smile.math.matrix.EigenValueDecomposition;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CHNWVTTokenizer 读文件的方法
 */
public class ScTest {
    static VSMService service;
    static List<Doc> list = new ArrayList<Doc>();
    static Boolean filter = true;
    static File root = new File("D:\\work\\tmp\\CORPUS1");
    static File output = new File("D:\\work\\tmp\\output");

    public static void main(String[] args) {
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("DF_MIN", Constants.DF_MIN);
        config.put("DF_MAX", Constants.DF_MAX);
        config.put("THERSAUS", "");// 同义词库
        config.put("WEIGHT", "Lucene");
        config.put("CORPUS", "D:\\work\\tmp\\CORPUS1");
        config.put("ENCODE_TRAIN", "utf-8");
        config.put("ENCODE_RECOGNIZE", "utf-8");
        VSMService.build(config, new FileTokenizer());
        service = VSMService.getInstance();

        int x = 0;
        double[][] data = new double[root.list().length][];

        for (File file : root.listFiles()) {
            Doc doc = buildDoc(x, file);
//            System.out.println(doc.vector.getTFIDFValues());
            data[x] = doc.vector.getValues();
            list.add(doc);
            x++;
        }
//        cluster(data);
//        write(cluster(data));
        cluster(data, 5);
        print(result);
        System.currentTimeMillis();
    }

    private static void print(SpectralClustering cluster) {
        Map<Integer, List<Doc>> map = new HashMap<Integer, List<Doc>>();
        int[] lab = cluster.getClusterLabel();
        File output = new File("D:\\work\\tmp\\output" + cluster.getNumClusters());
        for (int x = 0; x < lab.length; x++) {
            final Doc doc = list.get(x);
            FileUtil.write(new File(output, lab[x] + ".txt"), doc.text, true);
            if (!map.containsKey(new Integer(lab[x]))) {
                map.put(new Integer(lab[x]), new ArrayList<Doc>() {
                    {
                        add(doc);
                    }
                });
            } else {
                map.get(new Integer(lab[x])).add(doc);
            }
//            double[][] cluster = new double[dbscan.getClusterSize()[k]][];
//            for (int i = 0, j = 0; i < dataset[datasetIndex].length; i++) {
//                if (dbscan.getClusterLabel()[i] == k) {
//                    cluster[j++] = dataset[datasetIndex][i];
//                }
//            }
//
//            plot.points(cluster, pointLegend, Palette.COLORS[k % Palette.COLORS.length]);
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

    private static void write(SpectralClustering dbscan) {
        System.out.println(dbscan.toString());
        int[] lab = dbscan.getClusterLabel();
        for (int x = 0; x < lab.length; x++) {
            Doc doc = list.get(x);
            FileUtil.write(new File(output, lab[x] + "/" + doc.file.getName()), doc.text, false);
//            double[][] cluster = new double[dbscan.getClusterSize()[k]][];
//            for (int i = 0, j = 0; i < dataset[datasetIndex].length; i++) {
//                if (dbscan.getClusterLabel()[i] == k) {
//                    cluster[j++] = dataset[datasetIndex][i];
//                }
//            }
//
//            plot.points(cluster, pointLegend, Palette.COLORS[k % Palette.COLORS.length]);
        }
    }

    static SpectralClustering result;

    private static void cluster(double[][] data, int k) {
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

//        for (int x = 0; x < lab.length; x++) {
//            if (lab[x] < 0.8) {
//                more = false;
//                break;
//            }
//        }
        double sd = smile.math.Math.sd(eigen.getEigenValues());

        System.out.println("sd(eigen.getEigenValues()):" + sd);
        if (Math.min(eigen.getEigenValues()) > 0.3) {
            result = cluster;
            cluster(data, k + 1);
        } else {

            return;
        }
    }

    static class WordSort {
        String word;
        Double score;

        public WordSort(String word, double score) {
            this.word = word;
            this.score = score;
        }
    }

    private static Doc buildDoc(int x, File file) {
        double minScore = 0.05;
        String s = FileUtil.read(file);
        String[] arr = s.split("\n");
        WVTWordVector vector = service.getNativeVector(s);
        if (filter){
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

        return new Doc(vector, file, x, arr[2],arr[0],arr[1]);
    }

    public static class Doc {
        File file;
        int id;
        String text;
        String url;
        String title;
        WVTWordVector vector;

        public Doc(WVTWordVector vector, File file, int id, String s,String url,String title) {
            this.vector = vector;
            this.file = file;
            this.id = id;
            this.text = s;
            this.url=url;
            this.title =title;
        }
    }
}
