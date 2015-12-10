package jackals.lab.shares;

import jackals.lab.FileUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 股价上升与下降比例曲线
 */
public class Calculate {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void run() throws Exception {
        Shares shares = new Shares();
        String[] txt = FileUtil.read(new File("D:\\tmp\\codes.txt")).split("\n");
        for (String code : txt) {
            if (CodeUtil.blackList.contains(code)){
                new File(CodeUtil.calcFileName(code)).delete();
                continue;
            }
            File orig = new File(CodeUtil.origFileName(code));
            File calc = new File(CodeUtil.calcFileName(code));
            if (!orig.exists()||calc.exists())
                continue;
            try {
                System.out.println(code);
                shares.calculateFile(orig, calc, "2015");
            } catch (LackDataException e) {
                continue;
            }

        }
    }


}
