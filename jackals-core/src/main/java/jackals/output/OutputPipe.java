package jackals.output;

import jackals.job.pojo.JobInfo;
import jackals.model.PageObj;

import java.io.IOException;

/**
 */
public interface OutputPipe {
    void save(JobInfo spiderJob, PageObj page, Object e) throws IOException, Exception;

    void error(JobInfo job, PageObj page);
}
