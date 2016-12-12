package pl.elpassion.report.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.elpassion.android.commons.recycler.ItemAdapter
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.R
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.report.Report
import pl.elpassion.report.add.ReportAddActivity
import pl.elpassion.report.edit.ReportEditActivity
import pl.elpassion.report.list.adapter.ReportsAdapter
import pl.elpassion.report.list.adapter.addSeparators
import pl.elpassion.report.list.adapter.items.*
import pl.elpassion.report.list.service.DateChangeObserverImpl
import pl.elpassion.report.list.service.ReportDayServiceImpl
import java.util.*

class ReportListActivity : AppCompatActivity(), ReportList.View {

    private val controller by lazy {
        ReportListController( ReportDayServiceImpl(ReportList.ServiceProvider.get()), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_list_activity)
        reportsContainer.layoutManager = LinearLayoutManager(this)
        controller.onCreate()
        nextMonthButton.setOnClickListener { controller.onNextMonth() }
        prevMonthButton.setOnClickListener { controller.onPreviousMonth() }
    }

    override fun openEditReportScreen(report: Report) {
        ReportEditActivity.startForResult(this, report, EDIT_REPORT_SCREEN_REQUEST_CODE)
    }

    override fun showMonthName(monthName: String) {
        monthTitle.text = monthName
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

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(reportListCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.refresh_action, { controller.refreshReportList() })
                .show()
    }

    override fun showDays(days: List<Day>, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener) {
        val contentItemAdapters = createContentItemsAdapters(days, onDayClickListener, onReportClickListener)
        val adapterList = listOf(EmptyItemAdapter()) + contentItemAdapters + EmptyItemAdapter()
        reportsContainer.adapter = ReportsAdapter(addSeparators(adapterList))
    }

    private fun createContentItemsAdapters(days: List<Day>, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener): List<ItemAdapter<out RecyclerView.ViewHolder>> {
        val itemAdapters = days.flatMap {
            listOf(createDayAdapter(it, onDayClickListener)) + it.reports.map { ReportItemAdapter(it, onReportClickListener) }
        }
        return itemAdapters
    }

    private fun createDayAdapter(it: Day, listener: OnDayClickListener) =
            when {
                it.isWeekendDay && it.reports.isEmpty() -> WeekendDayItem(it, listener)
                it.isNotFilledIn() -> DayNotFilledInItemAdapter(it, listener)
                else -> DayItemAdapter(it, listener)
            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_REPORT_SCREEN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            controller.refreshReportList()
        } else if (requestCode == EDIT_REPORT_SCREEN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            controller.refreshReportList()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val ADD_REPORT_SCREEN_REQUEST_CODE = 100
        private val EDIT_REPORT_SCREEN_REQUEST_CODE = 105
        fun start(context: Context) {
            context.startActivity(Intent(context, ReportListActivity::class.java))
        }
    }

}

