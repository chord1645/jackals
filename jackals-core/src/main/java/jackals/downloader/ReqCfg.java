package jackals.downloader;

public class ReqCfg {
    int timeOut;
    String userAgent;

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
}