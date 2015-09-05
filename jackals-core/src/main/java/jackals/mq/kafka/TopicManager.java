package jackals.mq.kafka;

import jackals.utils.SpringContextHolder;
import kafka.admin.TopicCommand;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import scala.collection.Map;

import java.util.Properties;


public class TopicManager {
    private static Logger logger = LoggerFactory.getLogger(TopicManager.class);

    public static void main(String[] args) {
//        deleteTopic("listener_job_all.163.com");
        allTopics();

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
//        String[] options = new String[]{
//                "--list",
//                "--zookeeper",
//                mqConfig.getProperty("mq.kafka.zookeeper")
//        };
//        TopicCommand.main(options);
        ZkConnection zkConnection = new ZkConnection(mqConfig.getProperty("mq.kafka.zookeeper"));
        ZkClient zkClient = new ZkClient(zkConnection);
        Map<String, Properties> map = kafka.admin.AdminUtils.fetchAllTopicConfigs(zkClient);
        scala.collection.Iterator<Tuple2<String, Properties>> it = map.iterator();
        for (; it.hasNext(); ) {
            Tuple2<String, Properties> t = it.next();
            System.out.println(t._1);
            System.out.println(t._2);
        }

    }


}