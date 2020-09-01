package com.jakevin.currencycal.api

import com.jakevin.currencycal.Config
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RTERApiManager private constructor() {
    private val retrofit: IRTERApi
    private val okHttpClient = OkHttpClient()

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_V2_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(IRTERApi::class.java)
    }

    companion object {
        private val manager = RTERApiManager()
        val client: IRTERApi
            get() = manager.retrofit
    }
}