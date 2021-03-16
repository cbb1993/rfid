package com.app.rfidmaster.net

import com.app.rfidmaster.bean.AreaBean
import com.app.rfidmaster.bean.LoginBean
import com.app.rfidmaster.bean.RFIDBean
import com.app.rfidmaster.bean.RFIDSaveBean
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * @author:       cbb
 * @date:         2020/12/20 15:42
 * @description:
 */
class ApiService {

    interface LoginService {
        @POST(UrlConstants.login)
        suspend fun request(@Body map: Map<String, String>): BaseResponse<LoginBean>
    }

    interface CustomerService {
        @GET(UrlConstants.customer)
        suspend fun request(@Header("token") token: String): BaseResponse<List<AreaBean>>
    }

    interface GetRFIDInfoService {
        @POST(UrlConstants.getRFIDInfo)
        suspend fun request(
            @Header("token") token: String,
            @Body array : JsonArray
        ): BaseResponse<RFIDBean>
    }

    interface SubmitRFIDService {
        @POST(UrlConstants.submitRFID)
        suspend fun request(
            @Header("token") token: String,
            @Body jsonObject: JsonObject
        ): BaseResponse<RFIDSaveBean>
    }
}