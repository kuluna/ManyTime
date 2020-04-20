package jp.kuluna.manytime.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import jp.kuluna.manytime.NumberInputDialogFragment

class SampleMinutesNumberInputDialogFragment(inputValue: Int) : NumberInputDialogFragment(inputValue) {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setTitle("Sample Minutes Input Dialog")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun validate(inputValue: Int): Boolean {
        return true
    }

    override fun format(inputValue: Int) = "${inputValue}分"

    override fun onOkButtonClick(inputValue: String): Boolean {
        Toast.makeText(requireContext(), "inputValue is $inputValue", Toast.LENGTH_LONG).show()
        return true
    }
}
