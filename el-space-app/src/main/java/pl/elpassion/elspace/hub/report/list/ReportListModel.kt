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

    private val handleOnCreateEvent = events.ofType(ReportList.Event.OnCreate::class.java)
            .mapToLastFrom(states)
            .switchMap { state ->
                showLoader(state)
                        .concatWith(callServiceForAdapterItems)
            }

    private fun showLoader(state: ReportList.UIState) =
            Observable.just(state.copy(isLoaderVisible = true))

    private val callServiceForAdapterItems = service.createReportsListAdapters(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth())
            .mapToWithLastFrom(states) { state -> state.copy(adapterItems = this, isLoaderVisible = false) }

    init {
        handleOnCreateEvent.subscribe(states)
    }

    companion object {
        val startState = ReportList.UIState(emptyList(), false)
    }
}
