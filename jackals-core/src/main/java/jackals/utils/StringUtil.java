package jackals.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class StringUtil {

    public static void main(String[] args) {
        String s = regxGet("<a\\s+href\\s*=\\s*\"{0,1}(.*sports.sina.com.cn.*)\"{0,1}\\s*>", 1, "<a href=\"http://sports.sina.com.cn/others/ticao/news.shtml\">");
        System.out.println(s);
        List<String> list = regxGetList(
                "<a\\s+href\\s*=\\s*\"{0,1}([^<>\"]*sports.sina.com.cn[^<>\"]*)\"{0,1}\\s*>",
                1,
                "<a href=\"http://sports.sina.com.cn/others/ticao/news.shtml\"><a href=\"http://sports.sina.com.cn/others/ticao/news.shtml\">");
        System.out.println(list);
    }

    public static String regxGet(String regx, int group, String str) {
        Matcher matcher = Pattern.compile(regx).matcher(str);
        if (matcher.find()) {
            return matcher.group(group);
        } else {
            return null;
        }
    }

    public static List<String> regxGetList(String regx, int group, String str) {
//        System.out.println(str);
        List<String> out = new ArrayList<String>();
        Matcher matcher = Pattern.compile(regx, Pattern.DOTALL).matcher(str);
        while (matcher.find()) {
            out.add(matcher.group(group));
//            for (int i = 0; i <= matcher.groupCount(); i++) {
//                out.add(matcher.group(i));
//            }
        }
        return out;
    }

}