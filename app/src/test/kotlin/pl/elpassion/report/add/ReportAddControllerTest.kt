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
import rx.subjects.PublishSubject

class ReportAddControllerTest {

    private val addReportClicks = PublishSubject.create<Unit>()
    val view = mock<ReportAdd.View>()
    val api = mock<ReportAdd.Api>()
    val repository = mock<LastSelectedProjectRepository>()

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    @Before
    fun setUp() {
        stubApiToReturn(Completable.complete())
        stubRepositoryToReturn()
        whenever(view.addReportClicks()).thenReturn(addReportClicks)
    }

    @Test
    fun shouldCloseAfterAddingNewReport() {
        val controller = createController()
        controller.onCreate()
        addReportClicks.onNext(Unit)
        verify(view).close()
    }

    @Test
    fun shouldShowLoaderOnAddingNewReport() {
        stubApiToReturn(Completable.never())
        val controller = createController()
        controller.onCreate()
        addReportClicks.onNext(Unit)

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportFinish() {
        stubApiToReturn(Completable.complete())
        val controller = createController()
        controller.onCreate()
        addReportClicks.onNext(Unit)

        verify(view).showLoader()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportCanceledByOnDestroy() {
        stubApiToReturn(Completable.never())
        val controller = createController()
        controller.onCreate()
        addReportClicks.onNext(Unit)
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
    fun shouldAddReportWithChangedDate() {
        val controller = createController("2016-01-04")
        controller.onCreate()
        controller.onDateSelect("2016-05-04")
        addReportClicks.onNext(Unit)

        verify(api).addRegularReport(eq("2016-05-04"), any(), any(), any())
    }

    @Test
    fun shouldShowErrorWhenAddingReportFails() {
        whenever(api.addRegularReport(any(), any(), any(), any())).thenReturn(Completable.error(RuntimeException()))
        val controller = createController()
        controller.onCreate()
        addReportClicks.onNext(Unit)
        verify(view).showError(any())
    }

    @Test
    fun shouldShowDate() {
        val controller = createController("2016-09-23")
        controller.onCreate()
        verify(view).showDate("2016-09-23")
    }

    @Test
    fun shouldShowErrorWhenApiFails() {
        val exception = RuntimeException()
        val project = Project(1, "Slack")
        whenever(api.addRegularReport("2016-09-23", project.id, "8", "description")).thenReturn(Completable.error(exception))
        createController("2016-09-23").onCreate()

        addReportClicks.onNext(Unit)
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
    fun shouldShowHoursInputAfterReportTypeChangedToRegularReport() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.REGULAR)
        verify(view).showHoursInput()
    }

    @Test
    fun shouldShowDescriptionInputAfterReportTypeChangedToRegularReport() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.REGULAR)
        verify(view).showDescriptionInput()
    }

    @Test
    fun shouldShowProjectChooserAfterReportTypeChangedToRegularReport() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.REGULAR)
        verify(view).showProjectChooser()
    }

    @Test
    fun shouldShowHoursInputAfterReportTypeChangedToPaidVacations() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.PAID_VACATIONS)
        verify(view).showHoursInput()
    }

    @Test
    fun shouldHideDescriptionInputAfterReportTypeChangedToPaidVacations() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.PAID_VACATIONS)
        verify(view).hideDescriptionInput()
    }

    @Test
    fun shouldHideProjectChooserAfterReportTypeChangedToPaidVacations() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.PAID_VACATIONS)
        verify(view).hideProjectChooser()
    }

    @Test
    fun shouldHideHoursInputAfterReportTypeChangedToSickLeave() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.SICK_LEAVE)
        verify(view).hideHoursInput()
    }

    @Test
    fun shouldHideProjectChooserAfterReportTypeChangedToSickLeave() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.SICK_LEAVE)
        verify(view).hideProjectChooser()
    }

    @Test
    fun shouldHideDescriptionInputAfterReportTypeChangedToSickLeave() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.SICK_LEAVE)
        verify(view).hideDescriptionInput()
    }

    @Test
    fun shouldShowUnpaidVacationsReportDetailsFromAfterReportTypeChangedToUnpaidVacations() {
        val controller = createController()
        controller.onCreate()

        controller.onReportTypeChanged(ReportType.UNPAID_VACATIONS)
        verify(view).showUnpaidVacationsReportDetails()
    }

//    @Test
//    fun shouldReportUnpaidVacationsToApiAfterAddReportUnpaidVacations() {
//        val controller = createController("2016-01-01")
//        controller.addUnpaidVacationsReport()
//        verify(api).addUnpaidVacationsReport("2016-01-01")
//    }
//
//    @Test
//    fun shouldReportSickLeaveToApiAfterAddReportSickLeave() {
//        val controller = createController("2016-01-01")
//        controller.addSickLeaveReport()
//        verify(api).addSickLeaveReport("2016-01-01")
//    }
//
//    @Test
//    fun shouldShouldUsePaidVacationsApiToAddPaidVacationsReport() {
//        val controller = createController("2016-09-23")
//
//        controller.addPaidVacationsReport("8")
//        verify(api).addPaidVacationsReport("2016-09-23", "8")
//    }

    private fun createController(date: String? = "2016-01-01") = ReportAddController(date, view, api)

    private fun stubRepositoryToReturn(project: Project? = newProject()) {
        whenever(repository.getLastProject()).thenReturn(project)
    }

    private fun stubApiToReturn(completable: Completable) {
        whenever(api.addRegularReport(any(), any(), any(), any())).thenReturn(completable)
        whenever(api.addSickLeaveReport(any())).thenReturn(completable)
        whenever(api.addUnpaidVacationsReport(any())).thenReturn(completable)
        whenever(api.addPaidVacationsReport(any(), any())).thenReturn(completable)
    }
}

