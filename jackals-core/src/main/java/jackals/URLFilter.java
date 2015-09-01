package jackals;

import jackals.job.pojo.JobInfo;

/**
 * url分发接口
 */
public interface URLFilter {
    boolean exist(JobInfo jobInfo, String url);
    public void clean(JobInfo key);

    void add(JobInfo jobInfo, String url);
}
