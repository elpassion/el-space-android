package pl.elpassion.project.choose

import android.support.test.espresso.action.ViewActions.replaceText
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.activityTestRule
import pl.elpassion.common.onToolbarBackArrow
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.dto.newProject
import rx.Observable

class ProjectChooseActivityTest {

    val repository = mock<ProjectRepository>()

    @JvmField @Rule
    val rule = activityTestRule<ProjectChooseActivity>(autoStart = false, disableAnimation = true)

    @Test
    fun shouldHaveVisibleBackArrow() {
        stubRepositoryToReturn(emptyList())
        rule.launchActivity()

        onToolbarBackArrow().isDisplayed()
    }


    @Test
    fun shouldHaveSearchActionIconOnToolbar() {
        stubRepositoryToReturn(emptyList())
        rule.launchActivity()
        onId(R.id.action_search).isDisplayed()
    }

    @Test
    fun shouldDisplayProjectFromRepository() {
        stubRepositoryToReturn(listOf(newProject()))
        rule.launchActivity()
        onText("name").isDisplayed()
    }

    @Test
    fun shouldDisplayTwoProjectsFromRepository() {
        stubRepositoryToReturn(listOf(newProject(1, "name1"), newProject(2, "name2")))
        rule.launchActivity()
        onText("name1").isDisplayed()
        onText("name2").isDisplayed()
    }

    @Test
    fun shouldDisplayProjectAfterSearch() {
        stubRepositoryToReturn(listOf(newProject(1, "name1"), newProject(2, "name2")))
        rule.launchActivity()

        onId(R.id.action_search).click()
        onId(R.id.search_src_text).perform(replaceText("Name1"))

        onText("name1").isDisplayed()
        onText("name2").doesNotExist()
    }


    private fun stubRepositoryToReturn(projects: List<Project>) {
        ProjectRepositoryProvider.override = { repository }
        whenever(repository.getProjects()).thenReturn(Observable.just(projects))
    }
}

