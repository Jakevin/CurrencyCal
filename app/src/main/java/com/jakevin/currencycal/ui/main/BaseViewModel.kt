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

abstract class BaseViewModel : ViewModel() {

    abstract fun getCurrentQuotes(context: Context?)

    var mainRepository: MainRepository = MainRepository()

    //Current Quot List
    var currentQuotList = MutableLiveData<List<CurrencyQuot>>(mutableListOf())

    //User pin lst
    var pinList = HashMap<String,Boolean>()

    //Calculate status
    var stateError = false

    //Input Status
    var lastNum = false

    //Check Input dot
    var lastDot = false

    //User input formula
    val formulaTxt = MutableLiveData<String>("")

    //Calculation results
    val resultTxt = MutableLiveData<String>("0")

    //Now Decimal Point Length
    val decimalPointLength = MutableLiveData<Int>(2)

    //User Selected Currency
    val selectedCurrency = MutableLiveData<CurrencyQuot>(CurrencyQuot("USD",1.0,true))

    var lastUpdateTime = MutableLiveData<String>("")

    fun inputNum(num: CharSequence) {
        var nowNum = formulaTxt.value
        if (stateError) {
            // If current state is Error, replace the error message
            nowNum = num.toString()
            stateError = false
        } else {
            // If not, already there is a valid expression so append to it
            nowNum = formulaTxt.value + (num.toString())
        }
        // Set the flag
        lastNum = true

        onEqual(nowNum)
        formulaTxt.postValue(nowNum)

    }

    fun inputOperator(operator: CharSequence) {
        if (lastNum && !stateError) {
            when (operator) {
                "＋" -> {
                    formulaTxt.postValue(formulaTxt.value + "+")
                }
                "－" -> {
                    formulaTxt.postValue(formulaTxt.value + "-")
                }
                "×" -> {
                    formulaTxt.postValue(formulaTxt.value + "*")
                }
                "÷" -> {
                    formulaTxt.postValue(formulaTxt.value + "/")
                }
            }

            lastNum = false
            lastDot = false // Reset the DOT flag
        }
    }

    fun inputC() {
        formulaTxt.postValue("") // Clear the screen
        resultTxt.postValue("0") // Clear the screen

        // Reset all the states and flags
        lastNum = false
        stateError = false
        lastDot = false
    }

    fun inputDot() {
        if (lastNum && !stateError && !lastDot) {
            formulaTxt.postValue(formulaTxt.value + ".")
            lastNum = false
            lastDot = true
        }
    }

    fun onEqual(formula: String) {
        if (lastNum && !stateError) {
            // Read the expression
            // Create an Expression (A class from exp4j library)
            val expression: Expression = ExpressionBuilder(formula).build()
            try {
                // Calculate the result and display
                val result: Double = expression.evaluate()
                resultTxt.postValue(java.lang.Double.toString(result))
                lastDot = true // Result contains a dot
            } catch (ex: ArithmeticException) {
                // Display an error message
                formulaTxt.postValue("Error")
                stateError = true
                lastNum = false
            }
        }
    }

    fun changeDecimalPointLength(){
        if(decimalPointLength.value!! >= 6){
            decimalPointLength.postValue(0)
        }else{
            decimalPointLength.postValue(decimalPointLength.value!! + 1)
        }
    }

    fun sortCurrentQuotList(context:Context?,currencyQuot:CurrencyQuot){
        pinList[currencyQuot.Currency] = currencyQuot.pin
        mainRepository.saveHashMap(context,pinList,Config.KEY_FAVORITE)

        var temp =  currentQuotList.value
        temp = temp?.sortedWith(compareBy({ !it.pin }, { it.Currency }))
        currentQuotList.postValue(temp)
    }
}