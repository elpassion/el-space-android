package pl.elpassion.report.list

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.project.Project
import pl.elpassion.project.dto.newDailyReport
import pl.elpassion.project.dto.newPaidVacationHourlyReport
import pl.elpassion.project.dto.newRegularHourlyReport
import pl.elpassion.project.dto.newProject
import pl.elpassion.report.DailyReportType
import pl.elpassion.report.Report
import pl.elpassion.report.list.service.ProjectListService
import pl.elpassion.report.list.service.ReportFromApi
import pl.elpassion.report.list.service.ReportListService
import rx.Observable
import rx.observers.TestSubscriber

class ReportListServiceTest {

    val subscriber = TestSubscriber<List<Report>>()
    val reportApi = mock<ReportList.ReportApi>()
    val projectListService = mock<ProjectListService>()
    val service = ReportListService(reportApi, projectListService)

    @Before
    fun setUp() {
        stubProjectServiceToReturn(newProject(id = 1, name = "Project"))
    }

    private fun stubProjectServiceToReturn(project: Project) {
        whenever(projectListService.getProjects()).thenReturn(Observable.just(listOf(project)))
    }

    @Test
    fun shouldCorrectlyMapReportsYear() {
        stubReportApiToReturn(newReportFromApi(performedAt = "2012-06-01"))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newRegularHourlyReport(year = 2012)))
    }

    @Test
    fun shouldCorrectlyMapReportsMonth() {
        stubReportApiToReturn(newReportFromApi(performedAt = "2016-01-01"))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newRegularHourlyReport(month = 1)))
    }

    @Test
    fun shouldCorrectlyMapReportsDays() {
        stubReportApiToReturn(newReportFromApi(performedAt = "2016-06-21"))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newRegularHourlyReport(day = 21)))
    }

    @Test
    fun shouldCorrectlyMapReportsValues() {
        stubReportApiToReturn(newReportFromApi(value = 9.0))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newRegularHourlyReport(reportedHours = 9.0)))
    }

    @Test
    fun shouldCorrectlyMapReportsDescription() {
        stubReportApiToReturn(newReportFromApi(description = "1234"))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newRegularHourlyReport(description = "1234")))
    }

    @Test
    fun shouldCorrectlyMapReportsProjectsName() {
        stubProjectServiceToReturn(newProject(id = 2, name = "Test"))
        stubReportApiToReturn(newReportFromApi(projectId = 2))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newRegularHourlyReport(project = newProject(id = 2, name = "Test"))))
    }

    @Test
    fun shouldCorrectlyMapReportsId() {
        stubReportApiToReturn(newReportFromApi(id = 1))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newRegularHourlyReport(id = 1)))
    }

    @Test
    fun shouldReallyCorrectlyMapReportsId() {
        stubReportApiToReturn(newReportFromApi(id = 2))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newRegularHourlyReport(id = 2)))
    }

    @Test
    fun shouldMapRegularReportType() {
        stubReportApiToReturn(newReportFromApi(reportType = 0))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newRegularHourlyReport()))
    }

    @Test
    fun shouldMapPaidVacationsReportType() {
        stubReportApiToReturn(newReportFromApi(reportType = 1))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newPaidVacationHourlyReport()))
    }

    @Test
    fun shouldMapUnpaidVacationsReportType() {
        stubReportApiToReturn(newReportFromApi(reportType = 2))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newDailyReport(reportType = DailyReportType.UNPAID_VACATIONS)))
    }

    @Test
    fun shouldMapSickLeaveReportType() {
        stubReportApiToReturn(newReportFromApi(reportType = 3))
        service.getReports().subscribe(subscriber)
        subscriber.assertValue(listOf(newDailyReport(reportType = DailyReportType.SICK_LEAVE)))
    }

    private fun stubReportApiToReturn(reportFromApi: ReportFromApi) {
        whenever(reportApi.getReports()).thenReturn(Observable.just(listOf(reportFromApi)))
    }

    private fun newReportFromApi(performedAt: String = "2016-06-01",
                                 value: Double = 4.0,
                                 projectId: Long = 1,
                                 description: String = "description",
                                 id: Long = 1,
                                 reportType: Int = 0): ReportFromApi {

        return ReportFromApi(
                id = id,
                performedAt = performedAt,
                value = value,
                projectId = projectId,
                comment = description,
                reportType = reportType)
    }
}