package pl.elpassion.project.choose

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.commons.RxSchedulersRule
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.dto.newProject
import rx.Observable

class ProjectChooseControllerTest {

    @JvmField @Rule
    val rxSchedulersRule = RxSchedulersRule()

    val view = mock<ProjectChoose.View>()
    val repository = mock<ProjectRepository>()
    val controller = ProjectChooseController(view, repository)

    @Test
    fun shouldShowPossibleProjects() {
        stubRepositoryToReturn(emptyList())
        controller.onCreate()
        verify(view).showPossibleProjects(emptyList())
    }

    @Test
    fun shouldShowPossibleProjectFormRepository() {
        val projects = listOf(newProject())
        stubRepositoryToReturn(projects)
        controller.onCreate()
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
        controller.onCreate()
        verify(view).showPossibleProjects(argThat { this[0].name == "A" && this[1].name == "B" && this[2].name == "Z" })
    }

    @Test
    fun shouldShowFilteredProjects() {
        stubRepositoryToReturn(listOf(newProject(name = "A"), newProject(name = "A"), newProject(name = "B")))
        controller.onCreate(createSearch("B"))

        verify(view).showPossibleProjects(argThat { this[0].name == "B" })
    }


    @Test
    fun shouldShowFilteredSortedProjects() {
        stubRepositoryToReturn(listOf(newProject(name = "Bcd"), newProject(name = "Cde"), newProject(name = "Abc")))

        controller.onCreate(createSearch("C"))

        verify(view).showPossibleProjects(argThat { this[0].name == "Abc" && this[1].name == "Bcd" && this[2].name == "Cde" })
    }

    @Test
    fun shouldShowFilteredProjectsIgnoreCase() {
        stubRepositoryToReturn(listOf(newProject(name = "A"), newProject(name = "A"), newProject(name = "B")))

        controller.onCreate(createSearch("b"))

        verify(view).showPossibleProjects(argThat { this[0].name == "B" })
    }

    @Test
    fun shouldCallToRepositoryOnlyOnce() {
        stubRepositoryToReturn(emptyList())

        controller.onCreate(Observable.just("a", "b"))

        verify(repository).getProjects()
    }

    @Test
    fun shouldShowErrorWhenRepositoryReturnError() {
        whenever(repository.getProjects()).thenReturn(Observable.error(RuntimeException()))

        controller.onCreate()

        verify(view).showError()
    }

    private fun ProjectChooseController.onCreate() {
        onCreate(Observable.just(""))
    }

    private fun createSearch(query: CharSequence) = Observable.just(query)

    private fun stubRepositoryToReturn(list: List<Project>) {
        whenever(repository.getProjects()).thenReturn(Observable.just(list))
    }
}

