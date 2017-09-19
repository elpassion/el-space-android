package pl.elpassion.elspace.hub.report.list

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable
import pl.elpassion.elspace.common.extensions.changeToNextMonth
import pl.elpassion.elspace.common.extensions.changeToPreviousMonth
import pl.elpassion.elspace.common.extensions.changeToYearMonth
import pl.elpassion.elspace.common.extensions.mapToWithLastFrom
import java.util.*

class ReportListDateModel(date: Calendar) {
    val dates: Relay<YearMonth> = BehaviorRelay.createDefault<YearMonth>(date.toYearMonth())

    val events: Relay<ReportList.Date.Event> = PublishRelay.create()

    private val onNextMonthHandler = events.ofType(ReportList.Date.Event.OnNextMonth::class.java).mapToWithLastFrom(dates) {
        it.toCalendar().run {
            changeToNextMonth()
            toYearMonth()
        }
    }

    private val onPreviousMonthHandler = events.ofType(ReportList.Date.Event.OnPreviousMonth::class.java).mapToWithLastFrom(dates) {
        it.toCalendar().run {
            changeToPreviousMonth()
            toYearMonth()
        }
    }

    private val onChangeDateHandler = events.ofType(ReportList.Date.Event.OnChangeToDate::class.java).mapToWithLastFrom(dates) { prevDate ->
        prevDate.toCalendar().let { prevDateCalendar ->
            prevDateCalendar.changeToYearMonth(this.calendar)
            prevDateCalendar.toYearMonth()
        }
    }

    init {
        Observable.merge(
                onNextMonthHandler,
                onPreviousMonthHandler,
                onChangeDateHandler)
                .subscribe(dates)
    }

    val lastDate: YearMonth get() = dates.blockingFirst()
}