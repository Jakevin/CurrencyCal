package com.jakevin.currencycal.api

import com.jakevin.currencycal.model.QuotV2
import retrofit2.Call
import retrofit2.http.GET

interface IRTERApi {
    @GET("/capi.php")
    fun live(): Call<HashMap<String,QuotV2>>
}