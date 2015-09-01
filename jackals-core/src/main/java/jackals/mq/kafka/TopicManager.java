package jackals.mq.kafka;

import jackals.Constants;
import jackals.mq.activemq.ActiveMQSender;
import jackals.utils.SpringContextHolder;
import kafka.admin.TopicCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Properties;


public class TopicManager {
    private static Logger logger = LoggerFactory.getLogger(TopicManager.class);

    public static void main(String[] args) {
        deleteTopic("listener_job_all.163.com");
//        allTopics();

    }

    public void run() {


        allTopics();
        deleteTopic("test10");
        allTopics();
//        DeleteTopicCommand.main(options);
    }

    public static void deleteTopic(String s) {
        //bin/kafka-topics.sh --zookeeper zk_host:port/chroot --delete --topic my_topic_name
        Properties mqConfig = SpringContextHolder.getBean("mqConfig");
        String[] options = new String[]{
                "--delete",
                "--zookeeper",
                mqConfig.getProperty("mq.kafka.zookeeper"),
                "--topic",
                s,
        };
        TopicCommand.main(options);
        logger.info("!!!deleteTopic: " + s);
    }

    public static void allTopics() {
        Properties mqConfig = SpringContextHolder.getBean("mqConfig");
        String[] options = new String[]{
                "--list",
                "--zookeeper",
                mqConfig.getProperty("mq.kafka.zookeeper")
        };
        TopicCommand.main(options);
    }


}