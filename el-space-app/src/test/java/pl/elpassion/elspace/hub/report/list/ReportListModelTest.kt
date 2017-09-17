package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.kotlintest.matchers.shouldBe
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import pl.elpassion.elspace.common.TreeSpec
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.report.list.adapter.Empty
import pl.elpassion.elspace.hub.report.list.service.ReportsListAdaptersService
import java.util.*

class ReportListModelTest : TreeSpec() {

    private val reportListAdaptersSubject = PublishSubject.create<List<AdapterItem>>()
    private val service = mock<ReportsListAdaptersService>().apply {
        whenever(createReportsListAdapters(any())).thenReturn(reportListAdaptersSubject)
    }
    private val model = ReportListModel(service)

    init {
        "Model should " {
            "start with predefined ui state" > {
                model.states
                        .test()
                        .assertOnFirstElement { it shouldBe ReportListModel.startState }
            }
            "on create " {
                before { model.events.accept(ReportList.Event.OnCreate) }
                "propagate list of adapters returned from service" > {
                    val reportListAdapters = listOf(Empty(), Empty())
                    reportListAdaptersSubject.onNext(reportListAdapters)
                    model.states.test()
                            .assertOnFirstElement { it.adapterItems shouldBe reportListAdapters }
                }
                "call service for report list adapters" > {
                    verify(service).createReportsListAdapters(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth())
                }
                "show loader" > {
                    model.states
                            .test()
                            .assertOnFirstElement {
                                it.isLoaderVisible shouldBe true
                            }
                }
            }
        }
    }
}

private fun <T> TestObserver<T>.assertOnFirstElement(assertion: (T) -> Unit) {
    this.values().first().run(assertion)
}

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