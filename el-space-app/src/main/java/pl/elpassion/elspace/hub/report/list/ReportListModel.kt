package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersService
import java.util.*

class ReportListModel(service: ReportsListAdaptersService) {
    val states: Relay<ReportList.UIState> = BehaviorRelay.create<ReportList.UIState>().apply {
        accept(startState)
    }
    val events: PublishRelay<ReportList.Event> = PublishRelay.create()

    init {
        Observable.merge(
                handleFetchingReportListAdapters(service),
                handleShowingLoader())
                .subscribe(states)
    }

    private fun handleFetchingReportListAdapters(service: ReportsListAdaptersService) =
            events.ofType(ReportList.Event.OnCreate::class.java)
                    .switchMap { service.createReportsListAdapters(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth()) }
                    .withLatestFrom(states, BiFunction<List<AdapterItem>, ReportList.UIState, ReportList.UIState> { t1, t2 -> t2.copy(adapterItems = t1, isLoaderVisible = false) })

    private fun handleShowingLoader(): Observable<ReportList.UIState> =
            events.ofType(ReportList.Event.OnCreate::class.java).withLatestFrom(states, BiFunction { _, t2 -> t2.copy(isLoaderVisible = true) })

    companion object {
        val startState = ReportList.UIState(emptyList(), false)
    }
}