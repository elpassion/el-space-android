package pl.elpassion.elspace.hub.report.edit

import pl.elpassion.elspace.common.extensions.addTo
import pl.elpassion.elspace.hub.report.Report
import rx.subscriptions.CompositeSubscription

class ReportEditController(private val report: Report,
                           private val view: ReportEdit.View) {

    private val subscriptions = CompositeSubscription()

    fun onCreate() {
        view.showDate(report.date)
        view.reportTypeChanges()
                .doOnNext { view.showRegularForm() }
                .subscribe()
                .addTo(subscriptions)
    }
}