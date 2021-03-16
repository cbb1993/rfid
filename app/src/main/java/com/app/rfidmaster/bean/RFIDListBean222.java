package com.app.rfidmaster.bean;

/**
 * @author: cbb
 * @date: 2020/12/24 0:12
 * @description:
 */
public class RFIDListBean222 implements Comparable<RFIDListBean222>{
    public RFIDListBean222(String textileName, String sizeName, String code,String days) {
        this.textileName = textileName;
        this.sizeName = sizeName;
        this.code = code;
        this.days = days;
    }

    public String textileName;
    public String sizeName;
    public String code;
    public String days;

    @Override
    public int compareTo(RFIDListBean222 o) {
        int oDays = Integer.parseInt(o.days);
        int tDays = Integer.parseInt(days);
        return oDays - tDays;
    }
}
