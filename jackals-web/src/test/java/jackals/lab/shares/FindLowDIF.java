package jackals.lab.shares;

import com.google.common.collect.Lists;
import jackals.downloader.HttpDownloader;
import jackals.downloader.ReqCfg;
import jackals.lab.FileUtil;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.classification.Classifier;
import smile.classification.DecisionTree;
import smile.classification.RBFNetwork;
import smile.classification.SVM;
import smile.data.NumericAttribute;
import smile.math.distance.EuclideanDistance;
import smile.math.kernel.GaussianKernel;
import smile.math.rbf.RadialBasisFunction;
import smile.util.SmileUtils;

import java.io.File;
import java.util.*;

//@Ignore
public class FindLowDIF {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void ratio() throws Exception {
        Shares shares = new Shares();
        String[] txt = FileUtil.read(new File("D:\\tmp\\codes.txt")).split("\n");
        List<DataDay> list = new ArrayList<DataDay>();
        for (String code : txt) {
            File orig = new File(CodeUtil.origFileName(code));
            File calc = new File(CodeUtil.calcFileName(code));
            DataDay[] tmp = new DataDay[10];
            if (!orig.exists())
                continue;
            try {
                if (!calc.exists()) {
                    DataDay[] dataDays = shares.calculateFile(orig, calc, "2015");
//                    dataDay = dataDays[dataDays.length - 1];
                    tmp = Arrays.copyOfRange(dataDays, dataDays.length - 10, dataDays.length - 1);
                } else {
                    String[] dataDayStr = FileUtil.read(calc).split("\n");
                    for (int x = dataDayStr.length - 10, y = 0; x < dataDayStr.length; x++, y++) {
                        tmp[y] = new DataDay(dataDayStr[x]);
                    }

                }
                double ratio = 0;
                for (int m = 0; m < tmp.length-1; m++) {
                    double y = tmp[tmp.length - 1].macd.dif - tmp[m].macd.dif;
                    double x = tmp.length - m;
                    if (Math.abs(y/x)>ratio)
                        ratio = y/x;
                }
//                System.out.println(ratio);
                DataDay dataDay = tmp[tmp.length - 1];
                dataDay.code = code;
                dataDay.ratio = ratio;
                if (dataDay.macd.dif < 0 && dataDay.end > 10)
//                if (dataDay.end > 10)
                    list.add(dataDay);
//                if (dataDay.macd.macd < 0
//                        && dataDay.macd.dif < 0
//                        && dataDay.macd.dea < 0
//                        && dataDay.macd.dif -dataDay.macd.dea<0.3
//                        ) {
//                    System.out.println(code);
//                }

            } catch (LackDataException e) {
                continue;
            }

        }
        Collections.sort(list, new Comparator<DataDay>() {
            @Override
            public int compare(DataDay o1, DataDay o2) {
//                if (o1.ratio > o2.ratio) {
//                    return -1;
//                } else if (o1.ratio <o2.ratio) {
//                    return 1;
//                } else
//                    return 0;
                if (Math.abs(o1.macd.dif) > Math.abs(o2.macd.dif)) {
                    return 1;
                } else if (Math.abs(o1.macd.dif) < Math.abs(o2.macd.dif)) {
                    return -1;
                } else
                    return 0;
            }
        });
        for (DataDay d : list) {
            System.out.println(d.code+"\t"+d.macd.dif + "\t " + d.macd.macd);
        }
    }

    @Test
    public void run2() throws Exception {
        for (File file : new File("D:\\tmp\\runCode\\calc").listFiles()) {
            String[] txt = FileUtil.read(file).split("\n");
            DataDay dataDay = new DataDay(txt[txt.length - 1]);
            if (dataDay.macd.macd < 0 && dataDay.macd.dif < 0) {
                System.out.println(file.getName());
            }
        }

    }
}
