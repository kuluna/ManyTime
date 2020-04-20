package jp.kuluna.manytime

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment

abstract class NumberInputDialogFragment : DialogFragment() {
    protected lateinit var rootView: ConstraintLayout
    protected lateinit var textViewTitle: TextView
    protected lateinit var textViewInput: TextView
    protected lateinit var numberPadView: NumberPadView
    protected lateinit var buttonCancel: Button
    protected lateinit var buttonOk: Button

    //Intで数字を扱っているので10桁まで有効としている
    private val maxNumberOfDigits = 10
    private var currentInputValue = 0

    companion object {
        const val EXTRA_INITIAL_VALUE = "EXTRA_INITIAL_VALUE"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!).apply {
            // もともとあったダイアログのタイトルを消す
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            // Viewをセット
            layoutInflater.inflate(R.layout.dialog_number_input, null, false).let {
                setContentView(it)

                rootView = it.findViewById(R.id.dialog_time_range_root)
                textViewTitle = it.findViewById(R.id.text_title)
                textViewInput = it.findViewById(R.id.text_input)
                numberPadView = it.findViewById(R.id.number_pad_view)
                buttonCancel = it.findViewById(R.id.button_cancel)
                buttonOk = it.findViewById(R.id.button_ok)
            }

            // もともとあったダイアログの背景を消して画面目一杯にViewを広げる
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setUpView()
        setUpEvents()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    private fun setUpView() {
        currentInputValue = requireArguments().getInt(EXTRA_INITIAL_VALUE, 0)
        displayFormattedValue(currentInputValue)

        numberPadView.onKeyClick = {
            when {
                // OKモードならそのまま完了できる
                numberPadView.positiveKeyMode == NumberPadView.PositiveKeyMode.OK && it == InputKey.OK -> {
                    doOkButtonAction()
                }
                it == InputKey.BACK -> {
                    currentInputValue = if (currentInputValue.toString().length == 1) {
                        0
                    } else {
                        currentInputValue.toString().dropLast(1).toInt()
                    }
                    displayFormattedValue(currentInputValue)
                }
                else -> {
                    if (doDefaultValidation(currentInputValue)) {
                        val inputValue = (currentInputValue.toString() + it.value).toIntOrNull()
                        if (inputValue != null && validate(inputValue)) {
                            currentInputValue = inputValue
                            displayFormattedValue(currentInputValue)
                        }
                    }
                }
            }
        }
    }

    private fun setUpEvents() {
        // 黒い背景部分をクリックした時にダイアログを閉じる
        rootView.setOnClickListener {
            if (isCancelable) dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        buttonOk.setOnClickListener {
            doOkButtonAction()
        }
    }

    private fun doDefaultValidation(value: Int): Boolean {
        return value.toString().length < maxNumberOfDigits
    }

    private fun doOkButtonAction() {
        val result = textViewInput.text.toString()
        if (onOkButtonClick(result)) dismiss()
    }

    protected fun setTitle(@StringRes resId: Int) {
        textViewTitle.run {
            setText(resId)
            visibility = if (text.isNotBlank()) View.VISIBLE else View.GONE
        }
    }

    protected fun setTitle(title: String) {
        textViewTitle.run {
            text = title
            visibility = if (title.isNotBlank()) View.VISIBLE else View.GONE
        }
    }

    private fun displayFormattedValue(value: Int) {
        textViewInput.text = format(value)
    }

    open fun validate(inputValue: Int): Boolean {
        return true
    }

    /**
     * 入力値を任意のformatに変換
     * @return formatted value
     */
    open fun format(inputValue: Int) = inputValue.toString()

    /**
     * OKボタンをクリックした時に呼ばれます。
     * @return trueならダイアログを閉じる
     */
    abstract fun onOkButtonClick(inputValue: String): Boolean
}
