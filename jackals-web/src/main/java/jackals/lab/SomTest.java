package jackals.lab;

import cn.nhorizon.commons.classfier.constant.Constants;
import cn.nhorizon.commons.classfier.service.VSMService;
import cn.nhorizon.commons.classfier.utils.CHNWVTTokenizer;
import com.alibaba.fastjson.JSON;
import edu.udo.cs.wvtool.main.WVTWordVector;
import smile.clustering.DBScan;
import smile.clustering.SOM;
import smile.math.distance.EuclideanDistance;

import java.io.File;
import java.util.HashMap;

public class SomTest {
    static VSMService service;

    public static void main(String[] args) {
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("DF_MIN", Constants.DF_MIN);
        config.put("DF_MAX", Constants.DF_MAX);
        config.put("THERSAUS", "");// 同义词库
        config.put("WEIGHT", Constants.WEIGHT);
        config.put("CORPUS", "D:\\work\\tmp\\CORPUS");
        config.put("ENCODE_TRAIN", "utf-8");
        config.put("ENCODE_RECOGNIZE", "utf-8");
        VSMService.build(config, new CHNWVTTokenizer());
        service = VSMService.getInstance();
        File root = new File("D:\\work\\tmp\\CORPUS");
        int x = 0;
        double[][] data = new double[root.list().length][];
        for (File file : root.listFiles()) {
            Doc doc = buildDoc(x, file);
//            System.out.println(doc.vector.getTFIDFValues());
            data[x] = doc.vector.getValues();
            x++;
        }
        cluster(data);

        System.currentTimeMillis();
    }

    private static void cluster(double[][] data) {
        long clock = System.currentTimeMillis();
                SOM som = new SOM(data, 10, 10);

        System.out.println(JSON.toJSONString(som.partition(5)));
//        for (int k = 0; k < dbscan.getNumClusters(); k++) {
//            double[][] cluster = new double[dbscan.getClusterSize()[k]][];
//            for (int i = 0, j = 0; i < dataset[datasetIndex].length; i++) {
//                if (dbscan.getClusterLabel()[i] == k) {
//                    cluster[j++] = dataset[datasetIndex][i];
//                }
//            }
//
//            plot.points(cluster, pointLegend, Palette.COLORS[k % Palette.COLORS.length]);
//        }
    }

    private static Doc buildDoc(int x, File file) {
        String s = FileUtil.read(file);
        WVTWordVector vector = service.getNativeVector(s);
        return new Doc(vector, file, x);
    }

    public static class Doc {
        File file;
        int id;
        WVTWordVector vector;

        public Doc(WVTWordVector vector, File file, int id) {
            this.vector = vector;
            this.file = file;
            this.id = id;
        }
    }
}
