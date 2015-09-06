package jackals.web.quartz;

import com.google.common.collect.ImmutableList;
import jackals.job.JobManager;
import jackals.job.pojo.JobInfo;
import jackals.mq.kafka.KafkaSender;
import jackals.simples.News163;
import jackals.solr.IndexDao;
import jackals.solr.SolrDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class News163Job {
    public static void main(String[] args) {
        new News163Job().clean();
    }

    private Logger log = LoggerFactory.getLogger(this.getClass());

    JobManager jobManager = JobManager.create(new KafkaSender());
    @Autowired
    SolrDaoImpl indexDao;

    public void execute() {
        JobInfo jobInfo = News163.job();
        clean();
        log.info("execute: {}", jobInfo);
        jobManager.update(jobInfo,
                ImmutableList.of("10", "20", "30")
        );
    }

    public void clean() {
        String query = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -7);
        query += " saveTime_dt:[* TO " + sdf.format(c.getTime()) + "]";
        query += " AND -title:北京 ";
        log.info("clean solr {}",query);
        indexDao.delete(query);

    }

}
