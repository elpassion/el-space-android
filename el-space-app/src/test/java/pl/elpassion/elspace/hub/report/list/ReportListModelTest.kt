package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
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
                model.states.test()
                        .assertValue { it == ReportListModel.startState }
            }
            "on create " {
                model.events.accept(ReportList.Event.OnCreate)
                "propagate list of adapters returned from service" > {
                    val reportListAdapters = listOf(Empty(), Empty())
                    reportListAdaptersSubject.onNext(reportListAdapters)
                    model.states.test().assertValue { it.adapterItems == reportListAdapters }
                }
                "call service for report list adapters" > {
                    verify(service).createReportsListAdapters(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth())
                }
                "show loader" > {
                    reportListAdaptersSubject.onNext(emptyList())
                    model.states.test().assertValue { it.isLoaderVisible }
                }
            }
        }
    }
}

class ReportListModel(service: ReportsListAdaptersService) {
    val states: Relay<ReportList.UIState> = BehaviorRelay.create<ReportList.UIState>().apply {
        accept(startState)
    }
    val events: PublishRelay<ReportList.Event> = PublishRelay.create()

    init {
        events.flatMap { service.createReportsListAdapters(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth()) }
                .map { ReportList.UIState(adapterItems = it, isLoaderVisible = true) }
                .subscribe(states)
    }
    companion object {
        val startState = ReportList.UIState(emptyList(), false)
    }
}