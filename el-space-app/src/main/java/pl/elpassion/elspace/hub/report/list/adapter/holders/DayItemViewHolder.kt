package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.content.res.Resources
import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.extensions.toStringWithoutZeroes
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.DayWithHourlyReports
import pl.elpassion.elspace.hub.report.list.OnDayClick

class DayItemViewHolder(itemView: View, private val onDayClick: OnDayClick) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as DayWithHourlyReports
        itemView.apply {
            setOnClickListener { onDayClick(item.date) }
            dayNumber.text = item.name
            setTotalHoursTextWithIndicator(item)
        }
    }

    private fun setTotalHoursTextWithIndicator(item: DayWithHourlyReports) {
        itemView.totalHours.text = getTotalHoursText(itemView.resources, item)
    }

    private fun getTotalHoursText(resources: Resources, item: DayWithHourlyReports) = resources.getQuantityString(
            R.plurals.report_list_total_hours,
            item.reportedHours.toInt(),
            item.reportedHours.toStringWithoutZeroes())

}