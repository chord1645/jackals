package jackals.lab.shares;

import com.google.common.collect.Lists;
import jackals.lab.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.classification.*;
import smile.data.NumericAttribute;
import smile.math.distance.EuclideanDistance;
import smile.math.kernel.GaussianKernel;
import smile.math.rbf.RadialBasisFunction;
import smile.util.SmileUtils;

import java.io.File;
import java.util.*;
import java.util.Random;

//@Ignore
public class Shares {
    private Logger logger = LoggerFactory.getLogger(getClass());
    String training = "D:\\tmp\\calculate\\0_100.txt";
    //    String training = "D:\\tmp\\calculate\\0_50.txt";
    static int checkData = 200;

    @Test
    public void runOneCode() throws Exception {
//        runCode("000858");
//        runCode("600000");
//        runCode("600373");
//        runCode("600198");
        runCode("600419");
    }

    @Test
    public void runCode() throws Exception {

        File f2015 = new File("D:\\tmp\\runCode\\orig");
        int x = 0;
        for (File f : f2015.listFiles()) {
            try {
                System.out.println(x++);
                runCode(f.getName().replaceAll("\\.txt", ""));
            } catch (LackDataException e) {
//                e.printStackTrace();
            }
        }
    }


    @Test
    public void find() throws Exception {
        //查找月均量比为正的股票
//        BlockExecutorPool executor = new BlockExecutorPool(10);
//        String[] txt = FileUtil.read(new File("D:\\tmp\\codes.txt")).split("\n");
//        for (final String s : txt) {
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        logger.info("download {}", s);
//                        downloadData(s, 2014, new File("D:\\tmp\\find2014\\" + s + ".txt"));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
        logger.info("download ======================================================");
        for (File code : new File("D:\\tmp\\find\\").listFiles()) {
            try {
                logger.info(code.getName());
                calculateFile(code, new File("D:\\tmp\\calculate2015\\" + code.getName()), "2015");
            } catch (LackDataException e) {
                logger.error("LackDataException", e);
            }
        }
        for (int x = 180; x > 0; x--) {
            int zl = 0;
            int jl = 0;

            String data = "";
            for (File code : new File("D:\\tmp\\calculate2008").listFiles()) {
                String[] arr = FileUtil.read(code).split("\n");
                if (arr.length < 180)
                    continue;
                String[] rowArr = arr[arr.length - x].split("\\s");
                double d1 = Double.valueOf(rowArr[23]);
                double liangbi = Double.valueOf(rowArr[11]);
                data = arr[arr.length - x].split("\\s")[0];
                int cnt = 0;
                if (d1 > 1) zl++;
                if (d1 < 1) jl++;
//            for (int i = arr.length - 10; i < arr.length; i++) {
//                double d = Double.valueOf(arr[i].split("\\s")[23]);
//                if (d < 1) {
//                    cnt++;
//                }
//            }
//            if (d1 >= 1 && cnt >= 3) {
//                System.out.println(code.getName());
//            }
            }
            System.out.println(data + "\t" + zl + "\t" + jl);
//            System.out.println(data+"\t"+zl+"\t"+jl);

        }

    }

    public void runCode(String code) throws Exception {
        File orig = new File(CodeUtil.origFileName(code));
        File calc = new File(CodeUtil.calcFileName(code));
//        File orig = new File("D:\\tmp\\find\\" + code + ".txt");
//        File calc = new File("D:\\tmp\\calculate2015\\" + code + ".txt");
        if (!orig.exists())
//            return;
            new HistoryDownloader().downloadData(code, 2015, orig);
        if (!calc.exists())
            calculateFile(orig, calc, "2015");

//        runDTree(orig, calc);
        runRBF(orig, calc);
//        runSVM(calc);
    }

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


    //    @Test
    public void runSVM(File code) throws Exception {
        Data train = loadData(training);
//        Data train = loadData("D:\\tmp\\calculate\\all.txt");
        Data test = loadData(code.getPath());

        SVM<double[]> svm = new SVM<double[]>(new GaussianKernel(0.5), 1.0, 3, SVM.Multiclass.ONE_VS_ALL);
        svm.learn(train.matrix, train.label);
        svm.finish();

        printAcc(code.getName(), train, svm);

        printAcc(code.getName(), test, svm);

    }

    private double printAcc(String name, Data train, Classifier classifier) {
        double error = 0;
        for (int i = 0; i < train.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            if (classifier.predict(train.matrix[i]) != train.label[i]) {
                error++;
            }

        }
//        System.out.println(name + " " + classifier.getClass().getSimpleName() + " error:" + error);
        double acc = (train.matrix.length - error) / train.matrix.length * 100;
        System.out.println(name + " " + classifier.getClass().getSimpleName() + "acc:" + acc);
        return acc;
    }

    RBFNetwork<double[]> rbf;

    public void runRBF(File orig, File calc) throws Exception {
        if (rbf == null) {
            train = loadData(training);
            double[][] centers = new double[100][];
            RadialBasisFunction basis = SmileUtils.learnGaussianRadialBasis(train.matrix, centers);
//        RBFNetwork<double[]> rbf = new RBFNetwork<double[]>(train.matrix, train.label, new ManhattanDistance(), basis, centers);
            rbf = new RBFNetwork<double[]>(train.matrix, train.label, new EuclideanDistance(), basis, centers);

        }
//        Data train = loadData("D:\\tmp\\calculate\\all.txt");
        Data test = loadData(calc.getPath());
        printAcc("train " + calc.getName(), train, rbf);
        double acc = printAcc("test " + calc.getName(), test, rbf);
        accSum += acc;
        count++;
        System.out.println("acc avg :" + accSum / count + "   ===============================");
        filter(calc, test, acc, rbf);

    }

    private void filter(File calc, Data test, double acc, Classifier classifier) {

//        if (acc < 85)
//            return;
        String str = FileUtil.read(calc);
        String[] arr = str.split("\n");
        String day1 = arr[arr.length - 3];
        String day2 = arr[arr.length - 2];
        String day3 = arr[arr.length - 1];
//        String day = FileUtil.readLastLine(calc);
        if (day3.startsWith("2015-11-24")) {
            int result1 = classifier.predict(CodeUtil.loadDay(day1.split("\\s")));
            int result2 = classifier.predict(CodeUtil.loadDay(day2.split("\\s")));
            int result3 = classifier.predict(CodeUtil.loadDay(day3.split("\\s")));
            logger.info("{} {} {} {}", calc.getName(), result1, result2, result3);
            if (result3 == 1)
//            if ((result1 == 0 || result2 == 0) && result3 == 1)
                writeResult(test, classifier, new File("D:\\tmp\\buy\\" + acc + "-" + calc.getName()));
        }


    }

    DecisionTree tree;
    Data train;


    int count = 0;
    double accSum = 0;

    //    @Test
    public void runDTree(File orig, File calc) throws Exception {
        if (tree == null) {
            train = loadData(training);
            tree = new DecisionTree(train.matrix, train.label, 550, DecisionTree.SplitRule.ENTROPY);
        }
//        Data train = loadData("D:\\tmp\\calculate\\000063.txt");

//        Data train = loadData("D:\\tmp\\calculate\\0_700.txt");
//        Data test = loadData("D:\\tmp\\calculate\\100_10.txt");
//        Data test = loadData("D:\\tmp\\calculate\\500.txt");
        Data test = loadData(calc.getPath());
//        Data test = loadData("D:\\tmp\\calculate\\600426.txt");

        printAcc("train " + calc.getName(), train, tree);
        double acc = printAcc("test " + calc.getName(), test, tree);
        accSum += acc;
        count++;
        System.out.println("acc avg :" + accSum / count + "   ===============================");
        filter(calc, test, acc, tree);
//        writeResult(test, tree,new File("D:\\tmp\\calculate\\output.txt"));


//        System.out.println("error2:" + new DecimalFormat("###.######").format(error / test.matrix.length));

    }

    private void writeResult(Data test, Classifier tree, File output) {
        double error = 0;
        StringBuffer header = new StringBuffer();
        header.append("日期" + "\t");
        header.append("开盘价" + "\t");
        header.append("最高价" + "\t");
        header.append("收盘价" + "\t");
        header.append("最低价" + "\t");
        header.append("交易金额" + "\t");
        header.append("交易量" + "\t");
        header.append("均价变动" + "\t");
        header.append("最高变动" + "\t");
        header.append("最低变动" + "\t");
        header.append("高低差变动" + "\t");
        header.append("量比" + "\t");
        header.append("收盘5日涨幅" + "\t");
        header.append("收盘10日涨幅" + "\t");
        header.append("5日涨" + "\t");
        header.append("10日涨" + "\t");
        header.append("30日涨" + "\t");
        header.append("月均价" + "\t");
        header.append("月均价一日变动" + "\t");
        header.append("月均价未来10日变动" + "\t");
        header.append("月均量" + "\t");
        header.append("月均量一日变动" + "\t");
        header.append("月均量未来10日变动" + "\t");
        header.append("均量量比" + "\t");
        header.append("结果\n");
        error = 0;
//        File output = new File("D:\\tmp\\calculate\\output.txt");
        output.delete();
        FileUtil.write(output, header.toString(), true);
        for (int i = 0; i < test.matrix.length; i++) {
//            System.out.println(tree.predict(train.matrix[i])+""+train.label[i]);
            int result = tree.predict(test.matrix[i]);
            if (result != test.label[i]) {
                error++;
            }
            String line = test.rows[i] + "\t" + result + "\n";
            FileUtil.write(output, line, true);
//            System.out.println();
        }
    }

    private Data loadData(String file) {
        Data data = new Data();
        String strData = FileUtil.read(new File(file));
        String[] rows = strData.split("\n");

        List<double[]> dataList = new ArrayList<double[]>();
        List<Integer> typeList = new ArrayList<Integer>();
        for (int x = 0; x < rows.length; x++) {
            String[] colArr = rows[x].split("\\s");
            int type = Integer.valueOf(colArr[25]);
            if (type == -1) {
                continue;
            }
            dataList.add(CodeUtil.loadDay(colArr));
            typeList.add(type);

        }
        data.matrix = new double[dataList.size()][];
        data.label = new int[dataList.size()];
        for (int x = 0; x < dataList.size(); x++) {
            data.matrix[x] = dataList.get(x);
            data.label[x] = typeList.get(x);//收盘价未来变动结果
//            data.label[x] = Integer.valueOf(colArr[22]);//交易量未来变动结果
        }
        data.rows = rows;
        return data;
    }


    class Data {
        String[] rows;
        double[][] matrix;
        int[] label;
        NumericAttribute[] attributes;
    }

    public DataDay[] calculateFile(File file, File result, String year) throws LackDataException {
        String[] txt = FileUtil.read(file).split("\n");
//        String[] txt = FileUtil.read(new File("D:\\tmp\\code_data\\600000.txt")).split("\n");
        DataDay[] days = new DataDay[txt.length];
        for (int i = 0; i < txt.length; i++) {
            days[i] = new DataDay(txt[i]);
//            if (i > 0 && days[i].start < days[i - 1].start / 0.6) {
//                throw new LackDataException("派股未复权");
//            }
        }
        if (days.length < checkData)
            throw new LackDataException("数据不全");
        Avg(days, 10);
        Rise(days, 5);
        Macd(days, 12, 26, 10);
        for (int i = 10; i < days.length; i++) {
            if (StringUtils.isNotEmpty(year) && !days[i].date.startsWith(year))
                continue;
            calculateOne(i, days);
//            if (codes[i].done)
//                    FileUtil.write(new File("D:\\tmp\\calculate\\600093.txt"), codes[i].calculateStr() + "\n", true);
            FileUtil.write(result, days[i].calculateStr() + "\n", true);
        }
        return days;
    }

    /**
     * int emaDays1 = 12;
     * int emaDays2 = 26;
     * int deaDays = 9;
     */
    private void Macd(DataDay[] days, int emaDays1, int emaDays2, int deaDays) {
        for (int i = 1; i < days.length; i++) {
            MACD macd = new MACD();
            MACD lastMacd = days[i - 1].macd;
            macd.emaDays1 = emaDays1;
            macd.emaDays2 = emaDays2;
            macd.deaDays = deaDays;
            macd.emaFast = lastMacd.emaFast * (emaDays1 - 1) / (emaDays1 + 1) + days[i].end * 2 / (emaDays1 + 1);
            macd.emaSlow = lastMacd.emaSlow * (emaDays2 - 1) / (emaDays2 + 1) + days[i].end * 2 / (emaDays2 + 1);
            macd.dif = macd.emaFast - macd.emaSlow;
            macd.dea = lastMacd.dea * (deaDays - 1) / (deaDays + 1) + macd.dif * 2 / (deaDays + 1);
            macd.macd = (macd.dif - macd.dea) * 2;
            days[i].macd = macd;
        }
    }

    @Test
    public void calculateCodeTest() throws Exception {
        File file = new File("D:\\tmp\\calculate\\600708_2015.txt");
        calculateFile(file, new File("D:\\tmp\\calculate\\600708.txt"), null);
//        calculateFile(file, new File("D:\\tmp\\calculate\\600479.txt"), "2007");

    }

    @Test
    public void calculateAll() throws Exception {
        File root = new File("D:\\tmp\\code_data");


        int cnt = 0;
        int s = 0;
        int size = 100;
        File result = new File("D:\\tmp\\calculate\\" + s + "_" + size + ".txt");
        result.delete();
        Set<File> simple = new HashSet<File>();
        File[] files = root.listFiles();
        for (File f : files) {
            String[] arr = FileUtil.read(f).split("\n");
            if (arr.length < 900) {
                f.delete();
            }
        }
        files = root.listFiles();
        for (; ; ) {
            if (simple.size() >= size)
                break;
            int i = new Random().nextInt(files.length);
            simple.add(files[i]);
        }

        for (File f : simple) {
            System.out.println(cnt++ + "\t" + f.getName());
//            if (!f.getName().equals("600093.txt")) {
//                continue;
//            }
            if (cnt < s)
                continue;
            if (cnt > s + size)
                break;
            //FileUtil.write(new File("D:\\tmp\\calculate\\" + s + "_" + size + ".txt")
            calculateFile(f, result, null);
        }
    }


    private void Avg(DataDay[] codes, int rage) {
        for (int i = rage; i < codes.length; i++) {
            DataDay code = codes[i];
            double priceMonth = 0;
            double quanMonth = 0;
            for (int x = i - rage; x < i; x++) {
                priceMonth += codes[x].priceAvg;
                quanMonth += codes[x].quantity;
            }
            code.c17 = priceMonth / rage;
            code.c20 = quanMonth / rage;
        }
    }

    private void Rise(DataDay[] codes, int days) {
        for (int x = days; x < codes.length - days; x++) {
            DataDay code = codes[x];
            double d = (codes[x].end - codes[x - days].end) / codes[x - days].end * 100;
            code.c12 = d;
            if (x == days) {
                code.c24 = d;
            } else {
                code.c24 = codes[x - 1].c24 + d;
            }
        }

    }

    /**
     * 使用20日线
     * 加入交易量参数
     *
     * @param i
     * @param codes
     */
    private void calculateOne(int i, DataDay[] codes) {

        try {
            DataDay code = codes[i];
            //均价变动
            code.c7 = (codes[i].priceAvg - codes[i - 1].priceAvg) / codes[i - 1].priceAvg * 100;
            //最高变动
            code.c8 = (codes[i].highest - codes[i - 1].highest) / codes[i - 1].highest * 100;
            //最低变动
            code.c9 = (codes[i].lowest - codes[i - 1].lowest) / codes[i - 1].lowest * 100;
            //高低差变动
            if (codes[i - 1].diff == 0) {
                code.c10 = 100;
            } else {
                code.c10 = (codes[i].diff - codes[i - 1].diff) / codes[i - 1].diff * 100;
            }
            //量比
            double quantitySum = 0;
            for (int x = i - 5; x < i; x++) {
                quantitySum += codes[x].quantity;
            }
            code.c11 = code.quantity / (quantitySum / 5);
            //收盘5日涨幅
//            code.c12 = (codes[i].end - codes[i - 1].end) / codes[i - 1].end * 100;
            //收盘10日涨幅
            code.c13 = (codes[i].end - codes[i - 10].end) / codes[i - 10].end * 100;
            code.c18 = code.c17 > codes[i - 1].c17 ? 1 : 0;
            code.c21 = code.c20 > codes[i - 1].c20 ? 1 : 0;
            if (i + 10 > codes.length - 1) {
                code.c22 = code.c24 = -1;
            } else {
//                =SUM($N$1:N3)/50
//                for (int x = 0; x <= i; x++) {
//                    code.c19 += codes[i].c12;
//                }
                //1111111111111111111
                double d = codes[i + 5].c17 / code.c17;
                code.c25 = d > 1.05 ? 1 : 0;
                //2222222222222222222
//                code.c25 = 0;
//                for (int x = i + 1; x < i + 10; x++) {
//                    if (codes[x].end / code.end > 1.05) {
//                        code.c25 = 1;
//                        break;
//                    }
//                }
//                code.c22 = code.c20 < codes[i + 5].c20 ? 1 : 0;
            }
            //均量量比
            quantitySum = 0.00;
            int flag = 10;
            for (int x = i - flag; x < i; x++) {
                quantitySum += codes[x].c20;
            }

            code.c23 = code.c20 / (quantitySum == 0 ? code.c20 : quantitySum / flag);
            code.done = true;
//            System.out.println(i);
        } catch (Throwable e) {
            logger.error("calculate", e);

        }


    }


    //    000063
    @Test
    public void downloadData1() throws Exception {
        new HistoryDownloader().downloadData("", 2015, new File("D:\\tmp\\calculate\\" + "" + "_" + 1 + ".txt"));
    }

    @Test
    public void downloadData() throws Exception {
//        Data train = onePage();
        //循环股票代码
        String[] txt = FileUtil.read(new File("D:\\tmp\\codes.txt")).split("\n");
        go1:
        for (int x = 0; x < txt.length; x++) {
            List<DataDay> codes = new ArrayList<DataDay>();
            String code = txt[x];
            for (int y = 2006; y <= 2009; y++) {  //循环年
                for (int z = 1; z <= 4; z++) {                //循环季度
                    String url = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/" + code + ".phtml?year=" + y + "&jidu=" + z;
                    try {
                        codes.addAll(HistoryDownloader.onePage(url));
                    } catch (Throwable e) {
                        continue go1;
                    }

                }
            }
            Collections.sort(codes, new Comparator<DataDay>() {
                @Override
                public int compare(DataDay o1, DataDay o2) {
                    return o1.date.compareTo(o2.date);
                }
            });
            for (DataDay c : codes) {
                FileUtil.write(new File("D:\\tmp\\code_data\\" + code + ".txt"), c.toString() + "\n", true);
            }
        }

    }


}
