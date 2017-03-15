package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.common.extensions.addTo
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.add.ReportType
import rx.subscriptions.CompositeSubscription

class ReportEditController(private val report: Report,
                           private val view: ReportEdit.View) {

    private val subscriptions = CompositeSubscription()

    fun onCreate() {
        view.showDate(report.date)
        view.reportTypeChanges()
                .doOnNext { onReportTypeChanged(it) }
                .subscribe()
                .addTo(subscriptions)
    }

    private fun onReportTypeChanged(reportType: ReportType) =
            if (reportType == ReportType.REGULAR) {
                view.showRegularForm()
            } else {
                view.showPaidVacationsForm()
            }
}