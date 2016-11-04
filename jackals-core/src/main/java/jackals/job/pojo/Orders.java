package jackals.job.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class Orders {
    String pathRegx;
    String targetRegx;
    //    Map<String, ExtratField> fields = new HashMap<String, ExtratField>();
    List<ExtratField> fields = new ArrayList<>();

    public String getPathRegx() {
        return pathRegx;
    }

    public void setPathRegx(String pathRegx) {
        this.pathRegx = pathRegx;
    }

    public String getTargetRegx() {
        return targetRegx;
    }

    public void setTargetRegx(String targetRegx) {
        this.targetRegx = targetRegx;
    }

    public List<ExtratField> getFields() {
        return fields;
    }

    public void setFields(List<ExtratField> fields) {
        this.fields = fields;
    }
}
