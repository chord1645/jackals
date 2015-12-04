package jackals.lab.shares;

import jackals.lab.FileUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * 股价上升与下降比例曲线
 */
public class RiseRate {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void run1() throws Exception {
//        new HistoryDownloader().run();
        new Calculate().run();
        for (int x = 180; x > 0; x--) {
            int zl = 0;
            int jl = 0;
            String date = "";
            for (File calc : new File("D:\\tmp\\runCode\\calc").listFiles()) {
                String[] dataDayStr = FileUtil.read(calc).split("\n");
                if (dataDayStr.length < 180)
                    continue;
               String[] arr =  dataDayStr[dataDayStr.length-x].split("\\s");
                double[] day = CodeUtil.loadDay(arr);
                date=arr[0];
//                DataDay dataDay = new DataDay(dataDayStr[x]);
//                System.out.println(day[8]);

                if (day[8] > 0) {
                    zl++;
                } else {
                    jl++;
                }
            }
            System.out.println(date + "\t" + zl + "\t" + jl);

        }
    }


}
