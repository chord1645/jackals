package jackals.lab.test;

import com.wisers.crawler.BaseTest;
import jackals.lab.Cluster;
import jackals.web.quartz.FpGrowthJob;
import jackals.web.quartz.News163Job;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@Ignore
public class ClusterTest extends BaseTest {
    @Autowired
    Cluster cluster;
    @Test
    public void fpGrowthJob() throws Exception {
        cluster.buildData();
        cluster.transformToSequenceFile();

    }
}
