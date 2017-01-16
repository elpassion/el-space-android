package pl.elpassion.report.add

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import pl.elpassion.report.add.details.ReportAddDetailsFragment

class ReportAddPagerAdapter(private val items: List<ReportAddDetailsFragment>, activity: ReportAddActivity) :
        FragmentStatePagerAdapter(activity.supportFragmentManager) {

    override fun getItem(position: Int): Fragment = items[position]

    override fun getCount(): Int = items.size
}