package pl.elpassion.report.list.service

import pl.elpassion.report.list.Day
import pl.elpassion.report.list.YearMonth
import rx.Observable

interface ReportDayService {
    fun createDays(): Observable<List<Day>>

    fun observeDateChanges(): Observable<YearMonth>

    fun changeMonthToNext()

    fun changeMonthToPrevious()
}