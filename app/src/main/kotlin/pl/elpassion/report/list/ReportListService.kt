package pl.elpassion.report.list

import rx.Observable

class ReportListService :ReportList.Service{
    override fun getReports(): Observable<List<Report>> {
        throw NotImplementedError()
    }
}