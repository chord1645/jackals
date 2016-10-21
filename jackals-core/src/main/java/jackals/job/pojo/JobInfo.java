package jackals.job.pojo;

import jackals.model.PageObj;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class JobInfo {
    //    AfterExtract afterExtract;
    Integer maxDepth = 0;
    Integer jobThreadNum = 5;
    long sleep = 5;
    String id;
    Integer jobModel;
    Boolean reset = false;
    Orders orders = new Orders();
    List<String> seed = new ArrayList<String>();

    public boolean useful(PageObj pageObj) {
        return true;
    }

    public Integer getJobModel() {
        return jobModel;
    }

    public void setJobModel(Integer jobModel) {
        this.jobModel = jobModel;
    }

    public JobInfo() {
    }

    public long getSleep() {
        return sleep;
    }

    public void setSleep(long sleep) {
        this.sleep = sleep;
    }

    public JobInfo(String id) {
        this.id = id;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public JobInfo setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
        return null;
    }

    public List<String> getSeed() {
        return seed;
    }

    public void setSeed(List<String> seed) {
        this.seed = seed;
    }

    public Integer getJobThreadNum() {
        return jobThreadNum;
    }

    public void setJobThreadNum(Integer jobThreadNum) {
        this.jobThreadNum = jobThreadNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getReset() {
        return reset;
    }

    public void setReset(Boolean reset) {
        this.reset = reset;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public static JobInfo create(String s) {
        return new JobInfo(s);
    }
}
