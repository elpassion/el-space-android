package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.common.extensions.mapToLastFrom
import pl.elpassion.elspace.common.extensions.mapToWithLastFrom
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersService
import java.util.*

class ReportListModel(service: ReportsListAdaptersService) {
    val states: Relay<ReportList.UIState> = BehaviorRelay.create<ReportList.UIState>().apply {
        accept(startState)
    }
    val events: PublishRelay<ReportList.Event> = PublishRelay.create()

    init {
        handleFetchingReportListAdapters(service).subscribe(states)
    }

    private fun handleFetchingReportListAdapters(service: ReportsListAdaptersService) =
            events.ofType(ReportList.Event.OnCreate::class.java)
                    .mapToLastFrom(states)
                    .switchMap { state ->
                        Observable.just(state.copy(isLoaderVisible = true))
                                .concatWith(
                                        service.createReportsListAdapters(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth())
                                                .mapToWithLastFrom(states) { state -> state.copy(adapterItems = this, isLoaderVisible = false) }
                                )

                    }

    companion object {
        val startState = ReportList.UIState(emptyList(), false)
    }
}
