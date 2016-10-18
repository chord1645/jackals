package jackals.single;

import com.alibaba.fastjson.JSON;
import jackals.Constants;
import jackals.allocation.Allocation;
import jackals.job.pojo.JobInfo;
import jackals.model.RequestOjb;
import jackals.mq.kafka.KafkaSender;
import kafka.producer.KeyedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 */
public class SingleAllocationImpl implements Allocation {
    private Logger logger = LoggerFactory.getLogger(getClass());

    //    SpiderJob spider;
    protected KafkaSender kafkaSender;

    public SingleAllocationImpl() {
        kafkaSender = new KafkaSender();
    }

    //    public KafkaAllocationImpl(SpiderJob spider) {
//        this.spider = spider;
//    }
    int i = 0;

    public void allocate(JobInfo jobInfo, List<RequestOjb> reqlist) {
        String topic = Constants.TopicJobPrefix + jobInfo.getId();
//        kafkaSender.sendBatchRequest(Constants.TopicJobPrefix +jobId, list);
//        logger.info("sendBatchRequest topic={} ,msg={}", topic, list);
        if (CollectionUtils.isEmpty(reqlist))
            return;
        List<KeyedMessage> list = new ArrayList<KeyedMessage>();

        Set<String> set = new HashSet<String>();
        for (RequestOjb r : reqlist) {
            if (set.contains(r.getUrl()))
                continue;
            set.add(r.getUrl());
            i++;
//            KeyedMessage msg = new KeyedMessage<String, String>(topic, r.getUrl(), JSON.toJSONString(r));
            KeyedMessage msg = new KeyedMessage<String, String>(topic, i + "", JSON.toJSONString(r));
            list.add(msg);
        }
        logger.info("allocate [{}] :{}", jobInfo.getId(), reqlist.size());
        kafkaSender.sendBatch(topic, list);
    }
}
