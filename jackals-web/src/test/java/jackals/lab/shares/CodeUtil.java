package jackals.lab.shares;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//@Ignore
public class CodeUtil {
    private Logger logger = LoggerFactory.getLogger(getClass());
    static List<String> blackList = ImmutableList.of("600022", "600019", "600018", "000629", "000898", "150018", "600005", "601991", "600795", "600726");

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

    public static double ratio(double x1, double y1, double x2, double y2) {
        return (y2 - y1) / (x2 - x1);
    }
}
