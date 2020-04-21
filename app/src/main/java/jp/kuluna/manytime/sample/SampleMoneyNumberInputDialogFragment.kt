package jp.kuluna.manytime.sample

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import jp.kuluna.manytime.NumberInputDialogFragment
import java.text.NumberFormat
import java.util.*

class SampleMoneyNumberInputDialogFragment : NumberInputDialogFragment() {

    companion object {
        fun show(
            fm: FragmentManager,
            initialValue: Int
        ) {
            val f = SampleMoneyNumberInputDialogFragment()
            f.arguments = Bundle().apply {
                putInt(EXTRA_INITIAL_VALUE, initialValue)
                putString(EXTRA_TITLE, "Sample Money Input Dialog")
            }
            f.show(fm, "sampleMoneyInputDialog")
        }
    }

    override fun validate(inputValue: Int): Boolean {
        return true
    }

    override fun format(inputValue: Int) = inputValue.yen

    override fun onOkButtonClick(inputValue: Int): Boolean {
        Toast.makeText(requireContext(), "inputValue is $inputValue", Toast.LENGTH_LONG).show()
        return true
    }

    private val Number?.yen: String
        get() = NumberFormat.getCurrencyInstance(Locale.JAPAN).format(this ?: 0)
}
