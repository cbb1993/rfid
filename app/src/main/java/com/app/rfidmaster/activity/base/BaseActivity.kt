package com.app.rfidmaster.activity.base

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.app.rfidmaster.R
import com.app.rfidmaster.util.BarUtils

/**
 * @author:       cbb
 * @date:         2020/11/23 16:54
 * @description:  activity基类
 */
abstract class BaseActivity() :AppCompatActivity(), IBaseActivity {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 防止息屏
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 设置StatusBar
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = resources.getColor(R.color.main)
        setContentView(initLayout())
        initView()
    }

    override fun initView() {

    }
}