package jp.kuluna.manytime

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import org.apache.commons.lang3.time.DateUtils
import java.util.*

class TimeRangeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val root: View =
        LayoutInflater.from(context).inflate(R.layout.view_time_range, this, true)

    private val buttonStartHour by lazy { root.findViewById<Button>(R.id.time_range_text_start_hour) }
    private val buttonStartMin by lazy { root.findViewById<Button>(R.id.time_range_text_start_min) }
    private val buttonEndHour by lazy { root.findViewById<Button>(R.id.time_range_text_end_hour) }
    private val buttonEndMin by lazy { root.findViewById<Button>(R.id.time_range_text_end_min) }
    private val buttons: List<Button> by lazy {
        listOf(
            buttonStartHour,
            buttonStartMin,
            buttonEndHour,
            buttonEndMin
        )
    }
    private val textStartColon by lazy { root.findViewById<TextView>(R.id.time_range_text_start_colon) }
    private val textEndColon by lazy { root.findViewById<TextView>(R.id.time_range_text_end_colon) }

    /** 開始時刻 */
    val textViewStart by lazy { root.findViewById<TextView>(R.id.time_range_text_start) }
    /** 終了時刻 */
    val textViewEnd by lazy { root.findViewById<TextView>(R.id.time_range_text_end) }
    /** 注釈 */
    val textAnnotation by lazy { root.findViewById<TextView>(R.id.time_range_dialog_text_annotation) }

    private val defaultTextColor: ColorStateList? by lazy {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
        ContextCompat.getColorStateList(context, typedValue.resourceId)
    }

    private val primaryColor: ColorStateList? by lazy {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        ContextCompat.getColorStateList(context, typedValue.resourceId)
    }

    /////////// Public Properties ///////////

    /** 時間の基準日 */
    var baseDate: Date = DateUtils.truncate(Date(), Calendar.DAY_OF_MONTH)
        set(value) {
            field = DateUtils.truncate(value, Calendar.DAY_OF_MONTH)
        }

    /** このViewを操作可能にします。 */
    var editable: Boolean = false
        set(value) {
            field = value

            focus = if (value) {
                CurrentFocus.START_HOUR
            } else {
                CurrentFocus.NONE
            }
        }

    /** このViewの時刻表示を隠します */
    var masking: Boolean = false
        set(value) {
            field = value

            buttonStartHour.text = startHour.toDisplayTime()
            buttonStartMin.text = startMin.toDisplayTime()
            buttonEndHour.text = endHour.toDisplayTime()
            buttonEndMin.text = endMin.toDisplayTime()
        }

    var textColor: Int = 0
        set(value) {
            field = value
            focus = focus
            // 他のテキストの色も変更する
            setOtherTextColor(textColor)
        }

    var textAccentColor: Int = 0
        set(value) {
            field = value
            focus = focus
            // 他のテキストの色も変更する
            setTimeTextAccentColor(textColor)
        }

    var startHour: String = ""
        private set(value) {
            val (adjustTime, moveNext) = adjustTime(value, TimeField.HOUR)
            field = adjustTime
            buttonStartHour.text = adjustTime.toDisplayTime()
            overrideInput = false

            when (moveNext) {
                AdjustTimeResult.FocusTo.CURRENT, AdjustTimeResult.FocusTo.PREVIOUS -> nop()
                AdjustTimeResult.FocusTo.NEXT -> nextFocus()
            }
        }
    var startMin: String = ""
        private set(value) {
            val (adjustTime, moveNext) = adjustTime(value, TimeField.MIN)
            field = adjustTime
            buttonStartMin.text = adjustTime.toDisplayTime()
            overrideInput = false

            when (moveNext) {
                AdjustTimeResult.FocusTo.CURRENT -> nop()
                AdjustTimeResult.FocusTo.PREVIOUS -> previosFocus()
                AdjustTimeResult.FocusTo.NEXT -> nextFocus()
            }
        }
    var endHour: String = ""
        private set(value) {
            val (adjustTime, moveNext) = adjustTime(value, TimeField.HOUR)
            field = adjustTime
            buttonEndHour.text = adjustTime.toDisplayTime()
            overrideInput = false

            when (moveNext) {
                AdjustTimeResult.FocusTo.CURRENT -> nop()
                AdjustTimeResult.FocusTo.PREVIOUS -> previosFocus()
                AdjustTimeResult.FocusTo.NEXT -> nextFocus()
            }
        }
    var endMin: String = ""
        private set(value) {
            val (adjustTime, moveNext) = adjustTime(value, TimeField.MIN)
            field = adjustTime
            buttonEndMin.text = adjustTime.toDisplayTime()
            overrideInput = false

            when (moveNext) {
                AdjustTimeResult.FocusTo.CURRENT -> nop()
                AdjustTimeResult.FocusTo.PREVIOUS -> previosFocus()
                AdjustTimeResult.FocusTo.NEXT -> nextFocus()
            }
        }

    var focus: CurrentFocus = CurrentFocus.NONE
        set(value) {
            field = value

            // 全部のアクセントカラーを一旦外す
            buttons.forEach {
                if (textColor != 0) {
                    it.setTextColor(textColor)
                } else {
                    it.setTextColor(defaultTextColor)
                }
            }

            if (editable) {
                when (value) {
                    CurrentFocus.NONE -> nop()
                    CurrentFocus.START_HOUR -> buttonStartHour.setTextColor(primaryColor)
                    CurrentFocus.START_MIN -> buttonStartMin.setTextColor(primaryColor)
                    CurrentFocus.END_HOUR -> buttonEndHour.setTextColor(primaryColor)
                    CurrentFocus.END_MIN, CurrentFocus.COMPLETE -> buttonEndMin.setTextColor(primaryColor)
                }

                // フォーカスがあたるたび上書き入力可能にする
                overrideInput = true
            }
        }

    var textStartTime: String = "Start"
        set(value) {
            field = value
            textViewStart.text = value
        }

    var textEndTime: String = "End"
        set(value) {
            field = value
            setEndText()
        }

    var textOverDayEndTime: String = "End(Next)"
        set(value) {
            field = value
            setEndText()
        }

    /////////// Private Properties ///////////

    /**
     * 既に時刻が入力されているかに関わらず、新たな入力を許可する。
     *
     * trueの場合「12」と入力されている時刻に「1」を入力すると「01」となる。
     */
    private var overrideInput = true

    private val buttonTimeClick = OnClickListener {
        focus = when (it) {
            buttonStartHour -> CurrentFocus.START_HOUR
            buttonStartMin -> CurrentFocus.START_MIN
            buttonEndHour -> CurrentFocus.END_HOUR
            buttonEndMin -> CurrentFocus.END_MIN
            else -> CurrentFocus.NONE
        }

        onTimeClickListener?.onClick(focus)
    }

    /////////// Init ///////////

    init {
        // XMLから初期値設定
        if (attrs != null) {
            val args = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TimeRangeView,
                defStyleAttr,
                0
            )
            editable = args.getBoolean(R.styleable.TimeRangeView_editable, false)
            masking = args.getBoolean(R.styleable.TimeRangeView_masking, false)
            textColor = args.getColor(R.styleable.TimeRangeView_textColor, 0)
            textAccentColor = args.getColor(R.styleable.TimeRangeView_textAccentColor, 0)

            // オリジナルのテキストカラーをセットしているなら渡す
            if (textColor != 0) {
                setOtherTextColor(textColor)
            }
            // ラベルはあるならセットし、なければスルー
            args.getString(R.styleable.TimeRangeView_textStartTime)?.let { textStartTime = it }
            args.getString(R.styleable.TimeRangeView_textEndTime)?.let { textEndTime = it }
            args.getString(R.styleable.TimeRangeView_textOverDayEndTime)
                ?.let { textOverDayEndTime = it }
        }

        // Viewのセットアップ
        buttons.forEach { it.setOnClickListener(buttonTimeClick) }
        setEndText()
    }

    /** このViewがタッチ可能かどうかを返します */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return editable && super.dispatchTouchEvent(ev)
    }

    /////////// Public Functions ///////////

    fun setTimeRange(fromTo: ClosedRange<Date>) {
        // 最初のフォーカス先を覚えておく
        val currentFocus = focus

        baseDate = fromTo.start

        val startCalendar = Calendar.getInstance().apply { time = fromTo.start }
        startHour = startCalendar[Calendar.HOUR_OF_DAY].toString()
        startMin = startCalendar[Calendar.MINUTE].toString()

        val endCalendar = Calendar.getInstance().apply { time = fromTo.endInclusive }
        endHour = endCalendar[Calendar.HOUR_OF_DAY].toString()
        endMin = endCalendar[Calendar.MINUTE].toString()

        setEndText()
        // セットし終わったら元のフォーカスに戻す
        focus = currentFocus
    }

    fun getTimeRange(): ClosedRange<Date> {
        val startCalendar = Calendar.getInstance().apply { time = baseDate }
        startCalendar.add(Calendar.MINUTE, (startHour.toSafeInt() * 60) + startMin.toSafeInt())

        val endCalendar = Calendar.getInstance().apply { time = baseDate }
        endCalendar.add(Calendar.MINUTE, (endHour.toSafeInt() * 60) + endMin.toSafeInt())
        // 開始より終了日のほうが後ろの場合は後ろを1日増やす
        if (startCalendar >= endCalendar) {
            endCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return startCalendar.time..endCalendar.time
    }

    fun nextFocus() {
        focus = when (focus) {
            CurrentFocus.NONE -> CurrentFocus.START_HOUR
            CurrentFocus.START_HOUR -> CurrentFocus.START_MIN
            CurrentFocus.START_MIN -> CurrentFocus.END_HOUR
            CurrentFocus.END_HOUR -> CurrentFocus.END_MIN
            CurrentFocus.END_MIN -> CurrentFocus.COMPLETE
            CurrentFocus.COMPLETE -> CurrentFocus.COMPLETE
        }
    }

    fun previosFocus() {
        focus = when (focus) {
            CurrentFocus.NONE -> CurrentFocus.NONE
            CurrentFocus.START_HOUR -> CurrentFocus.START_HOUR
            CurrentFocus.START_MIN -> CurrentFocus.START_HOUR
            CurrentFocus.END_HOUR -> CurrentFocus.START_MIN
            CurrentFocus.END_MIN -> CurrentFocus.END_HOUR
            CurrentFocus.COMPLETE -> CurrentFocus.END_MIN
        }
    }

    /**
     * 入力された1文字を受け取り、現在のフォーカス対象に追加します。
     * @return 入力できたらtrue
     */
    fun input(key: InputKey): Boolean {
        fun add(source: String, value: String) = if (overrideInput) value else source + value
        fun drop(source: String) = if (source.isNotEmpty()) source.dropLast(1) else source

        when (key) {
            InputKey.OK -> nextFocus()
            InputKey.BACK -> {
                when (focus) {
                    CurrentFocus.START_HOUR -> startHour = drop(startHour)
                    CurrentFocus.START_MIN -> startMin = drop(startMin)
                    CurrentFocus.END_HOUR -> endHour = drop(endHour)
                    CurrentFocus.END_MIN -> endMin = drop(endMin)
                    CurrentFocus.COMPLETE -> endMin = drop(endMin)

                    CurrentFocus.NONE -> nop()
                }

                // 入力された結果日跨ぎになった場合はラベルを変える
                setEndText()
            }

            // 0〜9の数字
            else -> {
                when (focus) {
                    CurrentFocus.START_HOUR -> startHour = add(startHour, key.value)
                    CurrentFocus.START_MIN -> startMin = add(startMin, key.value)
                    CurrentFocus.END_HOUR -> endHour = add(endHour, key.value)
                    CurrentFocus.END_MIN -> endMin = add(endMin, key.value)
                    CurrentFocus.NONE, CurrentFocus.COMPLETE -> nop()
                }

                // 入力された結果日跨ぎになった場合はラベルを変える
                setEndText()
            }
        }

        return true
    }


    /////////// Private Functions ///////////

    private fun adjustTime(value: String, field: TimeField): AdjustTimeResult {
        when (field) {
            TimeField.HOUR -> {
                val num = value.toSafeInt()
                return if (num >= 24) {
                    AdjustTimeResult(value.dropLast(1), AdjustTimeResult.FocusTo.NEXT)
                } else {
                    val nextFocus = when {
                        // 時入力において1桁目が3以上、または0埋めの入力があったら次のフォーカスに移る
                        num >= 3 || value.length >= 2 -> AdjustTimeResult.FocusTo.NEXT
                        value.isEmpty() -> AdjustTimeResult.FocusTo.PREVIOUS
                        else -> AdjustTimeResult.FocusTo.CURRENT
                    }
                    AdjustTimeResult(value, nextFocus)
                }
            }

            TimeField.MIN -> {
                val num = value.toSafeInt()
                return if (num >= 60) {
                    AdjustTimeResult(value.dropLast(1), AdjustTimeResult.FocusTo.NEXT)
                } else {
                    val nextFocus = when {
                        // 時入力において1桁目が6以上、または0埋めの入力があったら次のフォーカスに移る
                        num >= 6 || value.length >= 2 -> AdjustTimeResult.FocusTo.NEXT
                        value.isEmpty() -> AdjustTimeResult.FocusTo.PREVIOUS
                        else -> AdjustTimeResult.FocusTo.CURRENT
                    }
                    AdjustTimeResult(value, nextFocus)
                }
            }
        }
    }

    private fun setEndText() {
        textViewEnd.text = if (getTimeRange().isOverDay) textOverDayEndTime else textEndTime
    }

    private fun setTimeTextAccentColor(@ColorInt accentColor: Int) {

    }

    private fun setOtherTextColor(@ColorInt color: Int) {
        if (color != 0) {
            textViewStart.setTextColor(textColor)
            textStartColon.setTextColor(textColor)
            textViewEnd.setTextColor(textColor)
            textEndColon.setTextColor(textColor)
        } else {
            textViewStart.setTextColor(defaultTextColor)
            textStartColon.setTextColor(defaultTextColor)
            textViewEnd.setTextColor(defaultTextColor)
            textEndColon.setTextColor(defaultTextColor)
        }
    }

    /** 何もしない。enumなどで使う */
    private fun nop() {}

    private fun String.toDisplayTime(): String {
        return when {
            masking -> "--"
            isEmpty() -> "00"
            length == 1 -> "0$this"
            else -> this
        }
    }

    private fun String.toSafeInt(): Int {
        return if (isEmpty()) 0 else toInt()
    }

    private val ClosedRange<Date>.isOverDay: Boolean
        get() = !DateUtils.isSameDay(start, endInclusive)

    /////////// Public Models ///////////

    enum class CurrentFocus {
        NONE, START_HOUR, START_MIN, END_HOUR, END_MIN, COMPLETE
    }

    /////////// Private Models ///////////

    private data class AdjustTimeResult(val value: String, val nextFocus: FocusTo) {
        enum class FocusTo {
            CURRENT, PREVIOUS, NEXT
        }
    }

    private enum class TimeField {
        HOUR, MIN
    }

    /////////// Listeners ///////////

    var onTimeClickListener: OnTimeClickListener? = null
}
