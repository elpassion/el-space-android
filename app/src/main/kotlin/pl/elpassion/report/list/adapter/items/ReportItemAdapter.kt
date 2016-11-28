package pl.elpassion.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.ItemAdapter
import kotlinx.android.synthetic.main.report_item.view.*
import pl.elpassion.R
import pl.elpassion.report.Report

class ReportItemAdapter(val report: Report) : ItemAdapter<ReportItemAdapter.VH>(R.layout.report_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.reportHeader.text = "${report.reportedHours}h - ${report.projectName}"
        holder.itemView.reportContent.text = report.description
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}