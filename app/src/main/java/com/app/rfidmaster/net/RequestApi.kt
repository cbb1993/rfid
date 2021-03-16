package com.app.rfidmaster.net

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.app.rfidmaster.activity.LoginActivity
import com.app.rfidmaster.activity.ReadRFIDActivity
import com.app.rfidmaster.activity.SelectAreaActivity
import com.app.rfidmaster.bean.AreaBean
import com.app.rfidmaster.bean.LoginBean
import com.app.rfidmaster.bean.RFIDBean
import com.app.rfidmaster.bean.RFIDSaveBean
import com.app.rfidmaster.net.ApiService.LoginService
import com.app.rfidmaster.util.Constants
import com.app.rfidmaster.util.ScopeUtil
import com.app.rfidmaster.util.SharedPreferencesUtils
import com.app.rfidmaster.util.ToastUtil
import com.google.gson.JsonArray
import com.google.gson.JsonObject

/**
 * @author:       cbb
 * @date:         2020/12/22 22:26
 * @description:
 */
class RequestApi {
    companion object {
        private val requestApi = RequestApi()
        fun getInstance(): RequestApi {
            return requestApi
        }
    }

    fun reLogin(context: Context, resultListener: Result<LoginBean>?) {
        val name = SharedPreferencesUtils.readData(Constants.USER_NAME, "")
        val password = SharedPreferencesUtils.readData(Constants.USER_PASSWORD, "")
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
            ToastUtil.show("账号异常，请重新登录")
            LoginActivity.start(context)
            return
        }
        login(context, name, password, resultListener)
    }

    fun login(
        context: Context,
        name: String,
        password: String,
        resultListener: Result<LoginBean>?
    ) {
        val map = HashMap<String, String>()
        map["userName"] = name
        map["passWord"] = password
        ScopeUtil.get().launch({
            val result = RetrofitServiceManager.getInstance()
                .create(LoginService::class.java)
                .request(map)
            if (result.code == 0) {
                SharedPreferencesUtils.addData(Constants.TOKEN, result.data.tokenData.token)
                SharedPreferencesUtils.addData(Constants.NAME, result.data.resultUserInfo.userName)
                resultListener?.success(result.data)
                if (context is LoginActivity) {
                    context.startActivity(Intent(context, SelectAreaActivity::class.java))
                    context.finish()
                }
            } else {
                ToastUtil.show(result.message)
            }
        }, {
            ToastUtil.show(it.message)
            if (context is LoginActivity) {
            } else {
                LoginActivity.start(context)
            }
        })
    }

    fun getCustomer(context: Context, resultListener: ResultList<AreaBean>?) {
        ScopeUtil.get().launch({
            val result = RetrofitServiceManager.getInstance()
                .create(ApiService.CustomerService::class.java)
                .request(SharedPreferencesUtils.readData(Constants.TOKEN))
            if (result.code == 0) {
                resultListener?.success(result.data)
            } else {
                if ("无效的 token" == result.message || result.code == -110) {
                    // 重新登录刷新token 如果失败了会自动跳转登录页面
                    reLogin(context, object : Result<LoginBean> {
                        override fun success(t: LoginBean) {
                            requestApi.getCustomer(context, resultListener)
                        }
                    })
                } else {
                    ToastUtil.show(result.message)
                }

            }
        }, {
            ToastUtil.show(it.message)
        })
    }

    fun getRFIDInfo(context: Context, array: JsonArray, resultListener: RequestCallback<RFIDBean>) {
        ScopeUtil.get().launch({
            val result = RetrofitServiceManager.getInstance()
                .create(ApiService.GetRFIDInfoService::class.java)
                .request(SharedPreferencesUtils.readData(Constants.TOKEN), array)
            if (result.code == 0) {
                resultListener.success(result.data)
            } else {
                resultListener.failed()
                if ("无效的 token" == result.message || result.code == -110) {
                    // 重新登录刷新token 如果失败了会自动跳转登录页面
                    reLogin(context, null)
                }
            }
        }, {
            resultListener.failed()
            ToastUtil.show(it.message)
        })
    }

    fun save(context: Context, jsonObject: JsonObject, resultListener: Result<RFIDSaveBean>) {
        ReadRFIDActivity.submitting = true
        ScopeUtil.get().launch({
            val result = RetrofitServiceManager.getInstance()
                .create(ApiService.SubmitRFIDService::class.java)
                .request(SharedPreferencesUtils.readData(Constants.TOKEN), jsonObject)
            if (result.code == 0) {
                resultListener.success(result.data)
            } else {
                if ("无效的 token" == result.message || result.code == -110) {
                    // 重新登录刷新token 如果失败了会自动跳转登录页面
                    reLogin(context, object : Result<LoginBean> {
                        override fun success(t: LoginBean) {
                            save(context, jsonObject, resultListener)
                        }
                    })
                } else {
                    ToastUtil.show(result.message)
                }
            }
            ReadRFIDActivity.submitting = false
        }, {
            ToastUtil.show(it.message)
            ReadRFIDActivity.submitting = false
        },{
            ReadRFIDActivity.submitting = false
        })
    }


    interface Result<T> {
        fun success(t: T)
    }

    interface ResultList<T> {
        fun success(t: List<T>)
    }

    interface RequestCallback<T> {
        fun success(t: T)
        fun failed()
    }
}