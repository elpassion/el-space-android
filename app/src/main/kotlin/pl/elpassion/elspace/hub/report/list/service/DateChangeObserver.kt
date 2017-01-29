package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.common.extensions.changeToYearMonth
import pl.elpassion.elspace.common.extensions.changeToNextMonth
import pl.elpassion.elspace.common.extensions.changeToPreviousMonth
import pl.elpassion.elspace.hub.report.list.YearMonth
import pl.elpassion.elspace.hub.report.list.toYearMonth
import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.*

class DateChangeObserver(initialDateCalendar: Calendar) {
    private val date: Calendar = initialDateCalendar.clone() as Calendar
    private val dateSubject = BehaviorSubject.create(date.toYearMonth())

    val lastDate: YearMonth get() = dateSubject.toBlocking().first()

    fun observe(): Observable<YearMonth> = dateSubject.asObservable()

    fun setNextMonth() {
        date.changeToNextMonth()
        notifyObservers()
    }

    fun setPreviousMonth() {
        date.changeToPreviousMonth()
        notifyObservers()
    }

    fun setYearMonth(year: Int, month: Int) {
        date.changeToYearMonth(year, month)
        notifyObservers()
    }

    private fun notifyObservers() {
        dateSubject.onNext(date.toYearMonth())
    }
}
