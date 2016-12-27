package pl.elpassion.report.list.adapter.items

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.day_not_filled_in_item.view.*
import com.elpassion.android.commons.recycler.StableItemAdapter
import pl.elpassion.R
import pl.elpassion.report.list.DayWithoutReports
import pl.elpassion.report.list.OnDayClickListener
import pl.elpassion.report.list.shouldHaveReports

class DayNotFilledInItemAdapter(val day: DayWithoutReports, val listener: OnDayClickListener) : StableItemAdapter<DayNotFilledInItemAdapter.VH>(day.uuid, R.layout.day_not_filled_in_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.run {
            setOnClickListener { listener.onDayDate(day.date) }
            dayNumber.text = day.name
            if (day.shouldHaveReports()) {
                hubIndicator.show()
                missingDayInformation.show()
                icoHubPlus.background = ContextCompat.getDrawable(context,R.drawable.ico_hub_plus_active)
            } else {
                hubIndicator.hide()
                missingDayInformation.visibility = View.INVISIBLE
                icoHubPlus.background = ContextCompat.getDrawable(context,R.drawable.ico_hub_plus_not_active)
            }
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}