package com.app.rfidmaster.net

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by 坎坎.
 * Date: 2020/9/15
 * Time: 16:02
 * describe:
 */
class RetrofitServiceManager
private constructor() {

    companion object {
        private val instance = RetrofitServiceManager()
        fun getInstance() = instance
    }

    var mRetrofit: Retrofit

    var client: OkHttpClient

    fun cancel() {
        client.dispatcher().cancelAll()
    }

    init {
        //连接超时时间//写操作 超时时间
        val defaultTimeOut = 60L
        val defaultOuTimeOut = 60L
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(defaultTimeOut, TimeUnit.SECONDS)
        builder.writeTimeout(defaultOuTimeOut, TimeUnit.SECONDS)
        builder.readTimeout(defaultOuTimeOut, TimeUnit.SECONDS)
        // 日志拦截器
        val interceptor = HttpLoggingInterceptor(HttpLogger())
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(interceptor)
        client = builder.build()
        // 创建Retrofit
        mRetrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(UrlConstants.BASE_URL).build()
    }


    /**
     * 获取对应的Service
     *
     * @param service Service 的 class
     * @param <T>
     * @return
    </T> */
    fun <T> create(service: Class<T>?): T {
        return mRetrofit.create(service)
    }

}