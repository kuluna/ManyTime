package jp.kuluna.manytime.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import jp.kuluna.manytime.TimeRangeDialogFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_show_dialog).setOnClickListener {
            SampleTimeRangeDialogFragment().show(supportFragmentManager, "dialog")
        }
    }
}
