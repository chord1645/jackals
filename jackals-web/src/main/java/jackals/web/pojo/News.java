package jackals.web.pojo;

import jackals.utils.StringUtil;
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
    String img;
    @Field("infoTime_dt")
    Date time;
    @Field("group_s")
    String group;
    @Field("sim_i")
    Integer sim = 0;
    @Field("useful_i")
    Integer useful;

    public String getImg() {
        String src = StringUtil.regxGet("src=\"(.*?\\.jpg)\"",1,content);
        return src;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getGroup() {
        return group;
    }

    public Integer getSim() {
        return sim;
    }

    public void setSim(Integer sim) {
        this.sim = sim;
    }

    public Integer getUseful() {
        return useful;
    }

    public void setUseful(Integer useful) {
        this.useful = useful;
    }

    public void setGroup(String group) {
        this.group = group;
    }

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
            String str = content.replaceAll("(?is)<[^>]*?>", "").replaceAll("\n|　", "").trim();
            return str.substring(0, str.length() > 30 ? 30 : str.length()) + "...";
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
