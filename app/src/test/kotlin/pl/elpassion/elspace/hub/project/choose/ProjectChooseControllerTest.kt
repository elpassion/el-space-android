package pl.elpassion.elspace.hub.project.choose

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.commons.RxSchedulersRule
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.ProjectRepository
import pl.elpassion.elspace.hub.project.dto.newProject
import rx.Observable
import rx.schedulers.Schedulers.trampoline
import rx.schedulers.TestScheduler
import rx.subjects.PublishSubject

class ProjectChooseControllerTest {

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    private val view = mock<ProjectChoose.View>()
    private val publishSubject = PublishSubject.create<List<Project>>()
    private val projectRepository = mock<ProjectRepository>()
    private val subscribeOnScheduler = TestScheduler()
    private val observeOnScheduler = TestScheduler()
    private val controller = ProjectChooseController(view, projectRepository, SchedulersSupplier(trampoline(), trampoline()))

    @Before
    fun setUp() {
        whenever(projectRepository.getProjects()).thenReturn(publishSubject)
    }

    @Test
    fun shouldShowPossibleProjects() {
        onCreate()
        emitProjects(emptyList())
        verify(view).showPossibleProjects(emptyList())
    }

    @Test
    fun shouldShowPossibleProjectFormRepository() {
        val projects = listOf(newProject())
        onCreate()
        emitProjects(projects)
        verify(view).showPossibleProjects(projects)
    }

    @Test
    fun shouldSelectClickedProject() {
        val project = newProject()
        controller.onProjectClicked(project)
        verify(view).selectProject(project)
    }

    @Test
    fun shouldReturnSortedProjects() {
        onCreate()
        emitProjects(listOf(newProject(name = "B"), newProject(name = "Z"), newProject(name = "A")))
        verify(view).showPossibleProjects(argThat { this[0].name == "A" && this[1].name == "B" && this[2].name == "Z" })
    }

    @Test
    fun shouldShowFilteredProjects() {
        onCreate("B")
        emitProjects(listOf(newProject(name = "A"), newProject(name = "A"), newProject(name = "B")))
        verify(view).showPossibleProjects(argThat { this[0].name == "B" })
    }

    @Test
    fun shouldShowFilteredSortedProjects() {
        onCreate("C")
        emitProjects(listOf(newProject(name = "Bcd"), newProject(name = "Cde"), newProject(name = "Abc")))
        verify(view).showPossibleProjects(argThat { this[0].name == "Abc" && this[1].name == "Bcd" && this[2].name == "Cde" })
    }

    @Test
    fun shouldShowFilteredProjectsIgnoreCase() {
        onCreate("b")
        emitProjects(listOf(newProject(name = "A"), newProject(name = "A"), newProject(name = "B")))
        verify(view).showPossibleProjects(argThat { this[0].name == "B" })
    }

    @Test
    fun shouldCallToRepositoryOnlyOnce() {
        controller.onCreate(Observable.just("a", "b"))
        emitProjects(emptyList())
        verify(projectRepository).getProjects()
    }

    @Test
    fun shouldShowErrorWhenRepositoryReturnError() {
        val exception = RuntimeException()
        onCreate()
        publishSubject.onError(exception)
        verify(view).showError(exception)
    }

    @Test
    fun shouldShowLoadingOnCreate() {
        onCreate()
        emitProjects(emptyList())
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoadingOnFinishRepositoryCall() {
        onCreate()
        emitProjects(emptyList())
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoaderOnDestroy() {
        onCreate()
        controller.onDestroy()
        verify(view).hideLoader()
    }

    @Test
    fun shouldSubscribeProjectsOnCorrectScheduler() {
        val controller = ProjectChooseController(view, projectRepository, SchedulersSupplier(subscribeOnScheduler, trampoline()) )
        controller.onCreate(Observable.just("any"))
        verify(view, never()).showPossibleProjects(any())
        subscribeOnScheduler.triggerActions()
        emitProjects(emptyList())
        verify(view).showPossibleProjects(any())
    }

    @Test
    fun shouldObserveProjectsOnCorrectScheduler() {
        val controller = ProjectChooseController(view, projectRepository, SchedulersSupplier(trampoline(), observeOnScheduler) )
        controller.onCreate(Observable.just("any"))
        verify(view, never()).showPossibleProjects(any())
        emitProjects(emptyList())
        observeOnScheduler.triggerActions()
        verify(view).showPossibleProjects(any())
    }

    private fun onCreate(query: String = "") {
        controller.onCreate(Observable.just(query))
    }

    private fun emitProjects(projects: List<Project>) {
        publishSubject.onNext(projects)
        publishSubject.onCompleted()
    }
}

