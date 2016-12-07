package pl.elpassion.report.list

import pl.elpassion.common.extensions.changeToNextMonth
import pl.elpassion.common.extensions.changeToPreviousMonth
import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.*

class DateChangeObserver(initialDateCalendar: Calendar) {
    private val date: Calendar = initialDateCalendar.clone() as Calendar
    private val dateSubject = BehaviorSubject.create(date.toYearMonth())

    fun observe(): Observable<YearMonth> = dateSubject.asObservable()

    fun setNextMonth() {
        date.changeToNextMonth()
        notifyObservers()
    }

    fun setPreviousMonth() {
        date.changeToPreviousMonth()
        notifyObservers()
    }

    private fun notifyObservers() {
        dateSubject.onNext(date.toYearMonth())
    }
}
