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
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.elpassion.android.commons.recycler.adapters.stableRecyclerViewAdapter
import com.elpassion.android.commons.recycler.components.base.MutableListItemsStrategy
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import com.jakewharton.rxbinding2.support.design.widget.dismisses
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.*
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.report.HourlyReport
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.add.ReportAddActivity
import pl.elpassion.elspace.hub.report.edit.ReportEditActivity
import pl.elpassion.elspace.hub.report.list.adapter.items.*
import pl.elpassion.elspace.hub.report.list.service.DayFilterImpl
import pl.elpassion.elspace.hub.report.list.service.ReportDayServiceImpl

class ReportListActivity : AppCompatActivity(), ReportList.View, ReportList.Actions {

    private val controller by lazy {
        ReportListController(
                reportDayService = ReportDayServiceImpl(ReportList.ServiceProvider.get()),
                dayFilter = DayFilterImpl(),
                actions = this,
                view = this,
                schedulers = SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
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
        showBackArrowOnActionBar()
        reportsContainer.layoutManager = ReportsLinearLayoutManager(this)
        reportsContainer.adapter = reportsAdapter
        controller.onCreate()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.report_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = handleClickOnBackArrowItem(item)

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

    override fun resultRefresh(): Observable<Unit> = reportScreenResult

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

    override fun openEditReportScreen(report: Report) {
        ReportEditActivity.startForResult(this, REPORT_SCREEN_CHANGES_REQUEST_CODE, report)
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

    override fun showDays(days: List<Day>, onDayClick: OnDayClick, onReportClick: OnReportClick) {
        val contentItemAdapters = createContentItemsAdapters(days, onDayClick, onReportClick)
        val adapterList = listOf<StableItemAdapter<*>>(EmptyItemAdapter()) + contentItemAdapters + EmptyItemAdapter()
        itemsStrategy.set(adapterList)
        reportsAdapter.notifyDataSetChanged()
        controller.updateLastPassedDayPosition(adapterList.indexOfLast { it is DayItem && it.day.hasPassed })
    }

    private fun createContentItemsAdapters(days: List<Day>, onDayClick: OnDayClick, onReportClick: OnReportClick) =
            days.flatMap {
                createDayAdapter(it, onDayClick, onReportClick)
            }

    private fun createDayAdapter(day: Day, onDayClick: OnDayClick, onReportClick: OnReportClick) =
            when (day) {
                is DayWithoutReports -> createDayWithoutReportsItemAdapter(day, onDayClick)
                is DayWithHourlyReports -> createDayWithHoursReportsItemAdapters(day, onDayClick, onReportClick)
                is DayWithDailyReport -> createDayWithDailyReportsItemAdapter(day, onReportClick)
            }

    private fun createDayWithDailyReportsItemAdapter(day: DayWithDailyReport, onReportClick: OnReportClick) = listOf(DayWithDailyReportsItemAdapter(day, onReportClick))

    private fun createDayWithoutReportsItemAdapter(day: DayWithoutReports, onDayClick: OnDayClick): List<StableItemAdapter<out RecyclerView.ViewHolder>> =
            if (day.isWeekend) {
                listOf(WeekendDayItem(day, onDayClick))
            } else {
                listOf(DayNotFilledInItemAdapter(day, onDayClick))
            }

    private fun createDayWithHoursReportsItemAdapters(it: DayWithHourlyReports, onDayClick: OnDayClick, onReportClick: OnReportClick) =
            listOf(DayItemAdapter(it, onDayClick)) + it.reports.map { createReportItemAdapter(it, onReportClick) }

    private fun createReportItemAdapter(report: HourlyReport, onReportClick: OnReportClick): StableItemAdapter<out RecyclerView.ViewHolder> =
            if (report is RegularHourlyReport) {
                RegularReportItemAdapter(report, onReportClick)
            } else {
                PaidVacationReportItemAdapter(report as PaidVacationHourlyReport, onReportClick)
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