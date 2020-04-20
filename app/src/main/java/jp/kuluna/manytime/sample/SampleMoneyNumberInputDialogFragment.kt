package jp.kuluna.manytime.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import jp.kuluna.manytime.NumberInputDialogFragment
import java.text.NumberFormat
import java.util.*

class SampleMoneyNumberInputDialogFragment : NumberInputDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setTitle("Sample Money Input Dialog")
        setInputValue(1)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun validate(inputValue: Int): Boolean {
        return true
    }

    override fun format(inputValue: Int) = inputValue.yen

    override fun onOkButtonClick(inputValue: String): Boolean {
        Toast.makeText(requireContext(), "inputValue is $inputValue", Toast.LENGTH_LONG).show()
        return true
    }

    private val Number?.yen: String
        get() = NumberFormat.getCurrencyInstance(Locale.JAPAN).format(this ?: 0)
}
