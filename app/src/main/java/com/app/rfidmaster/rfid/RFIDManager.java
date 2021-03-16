package com.app.rfidmaster.rfid;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.app.rfidmaster.App;
import com.app.rfidmaster.R;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.interfaces.IUHF;

import java.util.HashMap;

import static android.content.Context.AUDIO_SERVICE;

/**
 * @author: cbb
 * @date: 2020/12/19 14:05
 * @description:
 */
public class RFIDManager {
    private final String TAG = getClass().getSimpleName();
    private static RFIDManager rfidManager = new RFIDManager();
    public RFIDWithUHFUART mReader;
    private HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();
    private SoundPool soundPool;
    private float volumnRatio;
    private AudioManager am;

    public static RFIDManager getInstance() {
        return rfidManager;
    }

    private RFIDManager() {
        initSound();
        init();
    }

    private void init() {
        try {
            mReader = RFIDWithUHFUART.getInstance();
            if (mReader == null) {
                return;
            }

        } catch (Exception ex) {
            toastMessage(ex.getMessage());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                mReader.init();
                Log.e(TAG,"RFID初始化成功");
            }
        }).start();
    }


    private void initSound() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
        soundMap.put(1, soundPool.load(App.getContext(), R.raw.barcodebeep, 1));
        soundMap.put(2, soundPool.load(App.getContext(), R.raw.serror, 1));
        am = (AudioManager) App.getContext().getSystemService(AUDIO_SERVICE);// 实例化AudioManager对象
    }

    /**
     * 播放提示音
     *
     * @param id 成功1，失败2
     */
    public void playSound(int id) {

        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 返回当前AudioManager对象的最大音量值
        float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);// 返回当前AudioManager对象的音量值
        volumnRatio = audioCurrentVolumn / audioMaxVolumn;
        try {
            soundPool.play(soundMap.get(id), volumnRatio, // 左声道音量
                    volumnRatio, //先级，0 右声道音量
                    1, // 优为最低
                    0, // 循环次数，0无不循环，-1无永远循环
                    1.5f // 回放速度 ，该值在0.5-2.0之间，1为正常速度
            );
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    public void toastMessage(String msg) {
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
    }


    public String read() {
        String ptrStr = "2";
        String cntStr = "6";
        String pwdStr = "00000000";

        boolean result = false;
        int Bank = IUHF.Bank_EPC;

        String entity = "";
        entity = mReader.readData(pwdStr,
                Bank,
                Integer.parseInt(ptrStr),
                Integer.parseInt(cntStr));
        if (entity != null) {
            result = true;
            Log.e(TAG, "-------" + entity);
        } else {
            result = false;
            toastMessage("读取失败");
        }
        if (!result) {
            playSound(2);
        } else {
            playSound(1);
        }
        return entity;
    }

    public void destroy() {
        if (mReader != null) {
            mReader.free();
        }
    }
}
