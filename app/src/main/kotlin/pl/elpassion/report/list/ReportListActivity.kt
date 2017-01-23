package pl.elpassion.report.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import com.crashlytics.android.Crashlytics
import com.elpassion.android.commons.recycler.adapters.stableRecyclerViewAdapter
import com.elpassion.android.commons.recycler.components.base.MutableListItemsStrategy
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import com.jakewharton.rxbinding.view.clicks
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.*
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
import pl.elpassion.report.list.adapter.items.*
import pl.elpassion.report.list.service.DayFilterImpl
import pl.elpassion.report.list.service.ReportDayServiceImpl
import rx.Observable

class ReportListActivity : AppCompatActivity(), ReportList.View, ReportList.Actions {

    private val controller by lazy {
        ReportListController(ReportDayServiceImpl(ReportList.ServiceProvider.get()), DayFilterImpl(), this, this)
    }

    private val itemsStrategy = MutableListItemsStrategy<StableItemAdapter<*>>()
    private val reportsAdapter by lazy { stableRecyclerViewAdapter(itemsStrategy) }
    private val toolbarClicks by lazy { toolbar.menuClicks() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_list_activity)
        setSupportActionBar(toolbar)
        reportsContainer.layoutManager = ReportsLinearLayoutManager(this)
        reportsContainer.adapter = reportsAdapter
        controller.onCreate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.report_list_menu, menu)
        return true
    }

    override fun reportAdd(): Observable<Unit> = fabAddReport.clicks()

    override fun monthChangeToNext(): Observable<Unit> = toolbarClicks.onMenuItemClicks(R.id.action_next_month)

    override fun monthChangeToPrev(): Observable<Unit> = toolbarClicks.onMenuItemClicks(R.id.action_prev_month)

    override fun scrollToCurrent(): Observable<Unit> = toolbarClicks.onMenuItemClicks(R.id.action_today)

    override fun shouldFilterReports(): Observable<Boolean> {
        return Observable.just(false).concatWith(toolbarClicks.onMenuItemAction(R.id.action_filter)
                .doOnNext {
                    it.isChecked = !it.isChecked
                    val icon = when (it.isChecked) {
                        true -> R.drawable.filter_on
                        else -> R.drawable.filter_off
                    }
                    it.setIcon(icon)
                }
                .map { it.isChecked })
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

    override fun scrollToPosition(position: Int) {
        appBarLayout.setExpanded(false, true)
        reportsContainer.smoothScrollToPosition(position)
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
        val adapterList = listOf<StableItemAdapter<*>>(EmptyItemAdapter()) + contentItemAdapters + EmptyItemAdapter()
        itemsStrategy.set(adapterList)
        reportsAdapter.notifyDataSetChanged()
        val today = getCurrentTimeCalendar().dayOfMonth
        val todayPosition = adapterList.indexOfFirst {
            it is DayItem && it.day.date.drop(8) == today.toString()
        }
        controller.updateTodayPosition(todayPosition)
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

