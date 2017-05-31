package pl.elpassion.elspace.hub.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.list.DayWithoutReports
import pl.elpassion.elspace.hub.report.list.OnDayClick

class WeekendDayItem(override val day: DayWithoutReports, val onDayClick: OnDayClick) : StableItemAdapter<WeekendDayItem.VH>(day.uuid, R.layout.weekend_day_item), DayItem {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { onDayClick(day.date) }
        holder.itemView.dayNumber.text = day.name
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}