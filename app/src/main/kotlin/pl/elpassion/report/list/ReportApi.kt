package pl.elpassion.report.list

import rx.Observable

interface ReportApi {
    fun getReports(): Observable<List<ReportFromApi>>
}

