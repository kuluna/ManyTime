package jp.kuluna.manytime.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import jp.kuluna.manytime.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.openMoneyInputDialog.setOnClickListener {
            SampleMoneyNumberInputDialogFragment(1000).show(supportFragmentManager, "sampleMoneyInputDialog")
        }
        binding.openMinutesInputDialog.setOnClickListener {
            SampleMinutesNumberInputDialogFragment(2222).show(supportFragmentManager, "sampleMinuteInputDialog")
        }
    }
}
