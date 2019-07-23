package jp.kuluna.manytime

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import jp.kuluna.manytime.databinding.DialogTimeRangeBinding
import jp.kuluna.timerange.OnTimeClickListener
import java.util.*

abstract class TimeRangeDialogFragment : DialogFragment() {
    protected lateinit var binding: DialogTimeRangeBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(context!!).apply {
            // もともとあったダイアログのタイトルを消す
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            // Viewをセット
            binding = DialogTimeRangeBinding.inflate(layoutInflater, null, false)
            setContentView(binding.root)

            // もともとあったダイアログの背景を消して画面目一杯にViewを広げる
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //// Setup Views ////
        // TimeRangeに表示するテキストをセット
        binding.timeRangeDialogTimeRangeView.run {
            textStartTime = startTimeText
            textEndTime = endTimeText
            textOverDayEndTime = overDayEndTimeText
        }

        // NumberPadのの入力イベントを拾ってTimeRangeに渡し、結果を受け取る
        binding.timeRangeDialogNumberPadView.onKeyClick = {
            // OKモードならそのまま完了できる
            if (binding.timeRangeDialogNumberPadView.positiveKeyMode == NumberPadView.PositiveKeyMode.OK
                && it == InputKey.OK
            ) {
                val result = binding.timeRangeDialogTimeRangeView.getTimeRange()
                if (onOkButtonClick(result)) {
                    dismiss()
                }
            }

            binding.timeRangeDialogTimeRangeView.input(it)

            val currentTimeRange = binding.timeRangeDialogTimeRangeView.getTimeRange()
            // 入力された日付データを扱って良いか検証する
            validate(currentTimeRange)

            setKeyMode(binding.timeRangeDialogTimeRangeView.focus)
        }

        binding.timeRangeDialogTimeRangeView.onTimeClickListener = OnTimeClickListener {
            setKeyMode(it)
        }

        //// Events /////

        // 黒い背景部分をクリックした時にダイアログを閉じる
        binding.dialogTimeRangeRoot.setOnClickListener {
            if (isCancelable) {
                dismiss()
            }
        }

        binding.timeRangeDialogButtonCancel.setOnClickListener {
            dismiss()
        }

        binding.timeRangeDialogButtonOk.setOnClickListener {
            val result = binding.timeRangeDialogTimeRangeView.getTimeRange()
            if (onOkButtonClick(result)) {
                dismiss()
            }
        }
    }

    protected fun setTitle(@StringRes resId: Int) {
        binding.timeRangeDialogTextTitle.run {
            setText(resId)
            visibility = if (text.isNotBlank()) View.VISIBLE else View.GONE
        }
    }

    protected fun setTitle(title: String) {
        binding.timeRangeDialogTextTitle.run {
            text = title
            visibility = if (title.isNotBlank()) View.VISIBLE else View.GONE
        }
    }

    protected fun setTimeRange(range: ClosedRange<Date>) {
        binding.timeRangeDialogTimeRangeView.setTimeRange(range)
    }

    private fun setKeyMode(focus: TimeRangeView.CurrentFocus) {
        if (focus == TimeRangeView.CurrentFocus.END_MIN || focus == TimeRangeView.CurrentFocus.COMPLETE) {
            binding.timeRangeDialogNumberPadView.positiveKeyMode = NumberPadView.PositiveKeyMode.OK
        } else {
            binding.timeRangeDialogNumberPadView.positiveKeyMode = NumberPadView.PositiveKeyMode.NEXT
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
