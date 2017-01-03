package pl.elpassion.report.add

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import pl.elpassion.report.add.details.ReportAddDetails

class ReportAddPagerAdapter(private val items: List<ReportAddDetails.View>, activity: ReportAddActivity) :
        FragmentStatePagerAdapter(activity.supportFragmentManager) {

    override fun getItem(position: Int): Fragment = items[position] as Fragment

    override fun getCount(): Int = items.size
}