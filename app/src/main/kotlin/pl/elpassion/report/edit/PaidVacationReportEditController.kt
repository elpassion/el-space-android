package pl.elpassion.report.edit

import pl.elpassion.api.applySchedulers
import pl.elpassion.common.extensions.getPerformedAtString
import pl.elpassion.report.PaidVacationHourlyReport
import rx.Subscription
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class PaidVacationReportEditController(private val view: ReportEdit.PaidVacation.View,
                                       private val api: ReportEdit.PaidVacation.Service,
                                       private val removeReportApi: ReportEdit.RemoveApi) {

    private var report: PaidVacationHourlyReport by Delegates.notNull()
    private var subscription: Subscription? = null
    private var removeReportSubscription: Subscription? = null

    fun onCreate(report: PaidVacationHourlyReport) {
        this.report = report
        view.showReport(report)
        val performedDate = getPerformedAtString(report.year, report.month, report.day)
        view.showDate(performedDate)
    }

    fun onSaveReport(hours: String) {
        sendEditedReport(hours)
    }

    private fun sendEditedReport(hours: String) {
        subscription = api.edit(report.copy(reportedHours = hours.toDouble()))
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
    }

    fun onRemoveReport() {
        removeReportSubscription = removeReportApi.removeReport(report.id)
                .applySchedulers()
                .doOnSubscribe { view.showLoader() }
                .doOnUnsubscribe { view.hideLoader() }
                .subscribe({
                    view.close()
                }, {
                    view.showError(it)
                })
    }

    fun onDestroy() {
        subscription?.unsubscribe()
        removeReportSubscription?.unsubscribe()
    }

    fun onDateSelect(performedDate: String) {
        val date = SimpleDateFormat("yyyy-MM-dd").parse(performedDate)
        val calendar = Calendar.getInstance().apply { time = date }
        report = report.copy(day = calendar.get(Calendar.DAY_OF_WEEK), month = calendar.get(Calendar.MONTH) + 1, year = calendar.get(Calendar.YEAR))
        view.showDate(performedDate)
    }
}