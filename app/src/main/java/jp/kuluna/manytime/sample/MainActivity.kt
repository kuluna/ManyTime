package jp.kuluna.manytime.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        open_money_input_dialog.setOnClickListener {
            SampleMoneyNumberInputDialogFragment().show(supportFragmentManager, "sampleMoneyInputDialog")
        }
        open_minutes_input_dialog.setOnClickListener {
            SampleMinutesNumberInputDialogFragment().show(supportFragmentManager, "sampleMinuteInputDialog")
        }
    }
}
