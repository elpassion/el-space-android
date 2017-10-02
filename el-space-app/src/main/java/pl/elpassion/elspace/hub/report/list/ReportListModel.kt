package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import io.reactivex.Observable.just
import io.reactivex.disposables.Disposable
import pl.elpassion.elspace.common.extensions.andThen
import pl.elpassion.elspace.common.extensions.mapToLastFrom
import pl.elpassion.elspace.common.extensions.mapToWithLastFrom
import pl.elpassion.elspace.hub.report.list.service.DayFilterImpl
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersService
import java.util.*

class ReportListModel(private val service: ReportsListAdaptersService, getCurrentDay: () -> Calendar) {

    private val itemAdapterFilter = DayFilterImpl()
    private var disposable: Disposable? = null

    val states: Relay<ReportList.UIState> = BehaviorRelay.create<ReportList.UIState>().apply {
        accept(getGetStartState(getCurrentDay()))
    }

    val events: PublishRelay<ReportList.Event> = PublishRelay.create()

    private val handleBasicServiceCallEvents = events
            .filter { it is ReportList.Event.OnCreate || it is ReportList.Event.OnRefresh }
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
            .map { state ->
                val currentYearMonth = getCurrentDay().toYearMonth()
                if (state.yearMonth != currentYearMonth) {
                    state.copy(yearMonth = currentYearMonth, scrollToCurrentDayAction = ReportList.ScrollToCurrentDayAction.PENDING, isLoaderVisible = true)
                } else {
                    state.copy(scrollToCurrentDayAction = ReportList.ScrollToCurrentDayAction.SCROLL)
                }
            }

    private val handleServiceCalls = Observable.merge(handleBasicServiceCallEvents, handleOnNextMonth, handleOnPreviousMonth, handleChangeToCurrentDay)
            .switchMap { state ->
                just(state) andThen
                        if (state.scrollToCurrentDayAction != ReportList.ScrollToCurrentDayAction.SCROLL) {
                            callServiceForAdapterItems(state.yearMonth)
                        } else {
                            just(state.copy(isLoaderVisible = false))
                        }
            }

    private fun callServiceForAdapterItems(yearMonth: YearMonth) = service.createReportsListAdapters(yearMonth)
            .map<GettingReportsListAdapters>(GettingReportsListAdapters::Success)
            .onErrorReturn(GettingReportsListAdapters::Fail)
            .mapToWithLastFrom(states) { state ->
                state.copy(adapterItems = this.adapterItems, isLoaderVisible = false, isErrorViewVisible = this is GettingReportsListAdapters.Fail)
            }
            .mapAdapterItemsToShow()

    private val handleOnFilterEvent: Observable<ReportList.UIState> = events.ofType(ReportList.Event.OnFilter::class.java)
            .mapToLastFrom(states)
            .map { it.copy(isFilterEnabled = it.isFilterEnabled.not()) }
            .mapAdapterItemsToShow()

    private val handleOnScrollEnded = events.ofType(ReportList.Event.OnScrollEnded::class.java)
            .mapToLastFrom(states)
            .map { it.copy(scrollToCurrentDayAction = ReportList.ScrollToCurrentDayAction.NOT_SCROLL) }

    private fun Observable<ReportList.UIState>.mapAdapterItemsToShow() = map { state ->
        if (state.isFilterEnabled) {
            state.copy(adapterItemsToShow = itemAdapterFilter.fetchFilteredDays(state.adapterItems))
        } else {
            state.copy(adapterItemsToShow = state.adapterItems)
        }
    }

    init {
        disposable = Observable.merge(
                handleServiceCalls,
                handleOnFilterEvent,
                handleOnScrollEnded)
                .subscribe(states)
    }

    fun onCleared() {
        disposable?.dispose()
    }

    companion object {
        fun getGetStartState(calendar: Calendar) = ReportList.UIState(
                adapterItems = emptyList(),
                adapterItemsToShow = emptyList(),
                yearMonth = calendar.toYearMonth(),
                isFilterEnabled = false,
                isLoaderVisible = false,
                scrollToCurrentDayAction = ReportList.ScrollToCurrentDayAction.NOT_SCROLL,
                isErrorViewVisible = false)
    }
}

sealed class GettingReportsListAdapters(val adapterItems: List<AdapterItem>) {
    class Success(adapterItems: List<AdapterItem>) : GettingReportsListAdapters(adapterItems)
    data class Fail(val throwable: Throwable) : GettingReportsListAdapters(emptyList())
}

private fun YearMonth.changeToPreviousMonth() = toCalendar().apply { add(Calendar.MONTH, -1) }.toYearMonth()
private fun YearMonth.changeToNextMonth() = toCalendar().apply { add(Calendar.MONTH, 1) }.toYearMonth()
