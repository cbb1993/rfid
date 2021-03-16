package com.app.rfidmaster.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author: cbb
 * @date: 2020/12/26 0:38
 * @description:
 */
public class PrintBean implements Serializable {
    public String cusName;
    public String regionName;
    public String rcvNo;
    public String createTime;
    public String userName;
    public String loginCount;
    public String outCount;
    public List<Goods> list;

    public static class Goods implements Serializable{
        public String name;
        public String hospital;
        public String count;
    }
}
