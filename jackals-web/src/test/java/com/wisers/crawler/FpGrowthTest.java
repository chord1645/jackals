package com.wisers.crawler;

import jackals.web.quartz.FpGrowthJob;
import jackals.web.quartz.News163Job;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

//@Ignore
public class FpGrowthTest extends BaseTest {
    @Autowired
    FpGrowthJob fpGrowthJob;
    @Autowired
    News163Job news163Job;

    @Test
    public void fpGrowthJob() throws Exception {
//        fpGrowthJob.execute();
        fpGrowthJob.fpGrowth();
    }
    @Test
    public void execute() throws Exception {
        fpGrowthJob.execute();
//        fpGrowthJob.fpGrowth();
    }
    @Test
    public void news163Job() throws Exception {
        news163Job.clean();
    }
}
