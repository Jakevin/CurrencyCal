package com.jakevin.currencycal.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateFormat
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jakevin.currencycal.R
import com.jakevin.currencycal.api.RTERApiManager
import com.jakevin.currencycal.model.QuotV2
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class V2ApiRepository : MainRepository() {
    private val apiService = RTERApiManager

    //Load Default Json when offline
    fun loadOfflineData(context: Context?):HashMap<String,QuotV2>{
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val type: Type = object : TypeToken< HashMap<String,QuotV2>?>() {}.getType()

        val json: String? = prefs.getString("offlineData", null)
        val gson = Gson()

        if(json!=null){
            return gson.fromJson(json, type)
        }else{
            val inputStream = context?.getResources()?.openRawResource(R.raw.rterdefaultfile)
            val jsonString: String = Scanner(inputStream).useDelimiter("\\A").next()
            return gson.fromJson(jsonString, type)
        }
    }

    fun saveOfflineData(context: Context?,result:HashMap<String,QuotV2>?){
        if(result!=null){
            //Save to local
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            val gson = Gson()
            val json: String = gson.toJson(result)
            editor.putString("offlineData", json)
            editor.apply()

            var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
            setLastUpdateTime(context, dateFormat.format(Calendar.getInstance().getTime()))
        }
    }

    //Call Api to get newest data
    fun loadInfo(context: Context?): HashMap<String,QuotV2> {
        var  result: Response<HashMap<String,QuotV2>>? = null
        try {
            result = apiService.client.live().execute()
            if(result.isSuccessful && result.body()!=null){
                saveOfflineData(context,result.body())
                return result.body()!!
            }else{
                return HashMap<String,QuotV2>()
            }
        } catch (e: SocketTimeoutException) {
            Log.e("API loadInfo",e.localizedMessage)
            return loadOfflineData(context)
        } catch (e: IOException) {
            Log.e("API loadInfo",e.localizedMessage)
            return loadOfflineData(context)
        }
    }
}