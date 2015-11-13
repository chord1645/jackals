package jackals.lab.shares;

import com.alibaba.fastjson.JSON;
import jackals.downloader.HttpDownloader;
import jackals.downloader.ReqCfg;
import jackals.lab.FileUtil;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.classification.*;
import smile.data.NumericAttribute;
import smile.math.*;
import smile.math.distance.EuclideanDistance;
import smile.math.distance.MahalanobisDistance;
import smile.math.distance.ManhattanDistance;
import smile.math.kernel.GaussianKernel;
import smile.math.rbf.RadialBasisFunction;
import smile.util.SmileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//@Ignore
public class Shares {
    private Logger logger = LoggerFactory.getLogger(getClass());

    //////////////////////////////////
//    int k = smile.math.Math.max(label) + 1;
//    NeuralNetwork net = null;
//    if (k == 2) {
//        net = new NeuralNetwork(NeuralNetwork.ErrorFunction.CROSS_ENTROPY, NeuralNetwork.ActivationFunction.LOGISTIC_SIGMOID, data[0].length, units, 1);
//    } else {
//        net = new NeuralNetwork(NeuralNetwork.ErrorFunction.CROSS_ENTROPY, NeuralNetwork.ActivationFunction.SOFTMAX, data[0].length, units, k);
//    }
//
//    for (int i = 0; i < epochs; i++) {
//        net.learn(data, label);
//    }

    @Test
    public void knn() throws Exception {
//        Data train = loadData("D:\\tmp\\calculate\\300.txt");
        Data train = loadData("D:\\tmp\\calculate\\all.txt");
        Data test = loadData("D:\\tmp\\calculate\\400.txt");
        KNN<double[]> knn = KNN.learn(train.matrix, train.label, 1);

        int error = 0;
        for (int i = 0; i < train.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            if (knn.predict(train.matrix[i]) != train.label[i]) {
                error++;
            }
        }
        System.out.println("error1:" + error);
        System.out.println("error1:" + error * 1.0 / train.matrix.length);
        error = 0;
        for (int i = 0; i < test.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            if (knn.predict(test.matrix[i]) != test.label[i]) {
                error++;
            }

        }
        System.out.println("error2:" + error);
        System.out.println("error2:" + error * 1.0 / test.matrix.length);

    }


    @Test
    public void runSVM() throws Exception {
        Data train = loadData("D:\\tmp\\calculate\\300.txt");
//        Data train = loadData("D:\\tmp\\calculate\\all.txt");
        Data test = loadData("D:\\tmp\\calculate\\400.txt");

        SVM<double[]> svm = new SVM<double[]>(new GaussianKernel(0.5), 1.0, 3, SVM.Multiclass.ONE_VS_ALL);
        svm.learn(train.matrix, train.label);
        svm.finish();


        int error = 0;
        for (int i = 0; i < train.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            if (svm.predict(train.matrix[i]) != train.label[i]) {
                error++;
            }

        }
        System.out.println("error1:" + error);
        System.out.println("error1:" + error * 1.0 / train.matrix.length);
        error = 0;
        for (int i = 0; i < test.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            if (svm.predict(test.matrix[i]) != test.label[i]) {
                error++;
            }

        }
        System.out.println("error2:" + error);
        System.out.println("error2:" + error * 1.0 / test.matrix.length);

    }

    @Test
    public void runRBF() throws Exception {
        Data train = loadData("D:\\tmp\\calculate\\300.txt");
//        Data train = loadData("D:\\tmp\\calculate\\all.txt");
        Data test = loadData("D:\\tmp\\calculate\\400.txt");
        double[][] centers = new double[150][];
        RadialBasisFunction basis = SmileUtils.learnGaussianRadialBasis(train.matrix, centers);
//        RBFNetwork<double[]> rbf = new RBFNetwork<double[]>(train.matrix, train.label, new ManhattanDistance(), basis, centers);
        RBFNetwork<double[]> rbf = new RBFNetwork<double[]>(train.matrix, train.label, new EuclideanDistance(), basis, centers);

        int error = 0;
        for (int i = 0; i < train.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            if (rbf.predict(train.matrix[i]) != train.label[i]) {
                error++;
            }

        }
        System.out.println("error1:" + error);
        System.out.println("error1:" + error * 1.0 / train.matrix.length);
        error = 0;
        for (int i = 0; i < test.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            if (rbf.predict(test.matrix[i]) != test.label[i]) {
                error++;
            }

        }
        System.out.println("error2:" + error);
        System.out.println("error2:" + error * 1.0 / test.matrix.length);

    }

    @Test
    public void run() throws Exception {
//        Data train = loadData("D:\\tmp\\calculate\\test.txt");
//        Data train = loadData("D:\\tmp\\calculate\\300.txt");
        Data train = loadData("D:\\tmp\\calculate\\all.txt");
        Data test = loadData("D:\\tmp\\calculate\\400.txt");
//        Data test = loadData("D:\\tmp\\calculate\\000063.txt");
        DecisionTree tree = new DecisionTree(train.matrix, train.label, 5000, DecisionTree.SplitRule.ENTROPY);
        double error = 0;
        for (int i = 0; i < train.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            int result = tree.predict(train.matrix[i]);
            if (result != train.label[i]) {
                error++;
            }
//            System.out.println(result + "\t" + test.rows[i]);

        }
        System.out.println("error1:" + error);
        System.out.println("error1:" + error / train.matrix.length);
        error = 0;
        for (int i = 0; i < test.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            int result = tree.predict(test.matrix[i]);
            if (result != test.label[i]) {
                error++;
            }
//            System.out.println(result + "\t" + test.rows[i]);
        }
        System.out.println("error2:" + error);
        System.out.println("error2:" + error / test.matrix.length * 100);
//        System.out.println("error2:" + new DecimalFormat("###.######").format(error / test.matrix.length));

    }

    private Data loadData(String file) {
        Data data = new Data();
        String strData = FileUtil.read(new File(file));
        String[] rows = strData.split("\n");
        data.matrix = new double[rows.length][];
        data.label = new int[rows.length];
        for (int x = 0; x < rows.length; x++) {

            String[] colArr = rows[x].split("\\s");
            data.matrix[x] = new double[13];
//            System.out.println(colArr.length);
//            for (int y = 0; y < 7; y++) {
//                data.matrix[x][y] = Double.valueOf(colArr[y + 7]);
//            }
            data.matrix[x][0] = Double.valueOf(colArr[7]);
            data.matrix[x][1] = Double.valueOf(colArr[8]);
            data.matrix[x][2] = Double.valueOf(colArr[9]);
            data.matrix[x][3] = Double.valueOf(colArr[10]);
            data.matrix[x][4] = Double.valueOf(colArr[12]);
            data.matrix[x][5] = Double.valueOf(colArr[13]);
            data.matrix[x][6] = Double.valueOf(colArr[1]);
            data.matrix[x][7] = Double.valueOf(colArr[2]);
            data.matrix[x][8] = Double.valueOf(colArr[3]);
            data.matrix[x][9] = Double.valueOf(colArr[4]);
            data.matrix[x][10] = Double.valueOf(colArr[5]);
            data.matrix[x][11] = Double.valueOf(colArr[6]);
            data.matrix[x][12] = Double.valueOf(colArr[11]);

            data.label[x] = Integer.valueOf(colArr[15]);
        }
        data.rows = rows;
//        NumericAttribute[] attributes = new NumericAttribute[data.matrix[0].length];
//        attributes[0] = new NumericAttribute("c1", null, 1.0);
//        attributes[1] = new NumericAttribute("c2", null, 1.0);
//        attributes[2] = new NumericAttribute("c3", null, 1.0);
//        attributes[3] = new NumericAttribute("c4", null, 1.0);
//        attributes[4] = new NumericAttribute("c5", null, 1.0);
//        attributes[5] = new NumericAttribute("c6", null, 1.0);
//        attributes[6] = new NumericAttribute("c7", null, 1.0);
        return data;
    }


    class Data {
        String[] rows;
        double[][] matrix;
        int[] label;
        NumericAttribute[] attributes;
    }

    @Test
    public void calculateAll() throws Exception {
        File root = new File("D:\\tmp\\code_data");
        int cnt = 0;
        for (File f : root.listFiles()) {
            System.out.println(cnt++);
//            if (!f.getName().equals("000063.txt")) {
//                continue;
//            }
            if (cnt < 400)
                continue;
            if (cnt > 450)
                break;
            String[] txt = FileUtil.read(f).split("\n");
//        String[] txt = FileUtil.read(new File("D:\\tmp\\code_data\\600000.txt")).split("\n");
            Code[] codes = new Code[txt.length];
            for (int i = 0; i < txt.length; i++) {
                codes[i] = new Code(txt[i]);
            }
            for (int i = 0; i < codes.length; i++) {
                calculate(i, codes);
            }
            for (int i = 0; i < codes.length; i++) {
                calculate(i, codes);
                if (codes[i].done)
                    FileUtil.write(new File("D:\\tmp\\calculate\\400.txt"), codes[i].calculateStr() + "\n", true);
            }
        }

    }

    //        									30日涨
    private void calculate(int i, Code[] codes) {
        try {
            Code code = codes[i];
            //均价变动
            code.c1 = (codes[i].priceAvg - codes[i - 1].priceAvg) / codes[i - 1].priceAvg * 100;
            //最高变动
            code.c2 = (codes[i].highest - codes[i - 1].highest) / codes[i - 1].highest * 100;
            //最低变动
            code.c3 = (codes[i].lowest - codes[i - 1].lowest) / codes[i - 1].lowest * 100;
            //高低差变动
            if (codes[i - 1].diff == 0) {
                code.c4 = 100;
            } else {
                code.c4 = (codes[i].diff - codes[i - 1].diff) / codes[i - 1].diff * 100;
            }
            //量比
            double quantitySum = 0;
            for (int x = i - 5; x < i; x++) {
                quantitySum += codes[x].quantity / 240;
            }
            code.c5 = code.quantityAvg / (quantitySum / 5);
            //收盘5日涨幅
            code.c6 = (codes[i].end - codes[i - 5].end) / codes[i - 5].end * 100;
            //收盘10日涨幅
            code.c7 = (codes[i].end - codes[i - 10].end) / codes[i - 10].end * 100;
            //5日涨
            code.c8 = codes[i].end < codes[i + 5].end ? 1 : 0;
            //10日涨 30%
            double rise = (codes[i + 10].end - codes[i].end) / codes[i].end;
            //0=涨幅>10%,
            // 1=跌幅>10%,
            // 2=-10%<区间<10%
            if (rise > 0.1)
                code.c9 = 0;
            else if (rise < -0.1) {
                code.c9 = 1;
            } else {
                code.c9 = 2;
            }
            //30日涨
            code.c10 = codes[i].end < codes[i + 30].end ? 1 : 0;
            code.done = true;
        } catch (Throwable e) {

        }


    }

    //    000063
    @Test
    public void downloadData1() throws Exception {
        List<Code> codes = new ArrayList<Code>();
        String code = "000063";
        for (int y = 2015; y <= 2015; y++) {  //循环年
            for (int z = 1; z <= 4; z++) {                //循环季度
                String url = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/" + code + ".phtml?year=" + y + "&jidu=" + z;
                codes.addAll(onePage(url));
            }
        }
        Collections.sort(codes, new Comparator<Code>() {
            @Override
            public int compare(Code o1, Code o2) {
                return o1.date.compareTo(o2.date);
            }
        });
        for (Code c : codes) {
            FileUtil.write(new File("D:\\tmp\\code_data\\" + code + ".txt"), c.toString() + "\n", true);
        }
    }

    @Test
    public void downloadData() throws Exception {
//        Data train = onePage();
        //循环股票代码
        String[] txt = FileUtil.read(new File("D:\\tmp\\codes.txt")).split("\n");
        go1:
        for (int x = 0; x < txt.length; x++) {
            List<Code> codes = new ArrayList<Code>();
            String code = txt[x];
            for (int y = 2006; y <= 2009; y++) {  //循环年
                for (int z = 1; z <= 4; z++) {                //循环季度
                    String url = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/" + code + ".phtml?year=" + y + "&jidu=" + z;
                    try {
                        codes.addAll(onePage(url));
                    } catch (Throwable e) {
                        continue go1;
                    }

                }
            }
            Collections.sort(codes, new Comparator<Code>() {
                @Override
                public int compare(Code o1, Code o2) {
                    return o1.date.compareTo(o2.date);
                }
            });
            for (Code c : codes) {
                FileUtil.write(new File("D:\\tmp\\code_data\\" + code + ".txt"), c.toString() + "\n", true);
            }
        }

    }

    HttpDownloader downloader = new HttpDownloader(1);

    //http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/000063.phtml?year=2006&jidu=3
    public List<Code> onePage(String url) {
        List<Code> codes = new ArrayList<Code>();
        PageObj page = downloader.download(new RequestOjb(url),
                ReqCfg.deft().setTimeOut(10000));
        Document doc = Jsoup.parse(page.getRawText());
        Elements elements = doc.select("table#FundHoldSharesTable>tbody>tr");
        elements.remove(0);
        for (Element e : elements) {
            Code code = new Code();
            code.date = e.child(0).text();//            日期
            code.start = Double.valueOf(e.child(1).text());//            开盘价
            code.highest = Double.valueOf(e.child(2).text());//            最高价
            code.end = Double.valueOf(e.child(3).text());//            收盘价
            code.lowest = Double.valueOf(e.child(4).text());//            最低价
            code.quantity = Double.valueOf(e.child(5).text());//            交易量(股)
            code.money = Double.valueOf(e.child(6).text());//            交易金额(元)
            logger.info("{}", JSON.toJSONString(code));
            codes.add(code);
        }
        return codes;

    }


}
