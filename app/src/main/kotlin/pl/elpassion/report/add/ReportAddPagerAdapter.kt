package pl.elpassion.report.add

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter

class ReportAddPagerAdapter(private val items: List<ReportAddDetails.View>, activity: ReportAddActivity) :
        FragmentStatePagerAdapter(activity.supportFragmentManager) {

    override fun getItem(position: Int): Fragment = items[position]

    override fun getCount(): Int = items.size
}