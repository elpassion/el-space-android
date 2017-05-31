package pl.elpassion.elspace.hub.report.add

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers.trampoline
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepository
import pl.elpassion.elspace.hub.report.*

class ReportAddControllerTest {

    private val onAddReportClicks = PublishSubject.create<ReportViewModel>()
    private val reportTypeChanges = PublishSubject.create<ReportType>()
    private val projectClickEvents = PublishSubject.create<Unit>()
    private val view = mock<ReportAdd.View>()
    private val api = mock<ReportAdd.Api>()
    private val repository = mock<LastSelectedProjectRepository>()
    private val subscribeOn = TestScheduler()
    private val observeOn = TestScheduler()
    private val addReportApi = PublishSubject.create<Unit>()

    @Before
    fun setUp() {
        stubApiToReturnSubject()
        stubRepositoryToReturn()
        whenever(view.addReportClicks()).thenReturn(onAddReportClicks)
        whenever(view.reportTypeChanges()).thenReturn(reportTypeChanges)
        whenever(view.projectClickEvents()).thenReturn(projectClickEvents)
    }

    private fun stubApiToReturnSubject() {
        whenever(api.addRegularReport(any(), any(), any(), any())).thenReturn(addReportApi)
        whenever(api.addSickLeaveReport(any())).thenReturn(addReportApi)
        whenever(api.addUnpaidVacationsReport(any())).thenReturn(addReportApi)
        whenever(api.addPaidVacationsReport(any(), any())).thenReturn(addReportApi)
    }

    @Test
    fun shouldCloseAfterAddingNewReport() {
        createController().onCreate()
        addUnpaidVacationReport()
        completeReportAdd()
        verify(view).close()
    }

    @Test
    fun shouldShowLoaderOnAddingNewReport() {
        createController().onCreate()
        addUnpaidVacationReport()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportFinish() {
        createController().onCreate()
        addUnpaidVacationReport()
        completeReportAdd()
        verify(view).showLoader()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenAddingNewReportCanceledByOnDestroy() {
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
        onAddReportClicks.onNext(newRegularViewModel(selectedDate = "2016-05-04"))
        verify(api).addRegularReport(eq("2016-05-04"), any(), any(), any())
    }

    @Test
    fun shouldShowErrorWhenAddingReportFails() {
        createController().onCreate()
        onAddReportClicks.onNext(newRegularViewModel())
        addReportApi.onError(RuntimeException())
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
        createController("2016-09-23").onCreate()
        onAddReportClicks.onNext(newRegularViewModel())
        addReportApi.onError(exception)
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
        onAddReportClicks.onNext(newRegularViewModel(selectedDate = "2016-09-23", project = newProject(id = 1), hours = "8", description = "description"))
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
    fun shouldShowSelectedProjectOnProjectChange() {
        val project = newProject()
        createController().onProjectChanged(project)
        verify(view).showSelectedProject(project)
    }

    @Test
    fun shouldCallSenderAfterOnReportAdded() {
        createController().onCreate()
        onAddReportClicks.onNext(newRegularViewModel(selectedDate = "date", project = newProject(id = 1), hours = "8", description = "description"))
        verify(api).addRegularReport("date", projectId = 1, hours = "8", description = "description")
    }

    @Test
    fun shouldReallyCallSenderAfterOnReportAdded() {
        createController().onCreate()
        onAddReportClicks.onNext(newRegularViewModel(selectedDate = "date", project = newProject(id = 2), hours = "9", description = "description2"))
        verify(api).addRegularReport(date = "date", hours = "9", projectId = 2, description = "description2")
    }

    @Test
    fun shouldShowEmptyDescriptionErrorWhenDescriptionIsEmpty() {
        createController().onCreate()
        onAddReportClicks.onNext(newRegularViewModel(description = ""))
        verify(view).showEmptyDescriptionError()
    }

    @Test
    fun shouldShowEmptyProjectErrorWhenProjectWasNotSelected() {
        createController().onCreate()
        onAddReportClicks.onNext(newRegularViewModel(project = null))
        verify(view).showEmptyProjectError()
    }

    @Test
    fun shouldNotCloseScreenWhenDescriptionIsEmpty() {
        createController().onCreate()
        onAddReportClicks.onNext(newRegularViewModel(description = ""))
        verify(view, never()).close()
    }

    @Test
    fun shouldChangeDate() {
        createController().onDateChanged("2016-01-01")
        verify(view).showDate("2016-01-01")
    }

    @Test
    fun shouldSubscribeOnGivenScheduler() {
        createController(subscribeOnScheduler = subscribeOn).onCreate()
        onAddReportClicks.onNext(newRegularViewModel())
        completeReportAdd()
        verify(view, never()).hideLoader()
        subscribeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldObserveOnGivenScheduler() {
        createController(observeOnScheduler = observeOn).onCreate()
        onAddReportClicks.onNext(newRegularViewModel())
        completeReportAdd()
        verify(view, never()).hideLoader()
        observeOn.triggerActions()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenDescriptionIsEmptyOnAddingRegularReport() {
        createController().onCreate()
        onAddReportClicks.onNext(newRegularViewModel(description = ""))
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderWhenProjectIsEmptyOnAddingRegularReport() {
        createController().onCreate()
        onAddReportClicks.onNext(newRegularViewModel(project = null))
        verify(view).hideLoader()
    }

    @Test
    fun shouldNotEndMainEventsStreamOnApiCallFail() {
        createController().onCreate()
        onAddReportClicks.onNext(newRegularViewModel())
        addReportApi.onError(RuntimeException())
        verify(view).showLoader()
        onAddReportClicks.onNext(newRegularViewModel())
        verify(view, times(2)).showLoader()
    }

    private fun newRegularViewModel(selectedDate: String = "date", project: Project? = newProject(id = 1), hours: String = "8", description: String = "description")
            = RegularViewModel(selectedDate = selectedDate, project = project, hours = hours, description = description)

    private fun createController(date: String? = "2016-01-01", subscribeOnScheduler: Scheduler = trampoline(), observeOnScheduler: Scheduler = trampoline()) =
            ReportAddController(date, view, api, repository, SchedulersSupplier(backgroundScheduler = subscribeOnScheduler, uiScheduler = observeOnScheduler))

    private fun stubRepositoryToReturn(project: Project? = newProject()) {
        whenever(repository.getLastProject()).thenReturn(project)
    }

    private fun completeReportAdd() {
        addReportApi.onNext(Unit)
        addReportApi.onComplete()
    }

    private fun addUnpaidVacationReport() {
        reportTypeChanges.onNext(ReportType.UNPAID_VACATIONS)
        onAddReportClicks.onNext(DailyViewModel("2016-01-01"))
    }

    private fun addSickLeaveReport() {
        reportTypeChanges.onNext(ReportType.SICK_LEAVE)
        onAddReportClicks.onNext(DailyViewModel("2016-01-01"))
    }

    private fun addPaidVacationReport() {
        reportTypeChanges.onNext(ReportType.PAID_VACATIONS)
        onAddReportClicks.onNext(PaidVacationsViewModel("2016-09-23", "8"))
    }
}
