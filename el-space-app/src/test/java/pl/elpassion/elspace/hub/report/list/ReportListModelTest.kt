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

    init {
        "Model should " {
            "on create " {
                "propagate list of adapters" > {
                    val service = mock<ReportDayService>().apply {
                        whenever(this.createDays(any())).thenReturn(Observable.just(emptyList()))
                    }
                    val model = ReportListModel(service)
                    Observable.just(ReportList.Event.OnCreate).subscribe(model.events)
                    model.states
                            .test()
                            .assertValue { it.adapterItems.isEmpty() }
                }

                "call service for days" > {
                    val service = mock<ReportDayService>().apply {
                        whenever(this.createDays(any())).thenReturn(Observable.just(emptyList()))
                    }
                    val model = ReportListModel(service)
                    model.events.accept(ReportList.Event.OnCreate)
                    verify(service).createDays(yearMonth = getTimeFrom(year = 2016, month = Calendar.JUNE, day = 1).toYearMonth())
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
                .map { ReportList.UIState(emptyList()) }
                .subscribe(states)
    }
}