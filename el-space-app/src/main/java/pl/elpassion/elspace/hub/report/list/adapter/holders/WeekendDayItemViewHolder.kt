package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.weekend_day_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.DayWithoutReports
import pl.elpassion.elspace.hub.report.list.ReportListController

class WeekendDayItemViewHolder(itemView: View, val controller: ReportListController) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as DayWithoutReports
        itemView.apply {
            setOnClickListener { controller.onDayClick(item.date) }
            dayNumber.text = item.name
        }
    }

    companion object {
        operator fun invoke(controller: ReportListController) =
                R.layout.weekend_day_item to { itemView: View -> WeekendDayItemViewHolder(itemView, controller) }
    }
}