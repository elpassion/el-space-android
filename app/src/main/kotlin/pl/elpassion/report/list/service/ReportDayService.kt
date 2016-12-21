package pl.elpassion.report.list.service

import pl.elpassion.report.list.Day
import pl.elpassion.report.list.YearMonth
import rx.Observable

interface ReportDayService {
    fun createDays(dateChangeObservable: Observable<YearMonth>): Observable<List<Day>>
}