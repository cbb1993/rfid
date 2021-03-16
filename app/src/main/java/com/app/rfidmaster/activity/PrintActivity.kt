package com.app.rfidmaster.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.app.rfidmaster.R
import com.app.rfidmaster.activity.base.BaseActivity
import com.app.rfidmaster.bean.PrintBean
import com.app.rfidmaster.bluetooth.*
import com.app.rfidmaster.util.Constants
import com.app.rfidmaster.util.SharedPreferencesUtils
import com.app.rfidmaster.util.ToastUtil
import com.gprinter.command.FactoryCommand
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import kotlinx.android.synthetic.main.activity_print.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author:       cbb
 * @date:         2020/12/14 20:11
 * @description:
 */
class PrintActivity : BaseActivity() {
    private final val TAG = javaClass.simpleName
    override fun initLayout(): Int = R.layout.activity_print
    private lateinit var devicesPopWindow: DevicesPopWindow
    private val id = 0

    /**
     * ESC查询打印机实时状态指令
     */
    private val esc = byteArrayOf(0x10, 0x04, 0x02)

    /**
     * CPCL查询打印机实时状态指令
     */
    private val cpcl = byteArrayOf(0x1b, 0x68)

    /**
     * TSC查询打印机状态指令
     */
    private val tsc = byteArrayOf(0x1b, '!'.toByte(), '?'.toByte())

    private lateinit var printBean: PrintBean


    private var  handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            var cur = msg.what
            if(cur > 0){
                print()
                cur --
                sendEmptyMessageDelayed(cur,1500)
            }
        }
    }

    override fun initView() {
        super.initView()
        if (intent.getSerializableExtra(Constants.PrintBean) == null) {
            finish()
            return
        }
        printBean = intent.getSerializableExtra(Constants.PrintBean) as PrintBean

        devicesPopWindow = DevicesPopWindow.get(this)

        tv_no.text = "单据号：${printBean.rcvNo}"


        val print_count = SharedPreferencesUtils.readIntData("print_count")

        et_count.setText("$print_count")


        btn_complete.setOnClickListener {
            startActivity(Intent(this@PrintActivity, SelectAreaActivity::class.java))
        }

        btn_print.setOnClickListener {
            if(et_count.length() == 0){
                ToastUtil.show("请输入打印份数")
                return@setOnClickListener
            }
            SharedPreferencesUtils.addData("print_count",et_count.text.toString().toInt())

            val deviceConnFactoryManager: DeviceConnFactoryManager? =
                DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id]
            if (deviceConnFactoryManager == null || !deviceConnFactoryManager.connState) {
                // 没有连接 弹出蓝牙
                requestPermission()
                return@setOnClickListener
            }

            // 已经连接了 判断模式
            ThreadPool.getInstantiation().addSerialTask(Runnable {
                Log.e(
                    "--------",
                    "deviceConnFactoryManager.currentPrinterCommand   " + deviceConnFactoryManager.currentPrinterCommand
                )
                if (deviceConnFactoryManager.currentPrinterCommand == null || deviceConnFactoryManager.currentPrinterCommand == PrinterCommand.ESC) {
                    // 直接打印
                    Handler(Looper.getMainLooper()).post {
                        ToastUtil.show("开始打印")
                    }
                    handler.removeCallbacksAndMessages(null)
                    handler.sendEmptyMessage(et_count.text.toString().toInt())
                } else {
                    handler.removeCallbacksAndMessages(null)
                    //发送切换打印机模式后会断开连接，如果切换模式成功，打印机蜂鸣器会响一声，打印机关机，需手动开启
                    Handler(Looper.getMainLooper()).post {
                        ToastUtil.show("正在切换打印模式，需要重启打印机")
                    }
                    val bytes = FactoryCommand.changPrinterMode(FactoryCommand.printerMode.ESC)
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id]
                        .sendByteDataImmediately(bytes)
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].closePort(id)
                }
            })
        }
    }

    private fun print(){
        ThreadPool.getInstantiation().addSerialTask(Runnable {
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(PrintContent.getReceipt(printBean))
        })
    }

    override fun onDestroy() {
        devicesPopWindow.destroy()
        super.onDestroy()
    }


    private fun requestPermission() {
        AndPermission.with(this)
            .runtime()
            .permission(Permission.ACCESS_FINE_LOCATION,
                Permission.ACCESS_COARSE_LOCATION,
                Permission.READ_EXTERNAL_STORAGE,
                Permission.WRITE_EXTERNAL_STORAGE)
            .onGranted {
                devicesPopWindow.show(ll_root)
            }
            .onDenied {
                ToastUtil.show("请允许权限")
            }
            .start()
    }

    override fun onBackPressed() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        devicesPopWindow.onActivityResult(requestCode, resultCode, intent)
    }
}