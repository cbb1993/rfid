package com.app.rfidmaster.util;

import android.content.Context;
import android.widget.Toast;

import com.app.rfidmaster.App;

/**
 * @author: cbb
 * @date: 2020/12/14 20:34
 * @description:
 */
public class ToastUtil {
    public static void show(String msg){
        Toast.makeText(App.getContext(),msg,Toast.LENGTH_SHORT).show();
    }
    public static void showLong(String msg){
        Toast.makeText(App.getContext(),msg,Toast.LENGTH_LONG).show();
    }
}
