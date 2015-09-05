package jackals;

/**
 * Created by scott on 2015/8/6.
 */
public class Constants {

    public static String TopicSpiderPrefix = "listener_spider_";
    public static String TopicJobPrefix = "listener_job_";

    static public class Kafka {
        public final static String defaultGroup = "defaultGroup";
        public final static String DefaultKey = "key";
    }

    static public class FmtType {
        public final static int str = 1;
        public final static int date = 2;
    }

    static public class JobModel { //监听job的模式
        public final static int startJob = 1;
        public final static int addSpider = 2;
        public final static int update = 3;
        public final static int stopJob = 4;

    }
}
