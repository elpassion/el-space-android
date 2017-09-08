package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.paid_vacations_report_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.extensions.toStringWithoutZeroes
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.OnReportClick

class PaidVacationReportItemViewHolder(itemView: View, private val onReportClick: OnReportClick) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as PaidVacationHourlyReport
        itemView.apply {
            setOnClickListener { onReportClick(item) }
            reportHeader.text = "${item.reportedHours.toStringWithoutZeroes()}h - ${context.getString(R.string.report_paid_vacations_title)}"
        }
    }

    companion object {
        fun create(onReportClick: OnReportClick) =
                R.layout.paid_vacations_report_item to { itemView: View -> PaidVacationReportItemViewHolder(itemView, onReportClick) }
    }
}