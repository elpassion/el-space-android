package pl.elpassion.report.list.adapter.items

import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.ItemAdapter
import kotlinx.android.synthetic.main.day_item.view.*
import pl.elpassion.R
import pl.elpassion.report.list.Day
import pl.elpassion.report.list.OnDayClickListener

class DayItemAdapter(val day: Day, val listener: OnDayClickListener) : ItemAdapter<DayItemAdapter.VH>(R.layout.day_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.setOnClickListener { listener.onDayDate(day.date) }
        holder.itemView.dayNumber.text = day.name
        setTotalHoursTextWithIndicator(holder)
    }


    private fun setTotalHoursTextWithIndicator(holder: VH) {
        if (day.reports.isNotEmpty()) {
            holder.updateTextWithIndicator("Total: ${day.reportedHours} hours", R.color.filledIndicator)
        } else {
            holder.updateTextWithIndicator(null, R.color.unknownIndicator)
        }
    }

    private fun VH.updateTextWithIndicator(hourText: String?, color: Int) {
        itemView.apply {
            totalHours.text = hourText
            hubIndicator.setBackgroundColorFromRes(color)
        }
    }

    private fun View.setBackgroundColorFromRes(@ColorRes color: Int) = setBackgroundColor(ContextCompat.getColor(context, color))

    class VH(view: View) : RecyclerView.ViewHolder(view)

}