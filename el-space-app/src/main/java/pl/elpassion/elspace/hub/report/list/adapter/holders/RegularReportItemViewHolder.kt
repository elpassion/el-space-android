package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.regular_hourly_report_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.extensions.toStringWithoutZeroes
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.OnReportClick

class RegularReportItemViewHolder(itemView: View, private val onReportClick: OnReportClick) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as RegularHourlyReport
        itemView.apply {
            setOnClickListener { onReportClick(item) }
            reportHeader.text = "${item.reportedHours.toStringWithoutZeroes()}h - ${item.project.name}"
            reportContent.text = item.description.trim()
        }
    }

    companion object {
        fun create(onReportClick: OnReportClick) =
                R.layout.regular_hourly_report_item to { itemView: View -> RegularReportItemViewHolder(itemView, onReportClick) }
    }
}