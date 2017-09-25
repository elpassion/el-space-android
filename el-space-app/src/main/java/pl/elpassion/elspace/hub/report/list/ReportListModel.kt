package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import pl.elpassion.elspace.common.extensions.mapToLastFrom
import pl.elpassion.elspace.common.extensions.mapToWithLastFrom
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersService
import java.util.*

class ReportListModel(private val service: ReportsListAdaptersService, getCurrentDay: () -> Calendar) {

    val states: Relay<ReportList.UIState> = BehaviorRelay.create<ReportList.UIState>().apply {
        accept(getGetStartState(getCurrentDay()))
    }

    val events: PublishRelay<ReportList.Event> = PublishRelay.create()

    private val handleOnCreateEvent = events.ofType(ReportList.Event.OnCreate::class.java)
            .mapToLastFrom(states)
            .switchMap { state ->
                showLoader(state)
                        .concatWith(callServiceForAdapterItems(state.yearMonth))
            }

    private fun showLoader(state: ReportList.UIState) =
            Observable.just(state.copy(isLoaderVisible = true))

    private fun callServiceForAdapterItems(yearMonth: YearMonth) = service.createReportsListAdapters(yearMonth)
            .mapToWithLastFrom(states) { state -> state.copy(adapterItems = this, isLoaderVisible = false) }

    private val handleOnNextMonth = events.ofType(ReportList.Event.OnNextMonth::class.java)
            .mapToLastFrom(states)
            .switchMap { state ->
                showLoader(state)
                        .changeYearMonthToNextMonth()
                        .concatWith(states.switchMap { callServiceForAdapterItems(it.yearMonth) })
            }

    private val handleOnPreviousMonth = events.ofType(ReportList.Event.OnPreviousMonth::class.java)
            .mapToWithLastFrom(states) { it.copy(yearMonth = it.yearMonth.changeToPreviousMonth()) }

    private val handleChangeToCurrentDay = events.ofType(ReportList.Event.OnChangeToCurrentDay::class.java)
            .mapToWithLastFrom(states) { it.copy(yearMonth = getCurrentDay().toYearMonth()) }

    init {
        Observable.merge(
                handleOnCreateEvent,
                handleOnNextMonth,
                handleOnPreviousMonth,
                handleChangeToCurrentDay)
                .subscribe(states)
    }

    companion object {
        fun getGetStartState(calendar: Calendar) = ReportList.UIState(emptyList(), false, calendar.toYearMonth())
    }
}

private fun Observable<ReportList.UIState>.changeYearMonthToNextMonth() = map { it.copy(yearMonth = it.yearMonth.changeToNextMonth()) }
private fun YearMonth.changeToPreviousMonth() = toCalendar().apply { add(Calendar.MONTH, -1) }.toYearMonth()
private fun YearMonth.changeToNextMonth() = toCalendar().apply { add(Calendar.MONTH, 1) }.toYearMonth()
