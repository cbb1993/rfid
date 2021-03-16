package com.app.rfidmaster.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.app.rfidmaster.R
import com.app.rfidmaster.activity.base.BaseActivity
import com.app.rfidmaster.bean.LoginBean
import com.app.rfidmaster.net.RequestApi
import com.app.rfidmaster.util.Constants
import com.app.rfidmaster.util.SharedPreferencesUtils
import com.app.rfidmaster.util.ToastUtil
import kotlinx.android.synthetic.main.activity_login.*

/**
 * @author:       cbb
 * @date:         2020/12/19 20:55
 * @description:
 */
class LoginActivity : BaseActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    override fun initLayout(): Int = R.layout.activity_login
    @SuppressLint("SetTextI18n")
    override fun initView() {
        super.initView()
        // 获取缓存
        val name = SharedPreferencesUtils.readData(Constants.USER_NAME, "")
        val password = SharedPreferencesUtils.readData(Constants.USER_PASSWORD, "")
        val remember = SharedPreferencesUtils.readBooleanData(Constants.REMEMBER, false)
        et_name.setText(name)
        checkbox_remember.isChecked = remember
        if (remember) {
            et_password.setText(password)
        }

        // 设置监听
        checkbox_visible.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        btn_login.setOnClickListener {
            if (validate()) {
                setCache()
                RequestApi.getInstance()
                    .login(this, et_name.text.toString(), et_password.text.toString(), null)
            }
        }

        tv_version.text = "版本号: " + getVerName()
    }

    private fun validate(): Boolean {
        if (et_name.length() < 4) {
            ToastUtil.show("请输入正确账号")
            return false
        }

        if (et_password.length() < 6) {
            ToastUtil.show("请输入正确密码")
            return false
        }
        return true
    }

    private fun setCache() {
        SharedPreferencesUtils.addData(Constants.USER_NAME, et_name.text.toString())
        SharedPreferencesUtils.addData(Constants.USER_PASSWORD, et_password.text.toString())
        SharedPreferencesUtils.addData(Constants.REMEMBER, checkbox_remember.isChecked)
    }

    private fun getVerName(): String {
        var verName = ""
        try {
            verName = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace();
        }
        return verName;
    }


}