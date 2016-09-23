package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.project.dto.newReport
import rx.Observable
import rx.observers.TestSubscriber
import java.text.SimpleDateFormat

class ReportListServiceTest {

    @Test
    fun shouldCorrectlyMapReportsYear() {
        val subscriber = TestSubscriber<List<Report>>()
        val reportApi = mock<ReportApi>()
        whenever(reportApi.getReports()).thenReturn(Observable.just(listOf(newReportFromApi(createdAt = "2012-07-15T11:56:31.919+02:00"))))
        ReportListService(reportApi).getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newReport(year = 2012)))
    }

    private fun newReportFromApi(createdAt: String): ReportFromApi {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        return ReportFromApi(simpleDateFormat.parse(createdAt))
    }

}