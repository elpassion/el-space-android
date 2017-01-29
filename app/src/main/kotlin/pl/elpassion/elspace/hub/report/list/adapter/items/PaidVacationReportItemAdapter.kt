package pl.elpassion.elspace.hub.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import kotlinx.android.synthetic.main.regular_hourly_report_item.view.*
import pl.elpassion.R
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.list.OnReportClickListener

class PaidVacationReportItemAdapter(val report: PaidVacationHourlyReport, val onReportClickListener: OnReportClickListener) : StableItemAdapter<PaidVacationReportItemAdapter.VH>(report.id, R.layout.paid_vacations_report_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.run {
            setOnClickListener { onReportClickListener.onReport(report) }
            reportHeader.text = "${report.reportedHours}h - ${context.getString(R.string.report_paid_vacations_title)}"
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}