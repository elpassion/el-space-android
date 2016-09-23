package pl.elpassion.report.list

import rx.Observable

class ReportListService(val reportApi: ReportList.ReportApi) : ReportList.Service {
    override fun getReports(): Observable<List<Report>> {
        return reportApi.getReports().map { it.map { it.toReport() } }
    }
}