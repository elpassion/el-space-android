package pl.elpassion.report.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.ItemAdapter
import pl.elpassion.R

class ReportItemAdapter(val report: Report) : ItemAdapter<ReportItemAdapter.VH>(R.layout.report_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.reportHeader.text = "${report.reportedHours}h - ${report.projectName}"
        holder.itemView.reportContent.text = report.description
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}