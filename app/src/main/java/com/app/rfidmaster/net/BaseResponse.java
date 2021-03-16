package com.app.rfidmaster.net;

/**
 * Created by 坎坎.
 * Date: 2020/9/15
 * Time: 17:21
 * describe:
 */
public class BaseResponse<T> {
    public T data;
    public int code;
    public String message;
}
