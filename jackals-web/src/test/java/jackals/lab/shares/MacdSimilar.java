package jackals.lab.shares;

import jackals.lab.FileUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.*;

import java.io.File;
import java.util.Arrays;

/**
 * macd 曲线相似度
 */
public class MacdSimilar {
    private Logger logger = LoggerFactory.getLogger(getClass());


    static double[] base;

    static {
        String[] arr = FileUtil.read(new File("D:\\tmp\\standard.txt")).split("\n");
        base = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            base[i] = Double.valueOf(arr[i]);
        }
    }

    @Test
    public void run1() throws Exception {
//        new HistoryDownloader().run();

        new Calculate().run();
        go1:
        for (File calc : new File("D:\\tmp\\runCode\\calc").listFiles()) {
            String[] dataDayStr = FileUtil.read(calc).split("\n");
            compare(calc.getName(), base, dataDayStr, 40);
//            double[] arr = buildArr(dataDayStr, base.length);
//            for (int y = 0; y < arr.length; y++) {
//                if (arr[y] > 0)
//                    continue go1;
//            }
//            System.out.println(calc.getName() + "\t" + CodeUtil.similar(base, arr));
        }
    }

    private double[] buildArr(String[] dataDayStr, int length) {
        double[] arr = new double[length];
        for (int x = dataDayStr.length - length, y = 0; x < dataDayStr.length; x++, y++) {
            DataDay tmp = new DataDay(dataDayStr[x]);
//                if (tmp.macd.dif > 0)
//                    continue go1;
            arr[y] = tmp.macd.dif;
        }
        return arr;
    }

    /**
     * @param name
     * @param base
     * @param dataDayStr
     * @param i
     */
    private void compare(String name, double[] base, String[] dataDayStr, int i) {
        double score = -1;
        int index = -1;
        go1:
        for (int x = i; x < base.length; x++) {
            double[] origArr = Arrays.copyOfRange(base, 0, x);
            double[] arr = buildArr(dataDayStr, origArr.length);
//            if (smile.math.Math.min(arr) > 0)
//                continue go1;
            for (int y = arr.length - 20; y < arr.length - 1; y++) {
                if (arr[y] > 0)
                    continue go1;
            }
            double tmp = CodeUtil.similar(origArr, arr);
            if (tmp > score) {
                index = x;
                score = tmp;
            }

        }
        if (score > 0)
            System.out.println(index + "\t" + name + "\t" + score);
    }
}
