package pl.elpassion.report.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.R
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.report.Report
import pl.elpassion.report.add.ReportAddActivity
import pl.elpassion.report.list.adapter.ReportsAdapter
import pl.elpassion.report.list.adapter.items.DayItemAdapter
import pl.elpassion.report.list.adapter.items.DayNotFilledInItemAdapter
import pl.elpassion.report.list.adapter.items.ReportItemAdapter
import pl.elpassion.report.list.adapter.items.WeekendDayItem

class ReportListActivity : AppCompatActivity(), ReportList.View {

    val controller by lazy {
        ReportListController(ReportList.ServiceProvider.get(), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_list_activity)
        reportsContainer.layoutManager = LinearLayoutManager(this)
        controller.onCreate()
    }

    override fun openEditReportScreen(report: Report) {

    }

    override fun showMonthName(monthName: String) {
    }

    override fun openAddReportScreen(date: String) {
        ReportAddActivity.startForResult(this, date, ADD_REPORT_SCREEN_REQUEST_CODE)
    }

    override fun hideLoader() {
        hideLoader(reportListCoordinator)
    }

    override fun showLoader() {
        showLoader(reportListCoordinator)
    }

    override fun showError(it: Throwable) {
        Log.e("Error", it.message, it)
        Snackbar.make(reportListCoordinator, R.string.report_list_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showDays(days: List<Day>, listener: OnDayClickListener) {
        reportsContainer.adapter = ReportsAdapter(days.flatMap {
            listOf(createDayAdapter(it, listener)) + it.reports.map(::ReportItemAdapter)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_REPORT_SCREEN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            controller.refreshReportList()
        }
    }

    private fun createDayAdapter(it: Day, listener: OnDayClickListener) =
            if (it.isWeekendDay && it.reports.isEmpty()) WeekendDayItem(it, listener)
            else if (it.isNotFilledIn()) DayNotFilledInItemAdapter(it, listener)
            else DayItemAdapter(it, listener)

    companion object {
        private val ADD_REPORT_SCREEN_REQUEST_CODE = 100
        fun start(context: Context) {
            context.startActivity(Intent(context, ReportListActivity::class.java))
        }
    }

}

