package pl.elpassion.report.list.service

import pl.elpassion.report.list.RegularDay
import pl.elpassion.report.list.YearMonth
import rx.Observable

interface ReportDayService {
    fun createDays(dateChangeObservable: Observable<YearMonth>): Observable<List<RegularDay>>
}