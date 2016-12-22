package pl.elpassion.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.StableItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.R
import pl.elpassion.report.list.DayWithoutReports
import pl.elpassion.report.list.OnDayClickListener

class WeekendDayItem(val day: DayWithoutReports, val listener: OnDayClickListener) : StableItemAdapter<WeekendDayItem.VH>(day.uuid, R.layout.weekend_day_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { listener.onDayDate(day.date) }
        holder.itemView.dayNumber.text = day.name
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}