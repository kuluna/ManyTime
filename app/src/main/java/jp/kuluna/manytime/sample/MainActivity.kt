package jp.kuluna.manytime.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import jp.kuluna.manytime.TimeRangeDialogFragment
import androidx.databinding.DataBindingUtil
import jp.kuluna.manytime.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.buttonShowDialog.setOnClickListener {
            SampleTimeRangeDialogFragment().show(supportFragmentManager, "dialog")
        }

        binding.openMoneyInputDialog.setOnClickListener {
            SampleMoneyNumberInputDialogFragment.show(supportFragmentManager, 1000)
        }
        binding.openMinutesInputDialog.setOnClickListener {
            SampleMinutesNumberInputDialogFragment.show(supportFragmentManager, 2222)
        }
    }
}
