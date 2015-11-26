package jackals.lab.shares;

import java.text.DecimalFormat;

//EMA12	EMA26	DIF	DEA	MACD

public class MACD {
    int emaDays1 = 12;
    int emaDays2 = 26;
    int deaDays = 9;
    double emaFast;
    double emaSlow;
    double dif;
    double dea;
    double macd;

    public int getDeaDays() {
        return deaDays;
    }

    public void setDeaDays(int deaDays) {
        this.deaDays = deaDays;
    }

    public int getEmaDays1() {
        return emaDays1;
    }

    public void setEmaDays1(int emaDays1) {
        this.emaDays1 = emaDays1;
    }

    public int getEmaDays2() {
        return emaDays2;
    }

    public void setEmaDays2(int emaDays2) {
        this.emaDays2 = emaDays2;
    }

    public double getEmaFast() {
        return emaFast;
    }

    public void setEmaFast(double emaFast) {
        this.emaFast = emaFast;
    }

    public double getEmaSlow() {
        return emaSlow;
    }

    public void setEmaSlow(double emaSlow) {
        this.emaSlow = emaSlow;
    }

    public double getDif() {
        return dif;
    }

    public void setDif(double dif) {
        this.dif = dif;
    }

    public double getDea() {
        return dea;
    }

    public void setDea(double dea) {
        this.dea = dea;
    }

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }
}