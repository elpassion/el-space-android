package pl.elpassion.elspace.hub.report.list.adapter.items

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.day_not_filled_in_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.list.DayWithoutReports
import pl.elpassion.elspace.hub.report.list.OnDayClick
import pl.elpassion.elspace.hub.report.list.shouldHaveReports

class DayNotFilledInItemAdapter(override val day: DayWithoutReports, val onDayClick: OnDayClick) :
        StableItemAdapter<DayNotFilledInItemAdapter.VH>(day.uuid, R.layout.day_not_filled_in_item), DayItem {

    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.run {
            setOnClickListener { onDayClick(day.date) }
            dayNumber.text = day.name
            if (day.shouldHaveReports()) {
                hubIndicator.show()
                missingDayInformation.show()
                icoHubPlus.background = ContextCompat.getDrawable(context, R.drawable.ico_hub_plus_active)
            } else {
                hubIndicator.hide()
                missingDayInformation.visibility = View.INVISIBLE
                icoHubPlus.background = ContextCompat.getDrawable(context, R.drawable.ico_hub_plus_not_active)
            }
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}