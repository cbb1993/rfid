package com.app.rfidmaster.net;

import android.os.Environment;
import android.util.Log;

import com.app.rfidmaster.util.FileUtil;
import com.google.gson.JsonParseException;

import java.io.File;
import java.net.ConnectException;

import retrofit2.HttpException;

/**
 * Created by 坎坎.
 * Date: 2020/9/16
 * Time: 9:50
 * describe:
 */
public class Fault {
    private int code;
    private String message;
    private static final int jsonParseError = 1001;
    private static final int connectException = 1002;
    private static final int ssLHandshakeException = 1003;
    private static final int unKnown = 1003;

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public Fault(Throwable t) {
        if(t != null){
            String e = "message : " + t.getMessage();
            FileUtil.appendMethod(e);
        }

        if (t instanceof HttpException) {
            HttpException e = (HttpException) t;
            setMsg(e.code(),"请求异常");
        }else if (t instanceof ServerException) {
            ServerException e = (ServerException) t;
            setMsg(e.code,e.message);
        }else if (t instanceof JsonParseException) {
            JsonParseException e = (JsonParseException) t;
            setMsg(jsonParseError,e.getMessage());
        }else if (t instanceof ConnectException) {
            setMsg(connectException,"连接失败");
        } else if (t instanceof javax.net.ssl.SSLHandshakeException) {
            setMsg(ssLHandshakeException,"证书验证失败");
        } else {
            setMsg(unKnown,"未知失败");
        }
    }

    public Fault(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private void setMsg(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Fault{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    class ServerException extends RuntimeException {
        int code;
        String message;
    }
}
