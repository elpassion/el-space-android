package pl.elpassion.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.StableItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.R
import pl.elpassion.report.DailyReportType
import pl.elpassion.report.list.DayWithDailyReport

class DayWithDailyReportsItemAdapter(val day: DayWithDailyReport) : StableItemAdapter<DayWithDailyReportsItemAdapter.VH>(day.uuid, R.layout.day_with_daily_report_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.dayNumber.text = day.name
        holder.itemView.totalHours.setText(createReportDescription())
    }

    private fun createReportDescription(): Int {
        val dailyReportDescription = if (day.report.reportType == DailyReportType.SICK_LEAVE) {
            R.string.report_sick_leave_title
        } else {
            R.string.report_unpaid_vacations_title
        }
        return dailyReportDescription
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}