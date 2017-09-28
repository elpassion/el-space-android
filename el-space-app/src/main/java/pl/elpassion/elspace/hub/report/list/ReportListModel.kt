package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Observable.just
import pl.elpassion.elspace.common.extensions.andThen
import pl.elpassion.elspace.common.extensions.mapToLastFrom
import pl.elpassion.elspace.common.extensions.mapToWithLastFrom
import pl.elpassion.elspace.hub.report.list.service.DayFilterImpl
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersService
import java.util.*

class ReportListModel(private val service: ReportsListAdaptersService, getCurrentDay: () -> Calendar) {

    private val itemAdapterFilter = DayFilterImpl()

    val states: Relay<ReportList.UIState> = BehaviorRelay.create<ReportList.UIState>().apply {
        accept(getGetStartState(getCurrentDay()))
    }

    val events: PublishRelay<ReportList.Event> = PublishRelay.create()

    private val handleOnCreateEvent = events.ofType(ReportList.Event.OnCreate::class.java)
            .mapToLastFrom(states)
            .map { it.copy(isLoaderVisible = true) }

    private val handleOnNextMonth = events.ofType(ReportList.Event.OnNextMonth::class.java)
            .mapToLastFrom(states)
            .map { it.copy(isLoaderVisible = true, yearMonth = it.yearMonth.changeToNextMonth()) }

    private val handleOnPreviousMonth = events.ofType(ReportList.Event.OnPreviousMonth::class.java)
            .mapToLastFrom(states)
            .map { it.copy(isLoaderVisible = true, yearMonth = it.yearMonth.changeToPreviousMonth()) }

    private val handleChangeToCurrentDay = events.ofType(ReportList.Event.OnChangeToCurrentDay::class.java)
            .mapToLastFrom(states)
            .map { it.copy(yearMonth = getCurrentDay().toYearMonth(), isLoaderVisible = true) }

    private val handleOnFilterEvent: Observable<ReportList.UIState> = events.ofType(ReportList.Event.OnFilter::class.java)
            .mapToLastFrom(states)
            .map { it.copy(isFilterEnabled = it.isFilterEnabled.not()) }
            .mapAdapterItemsToShow()

    private fun callServiceForAdapterItems(yearMonth: YearMonth) = service.createReportsListAdapters(yearMonth)
            .mapToWithLastFrom(states) { state -> state.copy(adapterItems = this, isLoaderVisible = false) }
            .mapAdapterItemsToShow()


    private fun Observable<ReportList.UIState>.mapAdapterItemsToShow() = map { state ->
        if (state.isFilterEnabled) {
            state.copy(adapterItemsToShow = itemAdapterFilter.fetchFilteredDays(state.adapterItems))
        } else {
            state.copy(adapterItemsToShow = state.adapterItems)
        }
    }

    init {
        Observable.merge(
                Observable.merge(
                        handleOnCreateEvent,
                        handleOnNextMonth,
                        handleOnPreviousMonth,
                        handleChangeToCurrentDay)
                        .switchMap { state ->
                            just(state) andThen callServiceForAdapterItems(state.yearMonth)
                        }, handleOnFilterEvent)
                .subscribe(states)
    }

    companion object {
        fun getGetStartState(calendar: Calendar) = ReportList.UIState(emptyList(), emptyList(), calendar.toYearMonth(), false, false)
    }
}

private fun YearMonth.changeToPreviousMonth() = toCalendar().apply { add(Calendar.MONTH, -1) }.toYearMonth()
private fun YearMonth.changeToNextMonth() = toCalendar().apply { add(Calendar.MONTH, 1) }.toYearMonth()
