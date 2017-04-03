package pl.elpassion.elspace.hub.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.R
import pl.elpassion.elspace.common.extensions.toStringWithoutZeroes
import pl.elpassion.elspace.hub.report.list.DayWithHourlyReports
import pl.elpassion.elspace.hub.report.list.OnDayClickListener

class DayItemAdapter(override val day: DayWithHourlyReports, val listener: OnDayClickListener) :
        StableItemAdapter<DayItemAdapter.VH>(day.uuid, R.layout.day_item), DayItem {

    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { listener.onDayDate(day.date) }
        holder.itemView.dayNumber.text = day.name
        setTotalHoursTextWithIndicator(holder)
    }


    private fun setTotalHoursTextWithIndicator(holder: VH) {
        holder.itemView.totalHours.text = "Total: ${day.reportedHours.toStringWithoutZeroes()} hours"
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)
}