package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import pl.elpassion.elspace.common.TreeSpec
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.report.list.service.ReportDayService
import java.util.*

class ReportListModelTest : TreeSpec() {

    private val service = mock<ReportDayService>().apply {
        whenever(createDays(any())).thenReturn(Observable.just(emptyList()))
    }
    private val model = ReportListModel(service)

    init {
        "Model should " {
            "on create " {
                model.events.accept(ReportList.Event.OnCreate)
                "propagate list of adapters" > {
                    model.states.test().assertValue { it.adapterItems.isEmpty() }
                }
                "call service for days" > {
                    verify(service).createDays(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth())
                }
                "show loader" > {
                    model.states.test().assertValue { it.isLoaderVisible }
                }
            }
        }
    }
}

class ReportListModel(service: ReportDayService) {
    val states: BehaviorRelay<ReportList.UIState> = BehaviorRelay.create()
    val events: PublishRelay<ReportList.Event> = PublishRelay.create()

    init {
        events.flatMap { service.createDays(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth()) }
                .map { ReportList.UIState(adapterItems = emptyList(), isLoaderVisible = true) }
                .subscribe(states)
    }
}