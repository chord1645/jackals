package jackals.page;

import com.google.common.base.Joiner;
import org.apache.http.HttpHost;

public class Proxy {
    public HttpHost httpHost;
    public  long cost = 1000 * 60 * 30;
    public int useful = 1;

    public Proxy(HttpHost httpHost) {
        this.httpHost = httpHost;
    }

    @Override
    public String toString() {
        return Joiner.on(" ").join(useful, cost, httpHost.toString());
    }
}