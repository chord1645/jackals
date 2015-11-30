package jackals.lab.shares;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

//@Ignore
public class CodeUtil {
    private Logger logger = LoggerFactory.getLogger(getClass());
    static List<String> blackList = ImmutableList.of("000629","000898","150018","600005","601991","600795","600726");

    public static String origFileName(String s) {
        return "D:\\tmp\\runCode\\orig\\" + s + ".txt";
    }

    public static String calcFileName(String s) {
        return "D:\\tmp\\runCode\\calc\\calc-" + s + ".txt";
    }

}
