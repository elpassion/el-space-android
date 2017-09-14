package pl.elpassion.elspace.hub.report.list.service

import io.reactivex.Observable
import pl.elpassion.elspace.hub.report.list.AdapterItem
import pl.elpassion.elspace.hub.report.list.YearMonth

interface ReportDayService {
    fun createDays(yearMonth: YearMonth): Observable<List<AdapterItem>>
}