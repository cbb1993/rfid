package com.app.rfidmaster.util;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static void appendMethod(String content) {
        // 输出到文件
        File file = new File(Environment.getExternalStorageDirectory(),"rfidLogs");
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        int size = 0;
        if(files != null){
            size = files.length;
        }
        size++;
        File log = new File(file,size + ".txt");
        if (!log.exists()) {
            try {
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(log, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
