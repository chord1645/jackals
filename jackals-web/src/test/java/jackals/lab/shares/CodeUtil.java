package jackals.lab.shares;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.math.*;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

//@Ignore
public class CodeUtil {
    private Logger logger = LoggerFactory.getLogger(getClass());


    static List<String> blackList = ImmutableList.of("600022", "600019", "600018", "000629", "000898", "150018", "600005", "601991", "600795", "600726");

    public  static void main(String args[]){
        double[] arr1 = new double[]{1,2,3};
        double[] arr2 = new double[]{-3,-2,-1};
        System.out.println(similar(arr1,arr2));
    }
    public static String origFileName(String s) {
        return "D:\\tmp\\runCode\\orig\\" + s + ".txt";
    }

    public static String calcFileName(String s) {
        return "D:\\tmp\\runCode\\calc\\calc-" + s + ".txt";
    }

    public static double dist(double x1, double y1, double x2, double y2) {
        //d = sqrt((x1-x2)^2+(y1-y2)^2)
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    public static double similar(double[] arr1, double[] arr2) {
        double avg1 = avg(arr1);
        double avg2 = avg(arr2);
        double sum1 = 0;
        double sum2 = 0;
        double cov = smile.math.Math.cov(arr1, arr2);
        double sd1 = smile.math.Math.sd(arr1);
        double sd2 = smile.math.Math.sd(arr2);
        return cov / (sd1 * sd2);
    }

    private static double avg(double[] arr1) {
        return 0;
    }

    public static double ratio(double x1, double y1, double x2, double y2) {
        return (y2 - y1) / (x2 - x1);
    }

    public static double[] loadDay(String[] colArr) {

//            System.out.println(colArr.length);
//            for (int y = 0; y < 7; y++) {
//                data.matrix[x][y] = Double.valueOf(colArr[y + 7]);
//            }
        ArrayList<Double> list = Lists.newArrayList(
                Double.valueOf(colArr[1]),
                Double.valueOf(colArr[2]),
                Double.valueOf(colArr[3]),
                Double.valueOf(colArr[4]),
                Double.valueOf(colArr[7]),
                Double.valueOf(colArr[8]),
                Double.valueOf(colArr[9]),
                Double.valueOf(colArr[10]),
                //                    Double.valueOf(colArr[11]),
                Double.valueOf(colArr[12]),
                Double.valueOf(colArr[13]),
//                Double.valueOf(colArr[17]),
//                Double.valueOf(colArr[18]),
                //                     Double.valueOf(colArr[20]),
//                Double.valueOf(colArr[21]),
//                Double.valueOf(colArr[23]),
                Double.valueOf(colArr[24])
        );

        double[] day = new double[list.size()];

        for (int i = 0; i < list.size(); i++) {
            day[i] = list.get(i);
        }
        return day;
    }
}
