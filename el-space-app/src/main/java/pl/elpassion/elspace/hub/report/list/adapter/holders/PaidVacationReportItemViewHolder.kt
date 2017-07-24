package pl.elpassion.elspace.hub.report.list.adapter.items

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.paid_vacations_report_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.extensions.toStringWithoutZeroes
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.list.AdapterItem

class PaidVacationReportItemViewHolder(itemView: View) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as PaidVacationHourlyReport
        itemView.apply {
            //setOnClickListener { controller.onReportClick() }
            reportHeader.text = "${item.reportedHours.toStringWithoutZeroes()}h - ${context.getString(R.string.report_paid_vacations_title)}"
        }
    }
}