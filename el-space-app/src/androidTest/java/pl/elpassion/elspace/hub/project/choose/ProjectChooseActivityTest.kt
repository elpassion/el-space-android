package pl.elpassion.elspace.hub.project.choose

import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.onToolbarBackArrow
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.ProjectRepository
import pl.elpassion.elspace.hub.project.dto.newProject
import io.reactivex.Observable

class ProjectChooseActivityTest {

    val repository = mock<ProjectRepository>()

    @JvmField @Rule
    val rule = rule<ProjectChooseActivity>(autoStart = false)

    @Test
    fun shouldDisplayScreenName() {
        stubRepositoryToReturn(emptyList())
        rule.startActivity()
        onText(R.string.select_project).isDisplayed()
    }

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
        stubRepositoryToReturn(listOf(newProject(1, "name1"), newProject(2, "name2")))
        rule.startActivity()
        onText("name1").isDisplayed()
        onText("name2").isDisplayed()
    }

    private fun stubRepositoryToReturn(projects: List<Project>) {
        ProjectRepositoryProvider.override = { repository }
        whenever(repository.getProjects()).thenReturn(Observable.just(projects))
    }
}

