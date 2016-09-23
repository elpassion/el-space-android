package pl.elpassion.report.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.ItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.R

class DayItemAdapter(val day: Day, val listener: OnDayClickListener) : ItemAdapter<DayItemAdapter.VH>(R.layout.day_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { listener.onDay(day.dayNumber) }
        holder.itemView.dayNumber.text = day.dayNumber.toString()
        if (day.hasPassed && day.reports.isEmpty()) {
            holder.itemView.totalHours.text = "MISSING"

        } else {
            holder.itemView.totalHours.text = "Total: ${day.reportedHours} hours"
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}