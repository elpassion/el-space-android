package pl.elpassion.elspace.hub.report.list

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.Callback.DISMISS_EVENT_ACTION
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.crashlytics.android.Crashlytics
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithConstructors
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.jakewharton.rxbinding2.support.design.widget.dismisses
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.report_list_activity.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.*
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.report.PaidVacationHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.add.ReportAddActivity
import pl.elpassion.elspace.hub.report.edit.ReportEditActivity
import pl.elpassion.elspace.hub.report.list.adapter.Empty
import pl.elpassion.elspace.hub.report.list.adapter.Separator
import pl.elpassion.elspace.hub.report.list.adapter.holders.*
import pl.elpassion.elspace.hub.report.list.service.DayFilterImpl
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersServiceImpl

class ReportListActivity : AppCompatActivity(), ReportList.View, ReportList.Actions {

    private val controller by lazy {
        ReportListController(
                reportDayService = ReportsListAdaptersServiceImpl(
                        reportListService = ReportList.ServiceProvider.get(),
                        currentTime = { getTimeFrom(CurrentTimeProvider.get()) }),
                dayFilter = DayFilterImpl(),
                actions = this,
                view = this,
                schedulers = SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    private var adapterItems = mutableListOf<AdapterItem>()
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
        reportsContainer.adapter = basicAdapterWithConstructors(adapterItems, this::createHoldersForItem)
        controller.onCreate()
    }

    private fun createHoldersForItem(itemPosition: Int): Pair<Int, (itemView: View) -> ViewHolderBinder<AdapterItem>> {
        val item = adapterItems[itemPosition]
        return when (item) {
            is DayWithHourlyReports -> DayItemViewHolder.create(controller::onDayClick)
            is DayWithDailyReport -> DayWithDailyReportsItemViewHolder.create(controller::onReportClick)
            is DayWithoutReports -> createDayWithoutReportsHolder(item)
            is RegularHourlyReport -> RegularReportItemViewHolder.create(controller::onReportClick)
            is PaidVacationHourlyReport -> PaidVacationReportItemViewHolder.create(controller::onReportClick)
            is Separator -> SeparatorItemViewHolder.create()
            is Empty -> EmptyItemViewHolder.create()
            else -> throw IllegalArgumentException()
        }
    }

    private fun createDayWithoutReportsHolder(day: DayWithoutReports) = when {
        day.isWeekend -> WeekendDayItemViewHolder.create(controller::onDayClick)
        else -> DayNotFilledInItemViewHolder.create(controller::onDayClick)
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

    override fun showDays(items: List<AdapterItem>) {
        adapterItems.clear()
        adapterItems.addAll(items)
        reportsContainer.adapter.notifyDataSetChanged()
        controller.updateLastPassedDayPosition(adapterItems.indexOfLast { it is Day && it.hasPassed })
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