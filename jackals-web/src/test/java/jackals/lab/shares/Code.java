package jackals.lab.shares;

import java.text.DecimalFormat;

public class Code {
    double start;
    double highest;
    double end;
    double lowest;
    double quantity;
    double money;
    double priceAvg;//当日均价
    double quantityAvg; //当日平均量
    double diff; //高低差
    String date;
    double c7; //7//均价变动
    double c8;//8//最高变动
    double c9;//9//最低变动
    double c10;//10 //高低差变动
    double c11;//11//量比
    double c12;//12//收盘5日涨幅
    double c13;//13//收盘10日涨幅
    double c14;//14//1日涨
    double c15;//15//10日涨 30%
    double c16;//16//30日涨
    double c17;//17 //均价
    double c18;//18//月均价前日变动
    double c19;//19//涨幅变动曲线
    double c20;//20//月均量
    double c21;//21//月均量前日变动
    double c22;//22//月均量未来变动
    double c23;//23//均量量比
    double c24;//24//收盘5日涨幅动能
    double c25;//25//结果:收盘价未来变动

    boolean done = false;
    public Code(String s) {
        String[] arr = s.split("\\s");
        date = arr[0];//            日期
        start = Double.valueOf(arr[1]);//            开盘价
        highest = Double.valueOf(arr[2]);//            最高价
        end = Double.valueOf(arr[3]);//            收盘价
        lowest = Double.valueOf(arr[4]);//            最低价
        quantity = Double.valueOf(arr[5]);//            交易量(股)
        money = Double.valueOf(arr[6]);//            交易金额(元)
        priceAvg = (highest + lowest) / 2;
        quantityAvg = quantity;
        diff = Math.abs(highest - lowest);
    }

    public Code() {

    }

    public double getPriceAvg() {
        return priceAvg;
    }

    public void setPriceAvg(double priceAvg) {
        this.priceAvg = priceAvg;
    }

    public double getQuantityAvg() {
        return quantityAvg;
    }

    public void setQuantityAvg(double quantityAvg) {
        this.quantityAvg = quantityAvg;
    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        this.diff = diff;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getHighest() {
        return highest;
    }

    public void setHighest(double highest) {
        this.highest = highest;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public double getLowest() {
        return lowest;
    }

    public void setLowest(double lowest) {
        this.lowest = lowest;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return date + "\t" + start + "\t" + highest + "\t" + end + "\t" + lowest + "\t" + quantity + "\t" + money;
    }

    public String calculateStr() {
        DecimalFormat df =  new DecimalFormat("########.#####");
        return toString()
                + "\t" + df.format(c7)//7
                + "\t" + df.format(c8)//8
                + "\t" + df.format(c9)//9
                + "\t" +df.format(c10)//10
                + "\t" + df.format(c11)//11
                + "\t" + df.format(c12)//12
                + "\t" + df.format(c13)//13
                + "\t" + df.format(c14)//14
                + "\t" + df.format(c15)//15
                + "\t" + df.format(c16)//16
                + "\t" + df.format(c17)//17
                + "\t" + df.format(c18)//18
                + "\t" + df.format(c19)//19
                + "\t" + df.format(c20)//20
                + "\t" + df.format(c21)//21
                + "\t" + df.format(c22)//22
                + "\t" + df.format(c23)//23
                + "\t" + df.format(c24)//24
                + "\t" + df.format(c25)//25
                ;
    }
}