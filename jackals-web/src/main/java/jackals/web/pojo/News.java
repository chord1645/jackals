package jackals.web.pojo;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;
import java.util.List;

/**
 * Created by scott on 2015/9/2.
 */
public class News {

    @Field
    String id;
    @Field
    List<String> title;
    @Field("content_css")
    String content;
    @Field("infoTime_dt")
    Date time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public String getContent() {
        if (StringUtils.isEmpty(content)) {
            return "";
        } else {
            String str = content.replaceAll("(?is)<.*?>","").replaceAll("\n|　", "").trim();
            return str.substring(0, str.length() > 50 ? 50 : str.length())+"...";
        }

    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
