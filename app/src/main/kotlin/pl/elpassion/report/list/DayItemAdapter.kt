package pl.elpassion.report.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.ItemAdapter
import pl.elpassion.R

class DayItemAdapter(val day: Day) : ItemAdapter<DayItemAdapter.VH>(R.layout.day_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
        holder.itemView.dayNumber.text = day.dayNumber.toString()
        holder.itemView.totalHours.text = "Total: ${day.reportedHours} hours"
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}