package com.app.rfidmaster.bean;

/**
 * @author: cbb
 * @date: 2020/12/22 23:54
 * @description:
 */
public class LoginBean {
    public TokenData tokenData;
    public ResultUserInfo resultUserInfo;

    public static class TokenData{
       public String token;
    }
    public static class ResultUserInfo{
        public String id;
        public String loginName;
        public String userName;
        public String mobilePhone;
    }
}
