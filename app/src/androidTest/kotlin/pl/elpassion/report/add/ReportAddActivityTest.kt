package pl.elpassion.report.add

import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.common.InitIntentsRule
import pl.elpassion.common.checkIntent
import pl.elpassion.project.choose.ProjectChooseActivity
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepository
import pl.elpassion.project.common.ProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.startActivity

class ReportAddActivityTest {

    val repository = mock<ProjectRepository>()

    @JvmField @Rule
    val rule = ActivityTestRule<ReportAddActivity>(ReportAddActivity::class.java, false, false)

    @JvmField @Rule
    val intentsRule = InitIntentsRule()

    @Test
    fun shouldStartWithFirstProjectSelected() {
        stubRepository(listOf(newProject()))
        rule.startActivity()
        onText("name").isDisplayed()
    }

    @Test
    fun shouldReallyStartWithFirstProjectSelected() {
        stubRepository(listOf(newProject(name = "Project name")))
        rule.startActivity()
        onText("Project name").isDisplayed()
    }

    @Test
    fun shouldStartProjectChooserOnProjectClicked() {
        stubRepository(listOf(newProject()))
        rule.startActivity()
        onText("name").click()
        checkIntent(ProjectChooseActivity::class.java)
    }

    private fun stubRepository(listOf: List<Project>) {
        whenever(repository.getPossibleProjects()).thenReturn(listOf)
        ProjectRepositoryProvider.override = { repository }
    }
}

