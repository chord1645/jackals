package jackals.page;

import jackals.job.pojo.JobInfo;
import jackals.model.RequestOjb;

import java.util.ArrayList;

/**
 */
public interface PageProcess {

    ArrayList<RequestOjb> process(RequestOjb link,JobInfo job) throws Exception;
}
