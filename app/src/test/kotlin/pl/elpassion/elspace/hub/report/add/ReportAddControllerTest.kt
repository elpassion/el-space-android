package pl.elpassion.elspace.hub.report.add

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.commons.RxSchedulersRule
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepository
import rx.Completable
import rx.subjects.PublishSubject

class ReportAddControllerTest {

    private val addReportClicks = PublishSubject.create<ReportViewModel>()
    private val reportTypeChanges = PublishSubject.create<ReportType>()
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
        whenever(view.projectClickEvents()).thenReturn(projectClickEvents)
    }

    @Test
    fun shouldCloseAfterAddingNewReport() {
        createController().onCreate()
        addUnpaidVacationReport()

        verify(view).close()
    }

    @Test
    fun shouldShowLoaderOnAddingNewReport() {
        stubApiToReturn(Completable.never())
        createController().onCreate()
        addUnpaidVacationReport()

        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportFinish() {
        stubApiToReturn(Completable.complete())
        createController().onCreate()
        addUnpaidVacationReport()

        verify(view).showLoader()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportCanceledByOnDestroy() {
        stubApiToReturn(Completable.never())
        val controller = createController()
        controller.onCreate()
        addUnpaidVacationReport()
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
    fun shouldShowRegularFormAfterReportTypeChangedToRegularReport() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.REGULAR)
        verify(view).showRegularForm()
    }

    @Test
    fun shouldShowPaidVacationsFormAfterReportTypeChangedToPaidVacations() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.PAID_VACATIONS)
        verify(view).showPaidVacationsForm()
    }

    @Test
    fun shouldShowSickLeaveFormAfterReportTypeChangedToSickLeave() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        verify(view).showSickLeaveForm()
    }

    @Test
    fun shouldShowUnpaidVacationFormAfterReportTypeChangeToUnpaidVacation() {
        createController().onCreate()

        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        verify(view).showUnpaidVacationsForm()
    }

    @Test
    fun shouldReportUnpaidVacationsToApiAfterAddReportUnpaidVacations() {
        createController("2016-01-01").onCreate()
        addUnpaidVacationReport()
        verify(api).addUnpaidVacationsReport("2016-01-01")
    }

    @Test
    fun shouldReportSickLeaveToApiAfterAddReportSickLeave() {
        createController("2016-01-01").onCreate()
        addSickLeaveReport()
        verify(api).addSickLeaveReport("2016-01-01")
    }

    @Test
    fun shouldShouldUsePaidVacationsApiToAddPaidVacationsReport() {
        createController("2016-09-23").onCreate()

        addPaidVacationReport()
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
        stubRepositoryToReturn(newProject(id = 3))
        createController().onCreate()
        verify(view).showSelectedProject(newProject(id = 3))
    }

    @Test
    fun shouldCallSenderAfterOnReportAdded() {
        createController().onCreate()

        addReportClicks.onNext(RegularReport("date", project = newProject(id = 1), hours = "8", description = "description"))
        verify(api).addRegularReport("date", projectId = 1, hours = "8", description = "description")
    }

    @Test
    fun shouldReallyCallSenderAfterOnReportAdded() {
        createController().onCreate()

        addReportClicks.onNext(RegularReport(selectedDate = "date", project = newProject(id = 2), hours = "9", description = "description2"))
        verify(api).addRegularReport(date = "date", hours = "9", projectId = 2, description = "description2")
    }

    @Test
    fun shouldShowEmptyDescriptionErrorWhenDescriptionIsEmpty() {
        createController().onCreate()

        addReportClicks.onNext(RegularReport(selectedDate = "date", project = newProject(id = 2), hours = "9", description = ""))
        verify(view).showEmptyDescriptionError()
    }

    @Test
    fun shouldShowEmptyProjectErrorWhenProjectWasNotSelected() {
        createController().onCreate()

        addReportClicks.onNext(RegularReport(selectedDate = "date", project = null, hours = "9", description = "description2"))
        verify(view).showEmptyProjectError()
    }

    @Test
    fun shouldNotCloseScreenWhenDescriptionIsEmpty() {
        createController().onCreate()

        addReportClicks.onNext(RegularReport(selectedDate = "date", project = newProject(), hours = "9", description = ""))
        verify(view, never()).close()
    }

    @Test
    fun shouldChangeDate() {
        createController().onDateChanged("2016-01-01")

        verify(view).showDate("2016-01-01")
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

    private fun addUnpaidVacationReport() {
        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        addReportClicks.onNext(UnpaidVacationsReport("2016-01-01"))
    }

    private fun addSickLeaveReport() {
        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        addReportClicks.onNext(SickLeaveReport("2016-01-01"))
    }

    private fun addPaidVacationReport() {
        reportTypeChanges.onNext(ReportType.PAID_VACATIONS)
        addReportClicks.onNext(PaidVacationsReport("2016-09-23", "8"))
    }
}

