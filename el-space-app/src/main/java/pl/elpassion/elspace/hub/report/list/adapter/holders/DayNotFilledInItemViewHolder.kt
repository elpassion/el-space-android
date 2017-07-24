package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.support.v4.content.ContextCompat
import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.day_not_filled_in_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.DayWithoutReports
import pl.elpassion.elspace.hub.report.list.ReportListController
import pl.elpassion.elspace.hub.report.list.shouldHaveReports

class DayNotFilledInItemViewHolder(itemView: View, val controller: ReportListController) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {
        item as DayWithoutReports
        itemView.apply {
            setOnClickListener { controller.onDayClick(item.date) }
            dayNumber.text = item.name
            if (item.shouldHaveReports()) {
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

    companion object {
        operator fun invoke(controller: ReportListController) =
                R.layout.day_not_filled_in_item to { itemView: View -> DayNotFilledInItemViewHolder(itemView, controller) }
    }
}