package pl.elpassion.report.list.service

import pl.elpassion.common.extensions.changeToNextMonth
import pl.elpassion.common.extensions.changeToPreviousMonth
import pl.elpassion.report.list.YearMonth
import pl.elpassion.report.list.toYearMonth
import rx.Observable
import rx.subjects.BehaviorSubject
import java.util.*

class DateChangeObserverImpl(initialDateCalendar: Calendar) : DateChangeObserver {
    private val date: Calendar = initialDateCalendar.clone() as Calendar
    private val dateSubject = BehaviorSubject.create(date.toYearMonth())

    override fun observe(): Observable<YearMonth> = dateSubject.asObservable()

    override fun setNextMonth() {
        date.changeToNextMonth()
        notifyObservers()
    }

    override fun setPreviousMonth() {
        date.changeToPreviousMonth()
        notifyObservers()
    }

    private fun notifyObservers() {
        dateSubject.onNext(date.toYearMonth())
    }
}
