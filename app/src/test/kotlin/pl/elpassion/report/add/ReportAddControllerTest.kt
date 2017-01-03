package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.Project
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.last.LastSelectedProjectRepository
import rx.Completable

class ReportAddControllerTest {

    val view = mock<ReportAdd.View>()
    val api = mock<ReportAdd.Api>()
    val repository = mock<LastSelectedProjectRepository>()

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubApiToReturn(Completable.complete())
        stubRepositoryToReturn()
    }

    @Test
    fun shouldCloseAfterAddingNewReport() {
        val controller = createController()
        controller.onCreate()
        controller.sendAddReport("description", "8")
        verify(view).close()
    }

    @Test
    fun shouldShowLoaderOnAddingNewReport() {
        stubApiToReturn(Completable.never())
        val controller = createController()
        controller.onCreate()
        controller.sendAddReport("description", "8")

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportFinish() {
        stubApiToReturn(Completable.complete())
        val controller = createController()
        controller.onCreate()
        controller.sendAddReport("description", "8")

        verify(view).showLoader()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportCanceledByOnDestroy() {
        stubApiToReturn(Completable.never())
        val controller = createController()
        controller.onCreate()
        controller.sendAddReport("description", "8")
        controller.onDestroy()

        verify(view).hideLoader()
    }

    @Test
    fun shouldShowSelectedDate() {
        val controller = createController()

        controller.onDateSelect("2016-05-04")

        verify(view).showDate("2016-05-04")
    }

    @Test
    fun shouldChangeDateAfterOnCreate() {
        val controller = createController("2016-01-04")

        controller.onDateSelect("2016-05-04")
        controller.sendAddReport("description", "8")

        verify(api).addReport(eq("2016-05-04"), any(), any(), any())
    }

    @Test
    fun shouldShowErrorWhenAddingReportFails() {
        whenever(api.addReport(any(), any(), any(), any())).thenReturn(Completable.error(RuntimeException()))
        val controller = createController()
        controller.onCreate()
        controller.sendAddReport("description", "8")
        verify(view).showError(any())
    }

    @Test
    fun shouldShowDate() {
        val controller = createController("2016-09-23")
        controller.onCreate()
        verify(view).showDate("2016-09-23")
    }

    @Test
    fun shouldUseApi() {
        val exception = RuntimeException()
        val project = Project(1, "Slack")
        whenever(api.addReport("2016-09-23", project.id, "8", "description")).thenReturn(Completable.error(exception))
        val controller = createController("2016-09-23")

        controller.sendAddReport("description", "8")
        verify(view).showError(exception)
    }

    @Test
    fun shouldShowCurrentDateWhenNotDateNotSelected() {
        stubCurrentTime(2016, 2, 1)
        val controller = createController(null)
        controller.onCreate()

        verify(view).showDate("2016-02-01")
    }

    @Test
    fun shouldShowRegularReportDetailsFormAfterReportTypeChangedToRegularReport() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.REGULAR)
        verify(view).showRegularReportDetails()
    }

    @Test
    fun shouldShowPaidVacationDetailsFormAfterReportTypeChangedToPaidVacations() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.PAID_VACATIONS)
        verify(view).showPaidVacationsReportDetails()
    }

    @Test
    fun shouldShowSickLeaveDetailsFormAfterReportTypeChangedToSickLeave() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.SICK_LEAVE)
        verify(view).showSickLeaveReportDetails()
    }

    @Test
    fun shouldShowUnpaidVacationsReportDetailsFromAfterReportTypeChangedToUnpaidVacations() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.UNPAID_VACATIONS)
        verify(view).showUnpaidVacationsReportDetails()
    }

    @Test
    fun shouldSendEmptyDescriptionWhenSendReportInvokedOnlyWithHours() {
        val controller = createController()
        controller.sendAddReport("8")
        verify(api).addReport(any(), any(), "8", "")
    }

    private fun createController(date: String? = "2016-01-01") = ReportAddController(date, view, api)

    private fun stubRepositoryToReturn(project: Project? = newProject()) {
        whenever(repository.getLastProject()).thenReturn(project)
    }

    private fun stubApiToReturn(completable: Completable) {
        whenever(api.addReport(any(), any(), any(), any())).thenReturn(completable)
    }
}

