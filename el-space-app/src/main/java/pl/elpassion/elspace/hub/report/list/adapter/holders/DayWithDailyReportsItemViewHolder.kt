package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.day_with_daily_report_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.DailyReportType
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.DayWithDailyReport
import pl.elpassion.elspace.hub.report.list.OnReportClick

class DayWithDailyReportsItemViewHolder(itemView: View, private val onReportClick: OnReportClick) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as DayWithDailyReport
        itemView.apply {
            setOnClickListener { onReportClick(item.report) }
            dayNumber.text = item.name
            totalHours.setText(createReportDescription(item))
        }
    }

    private fun createReportDescription(item: DayWithDailyReport) = when (item.report.reportType) {
        DailyReportType.SICK_LEAVE -> R.string.report_sick_leave_title
        DailyReportType.PAID_CONFERENCE -> R.string.report_paid_conference_title
        else -> R.string.report_unpaid_vacations_title
    }

    companion object {
        fun create(onReportClick: OnReportClick) =
                R.layout.day_with_daily_report_item to { itemView: View -> DayWithDailyReportsItemViewHolder(itemView, onReportClick) }
    }
}