package pl.elpassion.project.choose

import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.project.Project
import pl.elpassion.project.CachedProjectRepository
import pl.elpassion.project.dto.newProject

class ProjectChooseControllerTest {

    val view = mock<ProjectChoose.View>()
    val repository = mock<CachedProjectRepository>()
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
        controller.onCreate()
        controller.searchQuery("B")

        verify(view).showFilteredProjects(argThat { this[0].name == "B" })
    }


    @Test
    fun shouldShowFilteredSortedProjects() {
        stubRepositoryToReturn(listOf(newProject(name = "Bcd"), newProject(name = "Cde"), newProject(name = "Abc")))
        controller.onCreate()

        controller.searchQuery("C")

        verify(view).showFilteredProjects(argThat { this[0].name == "Abc" && this[1].name == "Bcd" && this[2].name == "Cde" })
    }

    @Test
    fun shouldShowFilteredProjectsIgnoreCase() {
        stubRepositoryToReturn(listOf(newProject(name = "A"), newProject(name = "A"), newProject(name = "B")))
        controller.onCreate()

        controller.searchQuery("b")

        verify(view).showFilteredProjects(argThat { this[0].name == "B" })
    }


    private fun stubRepositoryToReturn(list: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(list)
    }
}

