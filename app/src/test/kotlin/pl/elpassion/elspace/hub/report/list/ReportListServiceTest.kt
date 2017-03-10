package pl.elpassion.elspace.hub.report.list

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.DailyReportType
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.service.ProjectListService
import pl.elpassion.elspace.hub.report.list.service.ReportFromApi
import pl.elpassion.elspace.hub.report.list.service.ReportListService
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
        getReports(newYearMonth(2012, 6))
        subscriber.assertValue(listOf(newRegularHourlyReport(year = 2012)))
    }

    @Test
    fun shouldCorrectlyMapReportsMonth() {
        stubReportApiToReturn(newReportFromApi(performedAt = "2016-01-01"))
        getReports(newYearMonth(2016, 1))
        subscriber.assertValue(listOf(newRegularHourlyReport(month = 1)))
    }

    @Test
    fun shouldCorrectlyMapReportsDays() {
        stubReportApiToReturn(newReportFromApi(performedAt = "2016-06-21"))
        getReports()
        subscriber.assertValue(listOf(newRegularHourlyReport(day = 21)))
    }

    @Test
    fun shouldCorrectlyMapReportsValues() {
        stubReportApiToReturn(newReportFromApi(value = 9.0))
        getReports()
        subscriber.assertValue(listOf(newRegularHourlyReport(reportedHours = 9.0)))
    }

    @Test
    fun shouldCorrectlyMapReportsDescription() {
        stubReportApiToReturn(newReportFromApi(description = "1234"))
        getReports()
        subscriber.assertValue(listOf(newRegularHourlyReport(description = "1234")))
    }

    @Test
    fun shouldCorrectlyMapReportsProjectsName() {
        stubProjectServiceToReturn(newProject(id = 2, name = "Test"))
        stubReportApiToReturn(newReportFromApi(projectId = 2))
        getReports()
        subscriber.assertValue(listOf(newRegularHourlyReport(project = newProject(id = 2, name = "Test"))))
    }

    @Test
    fun shouldCorrectlyMapReportsId() {
        stubReportApiToReturn(newReportFromApi(id = 1))
        getReports()
        subscriber.assertValue(listOf(newRegularHourlyReport(id = 1)))
    }

    @Test
    fun shouldReallyCorrectlyMapReportsId() {
        stubReportApiToReturn(newReportFromApi(id = 2))
        getReports()
        subscriber.assertValue(listOf(newRegularHourlyReport(id = 2)))
    }

    @Test
    fun shouldMapRegularReportType() {
        stubReportApiToReturn(newReportFromApi(reportType = 0))
        getReports()
        subscriber.assertValue(listOf(newRegularHourlyReport()))
    }

    @Test
    fun shouldMapPaidVacationsReportType() {
        stubReportApiToReturn(newReportFromApi(reportType = 1))
        getReports()
        subscriber.assertValue(listOf(newPaidVacationHourlyReport()))
    }

    @Test
    fun shouldMapUnpaidVacationsReportType() {
        stubReportApiToReturn(newReportFromApi(reportType = 2))
        getReports()
        subscriber.assertValue(listOf(newDailyReport(reportType = DailyReportType.UNPAID_VACATIONS)))
    }

    @Test
    fun shouldMapSickLeaveReportType() {
        stubReportApiToReturn(newReportFromApi(reportType = 3))
        getReports()
        subscriber.assertValue(listOf(newDailyReport(reportType = DailyReportType.SICK_LEAVE)))
    }

    @Test
    fun shouldCallApiWithStartAndEndDate() {
        stubReportApiToReturn(newReportFromApi())
        getReports(newYearMonth(2017, 3))
        verify(reportApi).getReports("2017-03-01", "2017-03-31")
    }

    private fun getReports(yearMonth: YearMonth = newYearMonth(2016, 6)) {
        service.getReports(yearMonth).subscribe(subscriber)
    }

    private fun newYearMonth(year: Int, month: Int) = getTimeFrom(year, month, 1).toYearMonth()

    private fun stubReportApiToReturn(reportFromApi: ReportFromApi) {
        whenever(reportApi.getReports(any(), any())).thenReturn(Observable.just(listOf(reportFromApi)))
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