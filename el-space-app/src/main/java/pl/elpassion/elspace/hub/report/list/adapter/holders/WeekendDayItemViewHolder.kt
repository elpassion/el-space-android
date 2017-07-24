package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.weekend_day_item.view.*
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.DayWithoutReports

class WeekendDayItemViewHolder(itemView: View) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as DayWithoutReports
        itemView.apply {
            //setOnClickListener { controller.onDayClick(item.date) }
            dayNumber.text = item.name
        }
    }
}