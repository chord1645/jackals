package jackals.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;


/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext.
 */
public class LinkUtil {

    public static void main(String[] args) {
        System.out.println(clean("http://news.mtime.com/movie/2/index.html#nav"));
    }

    public static String clean(String href) {
        return href == null ? "" : href.replaceAll("#.*", "");
    }
}