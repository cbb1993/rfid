package com.app.rfidmaster.bean;


/**
 * @author: cbb
 * @date: 2020/12/24 0:12
 * @description:
 */
public class RFIDListBean implements Comparable<RFIDListBean>{
    public RFIDListBean(String textileName, String hospital, int count) {
        this.textileName = textileName;
        this.hospital = hospital;
        this.count = count;
    }

    public String textileName;
    public String hospital;
    public int count;

    @Override
    public int compareTo(RFIDListBean o) {
        int i = this.hospital.compareTo(o.hospital);
        if(i == 0){
            i =  this.textileName.compareTo(o.textileName);
        }
        return i;
    }
}
