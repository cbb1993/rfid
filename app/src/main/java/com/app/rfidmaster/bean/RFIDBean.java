package com.app.rfidmaster.bean;

import java.util.List;

/**
 * @author: cbb
 * @date: 2020/12/24 0:12
 * @description:
 */
public class RFIDBean {
    public List<RFIDInfo> listRFIDInfo;
    public List<RFIDNotIN> listRFIDNotIN;

    public static class RFIDInfo{
        public String rfidTagID;
        public String rfidTagCode;
        public String textileID;
        public String textileName;
        public String sizeID;
        public String sizeName;
        public String cusID;
        public String cusName;
        public String shortCode;
        public String Days;
    }
    public static class RFIDNotIN{
        String rfidTagCode;
    }
}
