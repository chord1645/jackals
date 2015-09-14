package jackals.lab;

import cn.nhorizon.commons.classfier.constant.Constants;
import cn.nhorizon.commons.classfier.service.VSMService;
import cn.nhorizon.commons.classfier.utils.CHNWVTTokenizer;
import com.alibaba.fastjson.JSON;
import edu.udo.cs.wvtool.main.WVTWordVector;
import smile.clustering.DBScan;
import smile.math.distance.*;
import smile.neighbor.KDTree;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbscanTest {
    static VSMService service;
    static List<Doc> list = new ArrayList<Doc>();
    static File root = new File("D:\\work\\tmp\\CORPUS");
    static File output = new File("D:\\work\\tmp\\output");

    public static void main(String[] args) {
        HashMap<String, Object> config = new HashMap<String, Object>();
        config.put("DF_MIN", Constants.DF_MIN);
        config.put("DF_MAX", Constants.DF_MAX);
        config.put("THERSAUS", "");// 同义词库
        config.put("WEIGHT","Lucene");
        config.put("CORPUS", "D:\\work\\tmp\\CORPUS");
        config.put("ENCODE_TRAIN", "utf-8");
        config.put("ENCODE_RECOGNIZE", "utf-8");
        VSMService.build(config, new CHNWVTTokenizer());
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
        EuclideanDistance ed =new EuclideanDistance();
        long s = System.currentTimeMillis();
        ed.d(list.get(0).vector.getValues(),list.get(1).vector.getValues());
        System.out.println(System.currentTimeMillis()-s);
//        cluster(data);
        write(cluster(data));
        System.currentTimeMillis();
    }

    private static void write(DBScan<double[]> dbscan) {
        System.out.println(dbscan.toString());
        int[] lab = dbscan.getClusterLabel();
        for (int x = 0; x < lab.length; x++) {
            Doc doc = list.get(x);
            FileUtil.write(new File(output, lab[x] + "/" + doc.file.getName()), doc.text,false);
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

    private static DBScan<double[]> cluster(double[][] data) {
        long clock = System.currentTimeMillis();
        DBScan<double[]> dbscan = new DBScan<double[]>(data, new KDTree<double[]>(data, data), 2, 1.2);
//        DBScan<double[]> dbscan = new DBScan<double[]>(data, new EuclideanDistance(), 2, 1.0);
        System.out.format("DBSCAN clusterings %d samples in %dms\n", data.length, System.currentTimeMillis() - clock);
        System.out.println("getNumClusters:" + dbscan.getNumClusters());
        System.out.println("getClusterSize:" + dbscan.getClusterSize());
        System.out.println(JSON.toJSONString(dbscan.getClusterSize()));
        return dbscan;

    }

    private static Doc buildDoc(int x, File file) {
        String s = FileUtil.read(file);
        WVTWordVector vector = service.getNativeVector(s.replaceAll("_.*", ""));
        return new Doc(vector, file, x, s);
    }

    public static class Doc {
        File file;
        int id;
        String text;
        WVTWordVector vector;

        public Doc(WVTWordVector vector, File file, int id, String s) {
            this.vector = vector;
            this.file = file;
            this.id = id;
            this.text = s;
        }
    }
}
