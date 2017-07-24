package pl.elpassion.elspace.hub.report.list.adapter.items

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.regular_hourly_report_item.view.*
import pl.elpassion.elspace.common.extensions.toStringWithoutZeroes
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.ReportListController

class RegularReportItemViewHolder(itemView: View, val controller: ReportListController) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as RegularHourlyReport
        itemView.apply {
            setOnClickListener { controller.onReportClick(item) }
            reportHeader.text = "${item.reportedHours.toStringWithoutZeroes()}h - ${item.project.name}"
            reportContent.text = item.description.trim()
        }
    }
}