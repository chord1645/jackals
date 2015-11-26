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
    public void run1() throws Exception {
        Shares shares = new Shares();
        String[] txt = FileUtil.read(new File("D:\\tmp\\codes.txt")).split("\n");
        for (String code : txt) {
            File orig = new File(CodeUtil.origFileName(code));
            File calc = new File(CodeUtil.calcFileName(code));
            if (!orig.exists())
                continue;
            try {
                DataDay[] dataDay = shares.calculateFile(orig, calc, "2015");
                if (dataDay[dataDay.length - 1].macd.dif < -0.5) {
                    System.out.println(code);
                }
            } catch (LackDataException e) {
                continue;
            }

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
