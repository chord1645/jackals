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
    double c1;
    double c2;
    double c3;
    double c4;
    double c5;
    double c6;
    double c7;
    double c8;
    double c9;
    boolean done = false;
    double c10;

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
        quantityAvg = quantity / 240;
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
                + "\t" + df.format(c1)
                + "\t" + df.format(c2)
                + "\t" + df.format(c3)
                + "\t" +df.format( c4)
                + "\t" + df.format(c5)
                + "\t" + df.format(c6)
                + "\t" + df.format(c7)
//                + "\t" + (int)(c7/10)
                + "\t" + df.format(c8)
                + "\t" + df.format(c9)
                + "\t" + df.format(c10)
                ;
    }
}