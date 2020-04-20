package jp.kuluna.manytime.sample

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
            f.arguments = Bundle().apply {
                putInt(EXTRA_INITIAL_VALUE, initialValue)
            }
            f.show(fm, "sampleMoneyInputDialog")
        }
    }

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
