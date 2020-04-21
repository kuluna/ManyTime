package jp.kuluna.manytime.sample

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import jp.kuluna.manytime.NumberInputDialogFragment

class SampleMinutesNumberInputDialogFragment : NumberInputDialogFragment() {

    companion object {
        fun show(
            fm: FragmentManager,
            initialValue: Int
        ) {
            val f = SampleMinutesNumberInputDialogFragment()
            f.show(fm, "sampleMinutesInputDialog")
        }
    }

    override val maxNumberOfDigits: Int
        get() = 4

    override fun validate(inputValue: Int): Boolean {
        return true
    }

    override fun format(inputValue: Int) = "${inputValue}分"

    override fun onOkButtonClick(inputValue: Int): Boolean {
        Toast.makeText(requireContext(), "inputValue is $inputValue", Toast.LENGTH_LONG).show()
        return true
    }
}
