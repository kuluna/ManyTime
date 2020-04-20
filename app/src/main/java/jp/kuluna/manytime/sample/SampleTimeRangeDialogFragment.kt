package jp.kuluna.manytime.sample

import jp.kuluna.manytime.TimeRangeDialogFragment
import java.util.*

class SampleTimeRangeDialogFragment : TimeRangeDialogFragment() {
    override fun onOkButtonClick(timeRange: ClosedRange<Date>): Boolean = true
}
