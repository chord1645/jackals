package jackals.downloader;

import jackals.job.pojo.JobInfo;

public class ReqCfg {
    int timeOut;
    String userAgent;
    Valid valid;
    //    HttpHost proxy = new HttpHost("103.240.241.182", 80);

    private ReqCfg() {
        timeOut = 10000;
        userAgent = "";
    }

    public static ReqCfg deft() {
        return new ReqCfg();
    }

    public String getUserAgent() {
        return userAgent;
    }

    public ReqCfg setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public ReqCfg setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    public Valid getValid() {
        return valid;
    }

    public ReqCfg setValid(Valid valid) {
        this.valid = valid;
        return this;
    }
}