package pl.elpassion.elspace.hub.report.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.Callback.DISMISS_EVENT_ACTION
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.Menu
import com.crashlytics.android.Crashlytics
import com.elpassion.android.commons.recycler.adapters.stableRecyclerViewAdapter
import com.elpassion.android.commons.recycler.components.base.MutableListItemsStrategy
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import com.jakewharton.rxbinding.support.design.widget.dismisses
import com.jakewharton.rxbinding.support.v4.widget.refreshes
import com.jakewharton.rxbinding.view.clicks
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.extensions.menuClicks
import pl.elpassion.elspace.common.extensions.onMenuItemAction
import pl.elpassion.elspace.common.extensions.onMenuItemClicks
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.report.DailyReport
import pl.elpassion.elspace.hub.report.HourlyReport
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.add.ReportAddActivity
import pl.elpassion.elspace.hub.report.edit.daily.ReportEditDailyActivity
import pl.elpassion.elspace.hub.report.edit.paidvacation.ReportEditPaidVacationActivity
import pl.elpassion.elspace.hub.report.edit.regular.ReportEditRegularActivity
import pl.elpassion.elspace.hub.report.list.adapter.items.*
import pl.elpassion.elspace.hub.report.list.service.DayFilterImpl
import pl.elpassion.elspace.hub.report.list.service.ReportDayServiceImpl
import rx.Observable
import rx.subjects.PublishSubject

class ReportListActivity : AppCompatActivity(), ReportList.View, ReportList.Actions {

    private val controller by lazy {
        ReportListController(ReportDayServiceImpl(ReportList.ServiceProvider.get()), DayFilterImpl(), this, this)
    }

    private val itemsStrategy = MutableListItemsStrategy<StableItemAdapter<*>>()
    private val reportsAdapter by lazy { stableRecyclerViewAdapter(itemsStrategy) }
    private val toolbarClicks by lazy { toolbar.menuClicks() }
    private val reportScreenResult: PublishSubject<Unit> = PublishSubject.create()
    private val errorSnackBar by lazy {
        Snackbar.make(reportListCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.refresh_action, {})
    }

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

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }

    override fun refreshingEvents() = reportSwipeToRefresh.refreshes()

    override fun reportAdd(): Observable<Unit> = fabAddReport.clicks()

    override fun snackBarRetry(): Observable<Unit> = errorSnackBar.dismisses()
            .filter { it == DISMISS_EVENT_ACTION }
            .map { Unit }

    override fun monthChangeToNext(): Observable<Unit> = toolbarClicks.onMenuItemClicks(R.id.action_next_month)

    override fun monthChangeToPrev(): Observable<Unit> = toolbarClicks.onMenuItemClicks(R.id.action_prev_month)

    override fun scrollToCurrent(): Observable<Unit> = toolbarClicks.onMenuItemClicks(R.id.action_today)

    override fun resultRefresh(): Observable<Unit> = reportScreenResult.asObservable()

    override fun reportsFilter(): Observable<Boolean> = toolbarClicks.onMenuItemAction(R.id.action_filter)
            .doOnNext {
                it.isChecked = !it.isChecked
                val icon = when (it.isChecked) {
                    true -> R.drawable.filter_on
                    else -> R.drawable.filter_off
                }
                it.setIcon(icon)
            }
            .map { it.isChecked }
            .startWith(false)

    override fun openEditReportScreen(report: RegularHourlyReport) {
        ReportEditRegularActivity.startForResult(this, report, REPORT_SCREEN_CHANGES_REQUEST_CODE)
    }

    override fun openPaidVacationEditReportScreen(report: PaidVacationHourlyReport) {
        ReportEditPaidVacationActivity.startForResult(this, report, REPORT_SCREEN_CHANGES_REQUEST_CODE)
    }

    override fun openDailyEditReportScreen(report: DailyReport) {
        ReportEditDailyActivity.startForResult(this, report, REPORT_SCREEN_CHANGES_REQUEST_CODE)
    }

    override fun scrollToPosition(position: Int) {
        appBarLayout.setExpanded(false, true)
        reportsContainer.smoothScrollToPosition(position)
    }

    override fun showMonthName(monthName: String) {
        supportActionBar?.title = monthName
    }

    override fun openAddReportScreen(date: String) {
        ReportAddActivity.startForResult(this, date, REPORT_SCREEN_CHANGES_REQUEST_CODE)
    }

    override fun openAddReportScreen() {
        ReportAddActivity.startForResult(this, REPORT_SCREEN_CHANGES_REQUEST_CODE)
    }

    override fun hideLoader() {
        reportSwipeToRefresh.isRefreshing = false
        hideLoader(reportListCoordinator)
    }

    override fun showLoader() {
        showLoader(reportListCoordinator)
    }

    override fun isDuringPullToRefresh() = reportSwipeToRefresh.isRefreshing

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        errorSnackBar.show()
    }

    override fun showDays(days: List<Day>, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener) {
        val contentItemAdapters = createContentItemsAdapters(days, onDayClickListener, onReportClickListener)
        val adapterList = listOf<StableItemAdapter<*>>(EmptyItemAdapter()) + contentItemAdapters + EmptyItemAdapter()
        itemsStrategy.set(adapterList)
        reportsAdapter.notifyDataSetChanged()
        controller.updateTodayPosition(adapterList.indexOfLast { it is DayItem && it.day.hasPassed })
    }

    private fun createContentItemsAdapters(days: List<Day>, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener) =
            days.flatMap {
                createDayAdapter(it, onDayClickListener, onReportClickListener)
            }

    private fun createDayAdapter(day: Day, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener) =
            when (day) {
                is DayWithoutReports -> createDayWithoutReportsItemAdapter(day, onDayClickListener)
                is DayWithHourlyReports -> createDayWithHoursReportsItemAdapters(day, onDayClickListener, onReportClickListener)
                is DayWithDailyReport -> createDayWithDailyReportsItemAdapter(day, onReportClickListener)
                else -> throw IllegalArgumentException()
            }

    private fun createDayWithDailyReportsItemAdapter(day: DayWithDailyReport, onReportClickListener: OnReportClickListener) = listOf(DayWithDailyReportsItemAdapter(day, onReportClickListener))

    private fun createDayWithoutReportsItemAdapter(day: DayWithoutReports, onDayClickListener: OnDayClickListener): List<StableItemAdapter<out RecyclerView.ViewHolder>> =
            if (day.isWeekend) {
                listOf(WeekendDayItem(day, onDayClickListener))
            } else {
                listOf(DayNotFilledInItemAdapter(day, onDayClickListener))
            }

    private fun createDayWithHoursReportsItemAdapters(it: DayWithHourlyReports, onDayClickListener: OnDayClickListener, onReportClickListener: OnReportClickListener) =
            listOf(DayItemAdapter(it, onDayClickListener)) + it.reports.map { createReportItemAdapter(it, onReportClickListener) }

    private fun createReportItemAdapter(report: HourlyReport, onReportClickListener: OnReportClickListener): StableItemAdapter<out RecyclerView.ViewHolder> =
            if (report is RegularHourlyReport) {
                RegularReportItemAdapter(report, onReportClickListener)
            } else {
                PaidVacationReportItemAdapter(report as PaidVacationHourlyReport, onReportClickListener)
            }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REPORT_SCREEN_CHANGES_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            reportScreenResult.onNext(Unit)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private val REPORT_SCREEN_CHANGES_REQUEST_CODE = 100
        fun start(context: Context) {
            context.startActivity(Intent(context, ReportListActivity::class.java))
        }
    }
}