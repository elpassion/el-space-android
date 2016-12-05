package pl.elpassion.project.choose

import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.onToolbarBackArrow
import pl.elpassion.common.rule
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.ProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.startActivity

class ProjectChooseActivityTest {

    val repository = mock<ProjectRepository>()

    @JvmField @Rule
    val rule = rule<ProjectChooseActivity>(autoStart = false)

    @Test
    fun shouldHaveVisibleBackArrow() {
        stubRepositoryToReturn(emptyList())
        rule.startActivity()
        onToolbarBackArrow().isDisplayed()
    }

    @Test
    fun shouldHaveSearchActionIconOnToolbar() {
        stubRepositoryToReturn(emptyList())
        rule.startActivity()
        onId(R.id.action_search).isDisplayed()
    }

    @Test
    fun shouldDisplayProjectFromRepository() {
        stubRepositoryToReturn(listOf(newProject()))
        rule.startActivity()
        onText("name").isDisplayed()
    }

    @Test
    fun shouldDisplayTwoProjectsFromRepository() {
        stubRepositoryToReturn(listOf(newProject("id1", "name1"), newProject("id2", "name2")))
        rule.startActivity()
        onText("name1").isDisplayed()
        onText("name2").isDisplayed()
    }

    @Test
    fun shouldDisplayProjectAfterSearch() {
        stubRepositoryToReturn(listOf(newProject("id1", "name1"), newProject("id2", "name2")))
        rule.startActivity()

        onId(R.id.action_search).click()
        onId(R.id.search_src_text).typeText("Name1")

        onText("name1").isDisplayed()
        onText("name2").doesNotExist()
    }


    private fun stubRepositoryToReturn(projects: List<Project>) {
        ProjectRepositoryProvider.override = { repository }
        whenever(repository.getPossibleProjects()).thenReturn(projects)
    }
}

