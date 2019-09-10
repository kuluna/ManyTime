package jp.kuluna.manytime

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.GridLayout

class NumberPadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr), OnClickListener {
    private lateinit var okButton: View
    private lateinit var nextButton: View

    var positiveKeyMode = PositiveKeyMode.OK
        set(value) {
            field = value
            when (value) {
                PositiveKeyMode.OK -> {
                    okButton.visibility = View.VISIBLE
                    nextButton.visibility = View.GONE
                }
                PositiveKeyMode.NEXT -> {
                    okButton.visibility = View.GONE
                    nextButton.visibility = View.VISIBLE
                }
            }
        }

    var positiveKeyEnabled: Boolean = true
        set(value) {
            field = value
            okButton.isEnabled = value
        }

    var onKeyClick: ((InputKey) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_number_pad, this, true) as GridLayout
        val grid = getChildAt(0) as GridLayout
        repeat(grid.childCount) {
            val button = grid.getChildAt(it)
            button.setOnClickListener(this)

            if (button.id == R.id.number_pad_button_ok) {
                okButton = button
            }
            if (button.id == R.id.number_pad_button_next) {
                nextButton = button
            }
        }

        // XMLの初期値設定
        if (attrs != null) {
            val args = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.NumberPadView,
                defStyleAttr,
                0
            )
            val keyModeInt = args.getInt(R.styleable.NumberPadView_positiveKeyMode, 0)

            positiveKeyMode = if (keyModeInt == 0) {
                PositiveKeyMode.OK
            } else {
                PositiveKeyMode.NEXT
            }

            positiveKeyEnabled = args.getBoolean(R.styleable.NumberPadView_positiveKeyEnabled, true)
        }
    }

    override fun onClick(view: View) {
        val inputKey = InputKey.from(view.tag.toString())
        onKeyClick?.invoke(inputKey)
    }

    class PositiveKeyMode {
        companion object {
            const val OK = 0
            const val NEXT = 1
        }
    }
}
