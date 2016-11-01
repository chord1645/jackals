package jackals.model;

import com.alibaba.fastjson.JSON;

/**
 * Created by scott on 2015/8/11.
 */
public class RequestOjb {
    String url;
    Integer depth;
    boolean isSeed = false;
    boolean retry = false;

    public RequestOjb() {
    }

    public RequestOjb(String s, boolean b) {
        this(s);
        isSeed = b;
    }

    public boolean isSeed() {
        return isSeed;
    }

    public void setSeed(boolean isSeed) {
        this.isSeed = isSeed;
    }

    public RequestOjb(String url) {
        this.url = url;
        this.depth = 0;
    }

    public RequestOjb(String url, int depth) {
        this.url = url;
        this.depth = depth;
    }

    public boolean isRetry() {
        return retry;
    }

    public void setRetry(boolean retry) {
        this.retry = retry;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Override
    public boolean equals(Object obj) {
        RequestOjb tmp = (RequestOjb) obj;
        return this.getUrl().equals(tmp.getUrl());
    }
}
