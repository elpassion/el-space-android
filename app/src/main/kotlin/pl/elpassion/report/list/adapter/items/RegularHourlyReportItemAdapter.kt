package pl.elpassion.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.StableItemAdapter
import kotlinx.android.synthetic.main.regular_hourly_report_item.view.*
import pl.elpassion.R
import pl.elpassion.report.RegularHourlyReport
import pl.elpassion.report.list.OnReportClickListener

class RegularHourlyReportItemAdapter(val report: RegularHourlyReport, val onReportClickListener: OnReportClickListener) : StableItemAdapter<RegularHourlyReportItemAdapter.VH>(report.id, R.layout.regular_hourly_report_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { onReportClickListener.onReport(report) }
        holder.itemView.reportHeader.text = "${report.reportedHours}h - ${report.project.name}"
        holder.itemView.reportContent.text = report.description.trim()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}