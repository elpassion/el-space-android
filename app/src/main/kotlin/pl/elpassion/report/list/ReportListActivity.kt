package pl.elpassion.report.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.elpassion.android.commons.recycler.StableItemAdapter
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.R
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.report.DailyReport
import pl.elpassion.report.HourlyReport
import pl.elpassion.report.PaidVacationHourlyReport
import pl.elpassion.report.RegularHourlyReport
import pl.elpassion.report.add.ReportAddActivity
import pl.elpassion.report.edit.daily.ReportEditDailyActivity
import pl.elpassion.report.edit.paidvacation.ReportEditPaidVacationActivity
import pl.elpassion.report.edit.regular.ReportEditRegularActivity
import pl.elpassion.report.list.adapter.ReportsAdapter
import pl.elpassion.report.list.adapter.items.*
import pl.elpassion.report.list.service.ReportDayServiceImpl

class ReportListActivity : AppCompatActivity(), ReportList.View {

    private val controller by lazy {
        ReportListController(ReportDayServiceImpl(ReportList.ServiceProvider.get()), this)
    }
    private val reportsAdapter by lazy { ReportsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_list_activity)
        setSupportActionBar(toolbar)
        reportsContainer.layoutManager = LinearLayoutManager(this)
        reportsContainer.adapter = reportsAdapter
        controller.onCreate()
        fabAddReport.setOnClickListener { controller.onAddTodayReport() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.report_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_today -> controller.onToday()
            R.id.action_prev_month -> controller.onPreviousMonth()
            R.id.action_next_month -> controller.onNextMonth()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun openEditReportScreen(report: RegularHourlyReport) {
        ReportEditRegularActivity.startForResult(this, report, EDIT_REPORT_SCREEN_REQUEST_CODE)
    }

    override fun openPaidVacationEditReportScreen(report: PaidVacationHourlyReport) {
        ReportEditPaidVacationActivity.startForResult(this, report, EDIT_REPORT_SCREEN_REQUEST_CODE)
    }

    override fun openDailyEditReportScreen(report: DailyReport) {
        ReportEditDailyActivity.startForResult(this, report, EDIT_REPORT_SCREEN_REQUEST_CODE)
    }

    override fun scrollToToday() {
        reportsContainer.smoothScrollToPosition(31)
    }

    override fun showMonthName(monthName: String) {
        supportActionBar?.title = monthName
    }

    override fun openAddReportScreen(date: String) {
        ReportAddActivity.startForResult(this, date, ADD_REPORT_SCREEN_REQUEST_CODE)
    }

    override fun openAddReportScreen() {
        ReportAddActivity.startForResult(this, ADD_REPORT_SCREEN_REQUEST_CODE)
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
        reportsAdapter.updateAdapter(adapterList)
    }

    private fun createContentItemsAdapters(days: List<Day>, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener): List<StableItemAdapter<out RecyclerView.ViewHolder>> {
        val itemAdapters = days.flatMap {
            createDayAdapter(it, onDayClickListener, onReportClickListener)
        }
        return itemAdapters
    }

    private fun createDayAdapter(day: Day, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener) =
            when (day) {
                is DayWithoutReports -> createDayWithoutReportsItemAdapter(day, onDayClickListener)
                is DayWithHourlyReports -> createDayWithHoursReportsItemAdapters(day, onDayClickListener, onReportClickListener)
                is DayWithDailyReport -> createDayWithDailyReportsItemAdapter(day, onReportClickListener)
                else -> throw IllegalArgumentException()
            }

    private fun createDayWithDailyReportsItemAdapter(day: DayWithDailyReport, onReportClickListener: OnReportClickListener) = listOf(DayWithDailyReportsItemAdapter(day, onReportClickListener))

    private fun createDayWithoutReportsItemAdapter(day: DayWithoutReports, onDayClickListener: OnDayClickListener): List<StableItemAdapter<out RecyclerView.ViewHolder>> {
        return if (day.isWeekend) {
            listOf(WeekendDayItem(day, onDayClickListener))
        } else {
            listOf(DayNotFilledInItemAdapter(day, onDayClickListener))
        }
    }

    private fun createDayWithHoursReportsItemAdapters(it: DayWithHourlyReports, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener) =
            listOf(DayItemAdapter(it, onDayClickListener)) + it.reports.map { createReportItemAdapter(it, onReportClickListener) }

    private fun createReportItemAdapter(report: HourlyReport, onReportClickListener: OnReportClickListener): StableItemAdapter<out RecyclerView.ViewHolder> {
        return if (report is RegularHourlyReport) {
            RegularReportItemAdapter(report, onReportClickListener)
        } else {
            PaidVacationReportItemAdapter(report as PaidVacationHourlyReport, onReportClickListener)
        }
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

