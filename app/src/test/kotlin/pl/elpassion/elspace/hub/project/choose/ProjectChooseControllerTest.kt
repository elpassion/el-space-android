package pl.elpassion.elspace.hub.project.choose

import com.nhaarman.mockito_kotlin.*
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.commons.RxSchedulersRule
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.ProjectRepository
import pl.elpassion.elspace.hub.project.dto.newProject
import rx.Observable
import rx.schedulers.TestScheduler

class ProjectChooseControllerTest {

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    val view = mock<ProjectChoose.View>()
    val repository = mock<ProjectRepository>()
    val subscribeOn = TestScheduler()
    val observeOn = TestScheduler()
    val controller = ProjectChooseController(view, repository, SchedulersSupplier(subscribeOn, observeOn))

    @Test
    fun shouldShowPossibleProjects() {
        stubRepositoryToReturn(emptyList())
        onCreate()
        verify(view).showPossibleProjects(emptyList())
    }

    @Test
    fun shouldShowPossibleProjectFormRepository() {
        val projects = listOf(newProject())
        stubRepositoryToReturn(projects)
        onCreate()
        verify(view).showPossibleProjects(projects)
    }

    @Test
    fun shouldSelectClickedProject() {
        val project = newProject()
        stubRepositoryToReturn(listOf(project))
        controller.onProjectClicked(project)
        verify(view).selectProject(project)
    }

    @Test
    fun shouldReturnSortedProjects() {
        stubRepositoryToReturn(listOf(newProject(name = "B"), newProject(name = "Z"), newProject(name = "A")))
        onCreate()
        verify(view).showPossibleProjects(argThat { this[0].name == "A" && this[1].name == "B" && this[2].name == "Z" })
    }

    @Test
    fun shouldShowFilteredProjects() {
        stubRepositoryToReturn(listOf(newProject(name = "A"), newProject(name = "A"), newProject(name = "B")))
        onCreate("B")
        verify(view).showPossibleProjects(argThat { this[0].name == "B" })
    }


    @Test
    fun shouldShowFilteredSortedProjects() {
        stubRepositoryToReturn(listOf(newProject(name = "Bcd"), newProject(name = "Cde"), newProject(name = "Abc")))
        onCreate("C")
        verify(view).showPossibleProjects(argThat { this[0].name == "Abc" && this[1].name == "Bcd" && this[2].name == "Cde" })
    }

    @Test
    fun shouldShowFilteredProjectsIgnoreCase() {
        stubRepositoryToReturn(listOf(newProject(name = "A"), newProject(name = "A"), newProject(name = "B")))
        onCreate("b")
        verify(view).showPossibleProjects(argThat { this[0].name == "B" })
    }

    @Test
    fun shouldCallToRepositoryOnlyOnce() {
        stubRepositoryToReturn(emptyList())
        controller.onCreate(Observable.just("a", "b"))
        subscribeOn.triggerActions()
        verify(repository).getProjects()
    }

    @Test
    fun shouldShowErrorWhenRepositoryReturnError() {
        val exception = RuntimeException()
        whenever(repository.getProjects()).thenReturn(Observable.error(exception))
        onCreate()
        verify(view).showError(exception)
    }

    @Test
    fun shouldShowLoadingOnCreate() {
        stubRepositoryToReturn(emptyList())
        onCreate()
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoadingOnFinishRepositoryCall() {
        stubRepositoryToReturn(emptyList())
        onCreate()
        verify(view).hideLoader()
    }

    @Test
    fun shouldHideLoadingOnDestroy() {
        whenever(repository.getProjects()).thenReturn(Observable.never())
        onCreate()
        controller.onDestroy()
        verify(view).hideLoader()
    }

    @Test
    fun shouldFetchProjectsOnCorrectScheduler() {
        stubRepositoryToReturn(emptyList())
        controller.onCreate(Observable.just("any"))
        verify(view, never()).showPossibleProjects(any())
    }

    @Test
    fun shouldObserveProjectsOnCorrectScheduler() {
        stubRepositoryToReturn(emptyList())
        controller.onCreate(Observable.just("any"))
        subscribeOn.triggerActions()
        verify(view, never()).showPossibleProjects(any())
    }

    private fun onCreate(query: String = "") {
        controller.onCreate(Observable.just(query))
        subscribeOn.triggerActions()
        observeOn.triggerActions()
    }

    private fun stubRepositoryToReturn(list: List<Project>) {
        whenever(repository.getProjects()).thenReturn(Observable.just(list))
    }
}

