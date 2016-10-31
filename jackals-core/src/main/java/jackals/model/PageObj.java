package jackals.model;

/**
 * Created by scott on 2015/8/11.
 */
public class PageObj {
    String rawText;
    int statusCode;
    RequestOjb request;
    boolean success = false;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public RequestOjb getRequest() {
        return request;
    }

    public void setRequest(RequestOjb request) {
        this.request = request;
    }
}
