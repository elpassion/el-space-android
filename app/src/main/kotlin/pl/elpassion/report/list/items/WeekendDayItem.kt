package pl.elpassion.report.list.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.ItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.R
import pl.elpassion.report.list.Day
import pl.elpassion.report.list.OnDayClickListener

class WeekendDayItem(val day: Day, val listener: OnDayClickListener) : ItemAdapter<WeekendDayItem.VH>(R.layout.weekend_day_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { listener.onDay(day.dayNumber) }
        holder.itemView.dayNumber.text = day.dayNumber.toString()
        if (day.reports.isNotEmpty()) {
            holder.itemView.totalHours.text = "Total: ${day.reportedHours} hours"
        } else {
            holder.itemView.totalHours.text = null
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}