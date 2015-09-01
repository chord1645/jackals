package jackals.job.pojo;

import jackals.Constants;

public class ExtratField {
    String name;
    String regx;
    int group;
    String value;
    int fmtType;
    String fmtStr;
    public ExtratField() {

    }

    public ExtratField(String name, String regx, int group,int fmtType) {
        this.name = name;
        this.regx = regx;
        this.group = group;
        this.fmtType = fmtType;
    }

    public String getFmtStr() {
        return fmtStr;
    }

    public void setFmtStr(String fmtStr) {
        this.fmtStr = fmtStr;
    }

    public int getFmtType() {
        return fmtType;
    }

    public void setFmtType(int fmtType) {
        this.fmtType = fmtType;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegx() {
        return regx;
    }

    public void setRegx(String regx) {
        this.regx = regx;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}