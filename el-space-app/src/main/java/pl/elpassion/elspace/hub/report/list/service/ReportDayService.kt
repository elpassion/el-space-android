package pl.elpassion.elspace.hub.report.list.service

import io.reactivex.Observable
import pl.elpassion.elspace.hub.report.list.Day
import pl.elpassion.elspace.hub.report.list.YearMonth

interface ReportDayService {
    fun createDays(yearMonth: YearMonth): Observable<List<Day>>
}