package jackals.allocation;

import jackals.Constants;
import jackals.filter.URLRedisFilter;
import jackals.job.pojo.JobInfo;
import jackals.model.RequestOjb;
import jackals.mq.activemq.ActiveMQSender;
import jackals.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * url分发接口
 */
public class AmqAllocation implements Allocation {
    private Logger logger = LoggerFactory.getLogger(getClass());

    ActiveMQSender sender;
    StringRedisTemplate redisTemplate;
    URLRedisFilter filter = new URLRedisFilter();
    public AmqAllocation() {
        Properties mqConfig = SpringContextHolder.getBean("mqConfig");
        redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
        sender = new ActiveMQSender(mqConfig.getProperty("mq.amq.broker"));
    }

    @Override
    public void allocate(JobInfo jobInfo, List<RequestOjb> reqlist) {
        String topic = Constants.TopicJobPrefix + jobInfo.getId();
        if (CollectionUtils.isEmpty(reqlist))
            return;
        for (Iterator<RequestOjb> it = reqlist.iterator(); it.hasNext(); ) {
            RequestOjb requestOjb = it.next();
            if (filter.exist(jobInfo, requestOjb.getUrl())) {
                it.remove();
            }
        }
        sender.sendBatchRequest(topic, reqlist);
        logger.info("allocate [{}] :{}", jobInfo.getId(), reqlist.size());
    }
}
