package jackals.lab;

import cn.nhorizon.commons.classfier.constant.Constants;
import cn.nhorizon.commons.classfier.service.VSMService;
import cn.nhorizon.commons.classfier.utils.CHNWVTTokenizer;
import edu.udo.cs.wvtool.main.WVTWordVector;

import java.io.*;
import java.util.HashMap;

public class FileUtil {
    public static void main(String[] args) {

    }

    static String encoding = "utf-8";

    public static String read(File file) {
        StringBuffer out = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), encoding));
            String s = null;
            for (; (s = br.readLine()) != null; ) {
                out.append(s).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out.toString();
    }

    public static void write(File file, String text, boolean append) {
        file.getParentFile().mkdirs();
        try {
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file, append)));
            bw.write(text);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void clean(File output) {
        if (output.isDirectory()) {
            for (File f : output.listFiles()) {
                clean(f);
                f.delete();
            }
        } else {
            output.delete();
        }
    }

    public static String readLastLine(File file) {
        BufferedReader br = null;
        String out = null;
        try {
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), encoding));


            for (String s = null; (s = br.readLine()) != null; ) {
                out =s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return out;
    }
}
