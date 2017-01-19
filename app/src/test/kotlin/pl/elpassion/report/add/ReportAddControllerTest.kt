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

    private val addReportClicks = PublishSubject.create<ReportViewModel>()
    private val reportTypeChanges = PublishSubject.create<ReportType>()
    private val projectChanges = PublishSubject.create<Project>()
    private val projectClickEvents = PublishSubject.create<Unit>()

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
        whenever(view.reportTypeChanges()).thenReturn(reportTypeChanges)
        whenever(view.projectChanges()).thenReturn(projectChanges)
        whenever(view.projectClickEvents()).thenReturn(projectClickEvents)
    }

    @Test
    fun shouldCloseAfterAddingNewReport() {
        createController().onCreate()
        addReportClicks.onNext(UnpaidVacationsReport("date"))
        verify(view).close()
    }

    @Test
    fun shouldShowLoaderOnAddingNewReport() {
        stubApiToReturn(Completable.never())
        createController().onCreate()
        addReportClicks.onNext(UnpaidVacationsReport("date"))

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportFinish() {
        stubApiToReturn(Completable.complete())
        createController().onCreate()
        addReportClicks.onNext(UnpaidVacationsReport("date"))

        verify(view).showLoader()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportCanceledByOnDestroy() {
        stubApiToReturn(Completable.never())
        val controller = createController()
        controller.onCreate()
        addReportClicks.onNext(UnpaidVacationsReport("date"))
        controller.onDestroy()

        verify(view).hideLoader()
    }

    @Test
    fun shouldShowInitialDateOnCreate() {
        createController("2016-05-04").onCreate()

        verify(view).showDate("2016-05-04")
    }

    @Test
    fun shouldAddReportWithChangedDate() {
        createController("2016-01-04").onCreate()
        addReportClicks.onNext(RegularReport("2016-05-04", newProject(), "desc", "8"))

        verify(api).addRegularReport(eq("2016-05-04"), any(), any(), any())
    }

    @Test
    fun shouldShowErrorWhenAddingReportFails() {
        whenever(api.addRegularReport(any(), any(), any(), any())).thenReturn(Completable.error(RuntimeException()))
        createController().onCreate()

        addReportClicks.onNext(RegularReport("2016-05-04", newProject(), "desc", "8"))
        verify(view).showError(any())
    }

    @Test
    fun shouldShowDate() {
        createController("2016-09-23").onCreate()

        verify(view).showDate("2016-09-23")
    }

    @Test
    fun shouldShowErrorWhenApiFails() {
        val exception = RuntimeException()
        val project = Project(1, "Slack")
        whenever(api.addRegularReport("2016-09-23", project.id, "8", "description")).thenReturn(Completable.error(exception))
        createController("2016-09-23").onCreate()

        addReportClicks.onNext(RegularReport("2016-09-23", newProject(id = 1), "description", "8"))
        verify(view).showError(exception)
    }

    @Test
    fun shouldShowCurrentDateWhenNotDateNotSelected() {
        stubCurrentTime(2016, 2, 1)
        createController(null).onCreate()

        verify(view).showDate("2016-02-01")
    }

    @Test
    fun shouldShowHoursInputAfterReportTypeChangedToRegularReport() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.REGULAR)
        verify(view).showHoursInput()
    }

    @Test
    fun shouldShowDescriptionInputAfterReportTypeChangedToRegularReport() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.REGULAR)
        verify(view).showDescriptionInput()
    }

    @Test
    fun shouldShowProjectChooserAfterReportTypeChangedToRegularReport() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.REGULAR)
        verify(view).showProjectChooser()
    }

    @Test
    fun shouldShowHoursInputAfterReportTypeChangedToPaidVacations() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.PAID_VACATIONS)
        verify(view).showHoursInput()
    }

    @Test
    fun shouldHideDescriptionInputAfterReportTypeChangedToPaidVacations() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.PAID_VACATIONS)
        verify(view).hideDescriptionInput()
    }

    @Test
    fun shouldHideProjectChooserAfterReportTypeChangedToPaidVacations() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.PAID_VACATIONS)
        verify(view).hideProjectChooser()
    }

    @Test
    fun shouldHideHoursInputAfterReportTypeChangedToSickLeave() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        verify(view).hideHoursInput()
    }

    @Test
    fun shouldHideProjectChooserAfterReportTypeChangedToSickLeave() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        verify(view).hideProjectChooser()
    }

    @Test
    fun shouldHideDescriptionInputAfterReportTypeChangedToSickLeave() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        verify(view).hideDescriptionInput()
    }

    @Test
    fun shouldHideHoursInputAfterReportTypeChangedToUnpaidVacations() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        verify(view).hideHoursInput()
    }

    @Test
    fun shouldHideProjectChooserAfterReportTypeChangedToUnpaidVacations() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        verify(view).hideProjectChooser()
    }

    @Test
    fun shouldShowHideDescriptionInputAfterReportTypeChangedToUnpaidVacations() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        verify(view).hideDescriptionInput()
    }

    @Test
    fun shouldReportUnpaidVacationsToApiAfterAddReportUnpaidVacations() {
        createController("2016-01-01").onCreate()

        addReportClicks.onNext(UnpaidVacationsReport("2016-01-01"))
        verify(api).addUnpaidVacationsReport("2016-01-01")
    }

    @Test
    fun shouldReportSickLeaveToApiAfterAddReportSickLeave() {
        createController("2016-01-01").onCreate()

        addReportClicks.onNext(SickLeaveReport("2016-01-01"))
        verify(api).addSickLeaveReport("2016-01-01")
    }

    @Test
    fun shouldShouldUsePaidVacationsApiToAddPaidVacationsReport() {
        createController("2016-09-23").onCreate()

        addReportClicks.onNext(PaidVacationsReport("2016-09-23", "8"))
        verify(api).addPaidVacationsReport("2016-09-23", "8")
    }

    @Test
    fun shouldShouldUseRegularReportApiToAddRegularReport() {
        createController("2016-09-23").onCreate()

        addReportClicks.onNext(RegularReport("2016-09-23", newProject(id = 1), "description", "8"))
        verify(api).addRegularReport("2016-09-23", 1, "8", "description")
    }

    @Test
    fun shouldShowPossibleProject() {
        val project = newProject()
        stubRepositoryToReturn(project)
        createController().onCreate()

        verify(view).showSelectedProject(project)
    }

    @Test
    fun shouldNotShowPossibleProjectWhenRepositoryReturnNull() {
        stubRepositoryToReturn(null)
        createController().onCreate()

        verify(view, never()).showSelectedProject(any())
    }

    @Test
    fun shouldOpenProjectChooserOnProjectClicked() {
        createController().onCreate()
        projectClickEvents.onNext(Unit)
        verify(view).openProjectChooser()
    }

    @Test
    fun shouldShowSelectedProject() {
        stubRepositoryToReturn(null)
        createController().onCreate()
        projectChanges.onNext(newProject())
        verify(view).showSelectedProject(newProject())
    }

    @Test
    fun shouldCallSenderAfterOnReportAdded() {
        whenever(view.getDescription()).thenReturn("description")
        whenever(view.getHours()).thenReturn("8")
        createController().onCreate()

        projectChanges.onNext(newProject(id = 1))
        addReportClicks.onNext(RegularReport("date", project = newProject(id = 1), hours = "8", description = "description"))
        verify(api).addRegularReport("date", projectId = 1, hours = "8", description = "description")
    }

    @Test
    fun shouldReallyCallSenderAfterOnReportAdded() {
        whenever(view.getDescription()).thenReturn("description2")
        whenever(view.getHours()).thenReturn("9")
        createController().onCreate()

        addReportClicks.onNext(RegularReport(selectedDate = "date", project = newProject(id = 2), hours = "9", description = "description2"))
        verify(api).addRegularReport(date = "date", hours = "9", projectId = 2, description = "description2")
    }

    @Test
    fun shouldShowEmptyDescriptionErrorWhenDescriptionIsEmpty() {
        whenever(view.getDescription()).thenReturn("")
        createController().onCreate()

        addReportClicks.onNext(RegularReport(selectedDate = "date", project = newProject(id = 2), hours = "9", description = ""))
        verify(view).showEmptyDescriptionError()
    }

    @Test
    fun shouldShowEmptyProjectErrorWhenProjectWasNotSelected() {
        whenever(view.getDescription()).thenReturn("description")
        createController().onCreate()

        addReportClicks.onNext(RegularReport(selectedDate = "date", project = null, hours = "9", description = "description2"))
        verify(view).showEmptyProjectError()
    }

    private fun createController(date: String? = "2016-01-01") = ReportAddController(date, view, api, repository)

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

