package jp.kuluna.manytime.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.kuluna.manytime.TimeRangeDialogFragment
import java.util.*

class SampleTimeRangeDialogFragment : TimeRangeDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setTitle("SampleTimeRangeDialogFragment")
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    override fun onOkButtonClick(timeRange: ClosedRange<Date>): Boolean = true
}
