package pl.elpassion.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_ID
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.elpassion.android.commons.recycler.StableItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.R
import pl.elpassion.report.list.Day
import pl.elpassion.report.list.OnDayClickListener

class DayItemAdapter(val day: Day, val listener: OnDayClickListener) : StableItemAdapter<DayItemAdapter.VH>(day.uuid, R.layout.day_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { listener.onDayDate(day.date) }
        holder.itemView.dayNumber.text = day.name
        setTotalHoursTextWithIndicator(holder)
    }


    private fun setTotalHoursTextWithIndicator(holder: VH) {
        holder.itemView.run {
            if (day.reports.isNotEmpty()) {
                totalHours.text = "Total: ${day.reportedHours} hours"
                hubIndicator.visibility = VISIBLE
            } else {
                totalHours.text = null
                hubIndicator.visibility = GONE
            }
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}