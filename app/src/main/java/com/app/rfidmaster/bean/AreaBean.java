package com.app.rfidmaster.bean;

import java.util.List;

/**
 * @author: cbb
 * @date: 2020/12/23 20:45
 * @description:
 */
public class AreaBean {
    public String id;
    public String cusName;
    public String shortName;
    public List<Register> listRegister;

    public static class Register {
        public String id;
        public String regionName;
        public String cusID;
    }
}
