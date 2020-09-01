package com.jakevin.currencycal.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakevin.currencycal.R
import com.jakevin.currencycal.model.CurrencyQuot
import kotlinx.android.synthetic.main.item_currency.view.*

class CurrencyItemAdapter (var myDataset: List<CurrencyQuot>, var selectedCurrency:CurrencyQuot, var resultTxt:String, var decimalPointLength :Int, var callBack : IGetCurrency) : androidx.recyclerview.widget.RecyclerView.Adapter<CurrencyItemAdapter.EventItemViewHolder>() {

    inner class EventItemViewHolder(val cardView: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventItemViewHolder {
        val cardView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency, parent, false) as ViewGroup
        return EventItemViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: EventItemViewHolder, position: Int) {
        holder.cardView.tvCurrency.text = myDataset[position].Currency
        holder.cardView.tvWithUSD.text = myDataset[position].Quot.toString()

        if(myDataset[position].pin !=null && myDataset[position].pin == true){
            holder.cardView.imgFavorite.setImageResource(R.drawable.ic_favorite)
        }else{
            holder.cardView.imgFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
        holder.cardView.imgFavorite.setOnClickListener {
            myDataset[position].pin = myDataset[position].pin?.not()
            callBack.onPin(myDataset[position])
        }

        holder.cardView.tvResult.text = String.format("%.${decimalPointLength}f",(resultTxt.toDouble() * ( myDataset[position].Quot/selectedCurrency.Quot)))
        holder.cardView.setOnClickListener {
            callBack.onSelect(myDataset[position])
        }

        holder.cardView.tvResult.setOnClickListener {
            callBack.onChangeDecimalPoint()
        }


    }

    override fun getItemCount() = myDataset.size
}

interface IGetCurrency{
    fun onSelect(currencyQuot:CurrencyQuot)
    fun onChangeDecimalPoint()
    fun onPin(currencyQuot:CurrencyQuot)
}
