package pl.elpassion.report.list

import rx.Observable

interface ReportDayService {
    fun createDays(): Observable<List<Day>>

    fun observeDateChanges(): Observable<YearMonth>
}