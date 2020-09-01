package com.jakevin.currencycal.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateFormat
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


open class MainRepository {

    //check first open
    fun isFirstOpen(context: Context?):Boolean{
        val key = "FirstOpen"

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val isFirstOpen =prefs.getBoolean(key,true)

        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putBoolean(key,false)
        editor.apply()

        return isFirstOpen
    }

    fun lastUpdateTime(context: Context?):String{
        val key = "LastUpdateTime"

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val lastUpdateTime =prefs.getString(key,"")
        if(lastUpdateTime==null){
            return ""
        }else{
            return lastUpdateTime
        }
    }

    fun setLastUpdateTime(context: Context?,lastTime:String){
        val key = "LastUpdateTime"

        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(key,lastTime)
        editor.apply()
    }

    //Save HashMap to SharedPreferences
    fun saveHashMap(context: Context?, list: HashMap<String,Boolean>?, key: String?) {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(list)
        editor.putString(key, json)
        editor.apply()
    }

    //Get HashMap from SharedPreferences
    fun getHashMap(context: Context?,key: String?): HashMap<String,Boolean> {
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json: String? = prefs.getString(key, null)
        val type: Type = object : TypeToken< HashMap<String,Boolean>?>() {}.getType()
        if(json!=null){
            return gson.fromJson(json, type)
        }else{
            return HashMap()
        }
    }
}