package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.weekend_day_item.view.*
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.DayWithoutReports
import pl.elpassion.elspace.hub.report.list.OnDayClick

class WeekendDayItemViewHolder(itemView: View, private val onDayClick: OnDayClick) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as DayWithoutReports
        itemView.apply {
            setOnClickListener { onDayClick(item.date) }
            dayNumber.text = item.name
        }
    }
}