package pl.elpassion.elspace.hub.report.list.service

import pl.elpassion.elspace.hub.report.list.Day
import pl.elpassion.elspace.hub.report.list.YearMonth
import io.reactivex.Observable

interface ReportDayService {
    fun createDays(dateChangeObservable: Observable<YearMonth>): Observable<List<Day>>
}