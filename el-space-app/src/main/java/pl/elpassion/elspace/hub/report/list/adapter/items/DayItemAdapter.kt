package pl.elpassion.elspace.hub.report.list.adapter.items

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.extensions.toStringWithoutZeroes
import pl.elpassion.elspace.hub.report.list.DayWithHourlyReports
import pl.elpassion.elspace.hub.report.list.OnDayClick

class DayItemAdapter(override val day: DayWithHourlyReports, val onDayClick: OnDayClick) :
        StableItemAdapter<DayItemAdapter.VH>(day.uuid, R.layout.day_item), DayItem {

    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { onDayClick(day.date) }
        holder.itemView.dayNumber.text = day.name
        setTotalHoursTextWithIndicator(holder)
    }


    private fun setTotalHoursTextWithIndicator(holder: VH) {
        holder.itemView.totalHours.text = getTotalHoursText(holder.itemView.resources)
    }

    private fun getTotalHoursText(resources: Resources) = resources.getQuantityString(
            R.plurals.report_list_total_hours,
            day.reportedHours.toInt(),
            day.reportedHours.toStringWithoutZeroes())

    class VH(view: View) : RecyclerView.ViewHolder(view)
}