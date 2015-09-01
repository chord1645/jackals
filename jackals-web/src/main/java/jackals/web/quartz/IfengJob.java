package jackals.web.quartz;

import com.google.common.collect.ImmutableList;
import jackals.job.JobManager;
import jackals.job.pojo.JobInfo;
import jackals.mq.kafka.KafkaSender;
import jackals.simples.IfengNews;
import jackals.solr.SolrDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class IfengJob {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    JobManager jobManager = JobManager.create(new KafkaSender());
    @Autowired
    SolrDaoImpl indexDao;

    public void execute() {
        JobInfo jobInfo = IfengNews.job();
        log.info("execute: {}", jobInfo);
        jobManager.update(jobInfo,
                ImmutableList.of("10", "20", "30")
        );
    }


}
