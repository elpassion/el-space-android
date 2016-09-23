package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.project.common.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newReport
import rx.Observable
import rx.observers.TestSubscriber
import java.text.SimpleDateFormat

class ReportListServiceTest {

    val subscriber = TestSubscriber<List<Report>>()
    val reportApi = mock<ReportList.ReportApi>()
    val projectApi = mock<ReportList.ProjectApi>()
    val service = ReportListService(reportApi, projectApi, mock())

    @Before
    fun setUp(){
        stubProjectApiToReturn(newProject(id = "1", name = "Project"))
    }

    private fun stubProjectApiToReturn(project: Project) {
        whenever(projectApi.getProjects()).thenReturn(Observable.just(listOf(project)))
    }

    @Test
    fun shouldCorrectlyMapReportsYear() {
        stubReportApiToReturn(newReportFromApi(createdAt = "2012-06-01T11:56:31.919+02:00"))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newReport(year = 2012)))
    }

    @Test
    fun shouldCorrectlyMapReportsMonth() {
        stubReportApiToReturn(newReportFromApi(createdAt = "2016-01-01T11:56:31.919+02:00"))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newReport(month = 1)))
    }

    @Test
    fun shouldCorrectlyMapReportsDays() {
        stubReportApiToReturn(newReportFromApi(createdAt = "2016-06-21T11:56:31.919+02:00"))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newReport(day = 21)))
    }

    @Test
    fun shouldCorrectlyMapReportsValues() {
        stubReportApiToReturn(newReportFromApi(value = 9.0))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newReport(reportedHours = 9.0)))
    }

    @Test
    fun shouldCorrectlyMapReportsDescription() {
        stubReportApiToReturn(newReportFromApi(description = "1234"))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newReport(description = "1234")))
    }

    @Test
    fun shouldCorrectlyMapReportsProjectsName() {
        stubProjectApiToReturn(newProject(id = "2", name = "Test"))
        stubReportApiToReturn(newReportFromApi(projectId = 2))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newReport(projectId = 2, projectName = "Test")))
    }

    private fun stubReportApiToReturn(reportFromApi: ReportFromApi) {
        whenever(reportApi.getReports()).thenReturn(Observable.just(listOf(reportFromApi)))
    }

    private fun newReportFromApi(createdAt: String = "2016-06-01T11:56:31.919+02:00",
                                 value: Double = 4.0,
                                 projectId: Long = 1,
                                 description: String = "description"): ReportFromApi {

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        return ReportFromApi(
                createdAt = simpleDateFormat.parse(createdAt),
                value = value,
                projectId = projectId,
                comment = description)
    }

}