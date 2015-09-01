package jackals.utils;

public class MQTopicUtils {
    public static String queueId(Integer spiderNum) {
        return "spider_"+spiderNum;
    }

}
