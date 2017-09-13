package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import pl.elpassion.elspace.common.TreeSpec

class ReportListModelTest : TreeSpec() {

    init {
        "Model should " {
            "on create " {
                "propagate list of adapters" > {
                    val model = ReportListModel()
                    Observable.just(ReportList.Event.OnCreate).subscribe(model.events)
                    model.states
                            .test()
                            .assertValue { it.adapterItems.isEmpty() }
                }
            }
        }
    }
}

class ReportListModel {
    val states: BehaviorRelay<ReportList.UIState> = BehaviorRelay.create()
    val events: PublishRelay<ReportList.Event> = PublishRelay.create()

    init {
        events
                .map { ReportList.UIState(emptyList()) }
                .subscribe(states)
    }
}