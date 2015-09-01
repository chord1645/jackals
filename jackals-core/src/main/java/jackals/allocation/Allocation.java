package jackals.allocation;

import jackals.job.pojo.JobInfo;
import jackals.model.RequestOjb;

import java.util.List;

/**
 * url分发接口
 */
public interface Allocation {
    void allocate(JobInfo jobId,List<RequestOjb> list);
}
