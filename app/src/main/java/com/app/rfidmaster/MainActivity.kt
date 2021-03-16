package com.app.rfidmaster

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.rfidmaster.activity.PrintActivity
import com.app.rfidmaster.activity.ReadRFIDActivity
import com.app.rfidmaster.bluetooth.DevicesPopWindow
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_skip.setOnClickListener {
            startActivity(Intent(this,ReadRFIDActivity::class.java))
        }
    }

}