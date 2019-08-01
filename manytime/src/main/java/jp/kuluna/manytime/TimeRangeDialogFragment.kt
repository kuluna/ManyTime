package jp.kuluna.manytime

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import java.util.*

abstract class TimeRangeDialogFragment : DialogFragment() {
    protected lateinit var rootView: ConstraintLayout
    protected lateinit var textViewTitle: TextView
    protected lateinit var timeRangeView: TimeRangeView
    protected lateinit var numberPadView: NumberPadView
    protected lateinit var buttonCancel: Button
    protected lateinit var buttonOk: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!).apply {
            // もともとあったダイアログのタイトルを消す
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            // Viewをセット
            layoutInflater.inflate(R.layout.dialog_time_range, null, false).let {
                setContentView(it)

                rootView = it.findViewById(R.id.dialog_time_range_root)
                textViewTitle = it.findViewById(R.id.time_range_dialog_text_title)
                timeRangeView = it.findViewById(R.id.time_range_dialog_time_range_view)
                numberPadView = it.findViewById(R.id.time_range_dialog_number_pad_view)
                buttonCancel = it.findViewById(R.id.time_range_dialog_button_cancel)
                buttonOk = it.findViewById(R.id.time_range_dialog_button_ok)
            }

            // もともとあったダイアログの背景を消して画面目一杯にViewを広げる
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //// Setup Views ////
        // TimeRangeに表示するテキストをセット
        timeRangeView.run {
            textStartTime = startTimeText
            textEndTime = endTimeText
            textOverDayEndTime = overDayEndTimeText
        }

        // NumberPadのの入力イベントを拾ってTimeRangeに渡し、結果を受け取る
        numberPadView.onKeyClick = {
            // OKモードならそのまま完了できる
            if (numberPadView.positiveKeyMode == NumberPadView.PositiveKeyMode.OK
                && it == InputKey.OK
            ) {
                val result = timeRangeView.getTimeRange()
                if (onOkButtonClick(result)) {
                    dismiss()
                }
            }

            timeRangeView.input(it)

            val currentTimeRange = timeRangeView.getTimeRange()
            // 入力された日付データを扱って良いか検証する
            validate(currentTimeRange)

            setKeyMode(timeRangeView.focus)
        }

        timeRangeView.onTimeClickListener = OnTimeClickListener {
            setKeyMode(it)
        }

        //// Events /////

        // 黒い背景部分をクリックした時にダイアログを閉じる
        rootView.setOnClickListener {
            if (isCancelable) {
                dismiss()
            }
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        buttonOk.setOnClickListener {
            val result = timeRangeView.getTimeRange()
            if (onOkButtonClick(result)) {
                dismiss()
            }
        }
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

    protected fun setTimeRange(range: ClosedRange<Date>) {
        timeRangeView.setTimeRange(range)
    }

    private fun setKeyMode(focus: TimeRangeView.CurrentFocus) {
        if (focus == TimeRangeView.CurrentFocus.END_MIN || focus == TimeRangeView.CurrentFocus.COMPLETE) {
            numberPadView.positiveKeyMode = NumberPadView.PositiveKeyMode.OK
        } else {
            numberPadView.positiveKeyMode = NumberPadView.PositiveKeyMode.NEXT
        }
    }

    //// Open /////

    open val startTimeText: String = "Start"
    open val endTimeText: String = "End"
    open val overDayEndTimeText: String = "End(Next)"

    open fun validate(timeRange: ClosedRange<Date>): Boolean {
        return true
    }

    /**
     * OKボタンをクリックした時に呼ばれます。
     * @return trueならダイアログを閉じる
     */
    abstract fun onOkButtonClick(timeRange: ClosedRange<Date>): Boolean
}
