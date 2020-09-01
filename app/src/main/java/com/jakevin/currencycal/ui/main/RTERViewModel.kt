package com.jakevin.currencycal.ui.main

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakevin.currencycal.Config
import com.jakevin.currencycal.model.CurrencyQuot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class RTERViewModel : BaseViewModel() {
    var v2Repository: V2ApiRepository = V2ApiRepository()

    override fun getCurrentQuotes(context: Context?) {
        viewModelScope.launch(Dispatchers.IO) {
            pinList = mainRepository.getHashMap(context,Config.KEY_FAVORITE)
            if(mainRepository.isFirstOpen(context)){
                pinList.put("USD",true)
                pinList.put("JPY",true)
                pinList.put("TWD",true)
                mainRepository.saveHashMap(context,pinList,Config.KEY_FAVORITE)
            }

            var originalQuotes = v2Repository.loadInfo(context)

            val tempCurrencyQuotList:MutableList<CurrencyQuot> = mutableListOf()

            for ((key, value) in originalQuotes) {
                if(key.indexOf("USD")==0){
                    val newKey = key.replaceFirst("USD","")
                    if(pinList[newKey]!=null && pinList[newKey]==true){
                        tempCurrencyQuotList += CurrencyQuot( newKey ,value.Exrate, true)
                    }else{
                        if(newKey!="")
                            tempCurrencyQuotList += CurrencyQuot( newKey,value.Exrate,false)
                    }
                }
            }
            val temp = tempCurrencyQuotList.sortedWith(compareBy({ !it.pin }, { it.Currency }))

            lastUpdateTime.postValue("Last: "+v2Repository.lastUpdateTime(context))
            currentQuotList.postValue(temp)
        }
    }


}