package com.jakevin.currencycal.ui.main

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakevin.currencycal.MainActivity
import com.jakevin.currencycal.R
import com.jakevin.currencycal.model.CurrencyQuot
import kotlinx.android.synthetic.main.main_fragment.*


class MainFragment : Fragment() {
    lateinit var numButtons: Array<TextView>
    lateinit var operatorButtons: Array<TextView>

    lateinit var adapter: CurrencyItemAdapter

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: RTERViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Put all Number Btn to array
        numButtons = arrayOf(
            btn0,
            btn1,
            btn2,
            btn3,
            btn4,
            btn5,
            btn6,
            btn7,
            btn8,
            btn9
        )

        // Put all Operator Btn to array
        operatorButtons = arrayOf(
            btnAdd,
            btnSubtract,
            btnMultiply,
            btnDivide
        )

        //init ViewModel
        viewModel = ViewModelProviders.of(this).get(RTERViewModel::class.java)

        //Get New Quotes
        viewModel.getCurrentQuotes(context)

        //Observe currency
        viewModel.selectedCurrency.observe(viewLifecycleOwner, Observer {
            tvCurrentCurrency.text = it.Currency
            adapter.selectedCurrency = it
            adapter.notifyDataSetChanged()
        })

        //Observe formula
        viewModel.formulaTxt.observe(viewLifecycleOwner, Observer {
            tvCurrentFormula.text = it
        })

        //Observe calculator result
        viewModel.resultTxt.observe(viewLifecycleOwner, Observer {
            tvCurrentSum.text = it

            adapter.resultTxt = it
            adapter.notifyDataSetChanged()
        })

        //Observe Api result
        viewModel.currentQuotList.observe(viewLifecycleOwner, Observer {
            adapter.myDataset = it
            adapter.notifyDataSetChanged()
        })

        //observe Decimal Point Length
        viewModel.decimalPointLength.observe(viewLifecycleOwner, Observer {
            adapter.decimalPointLength = it
            adapter.notifyDataSetChanged()
        })

        viewModel.lastUpdateTime.observe(viewLifecycleOwner, Observer {
            tvLastUpdate.text = it
        })

        initToolBar()

        //init Currency List
        initCurrencyList()

        setNumericOnClickListener()
        setOperatorOnClickListener()
    }

    private fun initToolBar() {

        imgInfo.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Info")
                .setMessage("Thx Api 即匯站 https://tw.rter.info/\nApp Creator Jakevin")
                .setPositiveButton("Close", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .show()
        }

    }


    private fun initCurrencyList() {
        listCurrency.layoutManager = LinearLayoutManager(context)

        //init Adapter
        adapter = CurrencyItemAdapter(
            viewModel.currentQuotList.value!!,
            viewModel.selectedCurrency.value!!,
            viewModel.resultTxt.value!!,
            viewModel.decimalPointLength.value!!,
            object :
                IGetCurrency {
                override fun onSelect(currencyQuot: CurrencyQuot) {
                    //set user selected currency
                    viewModel.selectedCurrency.postValue(currencyQuot)
                }

                override fun onChangeDecimalPoint() {
                    //change result decimal point length
                    viewModel.changeDecimalPointLength()
                }

                override fun onPin(currencyQuot: CurrencyQuot) {
                    viewModel.sortCurrentQuotList(context, currencyQuot)
                    listCurrency.scrollToPosition(0)
                }
            })

        listCurrency.adapter = adapter
    }

    private fun setNumericOnClickListener() {
        // Create a common OnClickListener
        val listener =
            View.OnClickListener { v -> // Just append/set the text of clicked button
                val button: TextView = v as TextView
                viewModel.inputNum(button.getText())
            }
        // Assign the listener to all the numeric buttons
        for (id in numButtons) {
            id.setOnClickListener(listener)
        }
    }

    private fun setOperatorOnClickListener() {
        // Create a common OnClickListener for operators
        val listener =
            View.OnClickListener { v ->
                val button: TextView = v as TextView
                viewModel.inputOperator(button.getText())
            }

        // Assign the listener to all the operator buttons
        for (id in operatorButtons) {
            id.setOnClickListener(listener)
        }

        // Decimal point
        btnDot.setOnClickListener {
            viewModel.inputDot()
        }
        // Clear button
        btnC.setOnClickListener {
            viewModel.inputC()
        }
    }

}