package pl.elpassion.report.add

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.DeaultMocksRule
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
    val defaultMocks = DeaultMocksRule()

    @JvmField @Rule
    val rule = ActivityTestRule<ReportAddActivity>(ReportAddActivity::class.java, false, false)

    @JvmField @Rule
    val intentsRule = InitIntentsRule()

    @Test
    fun shouldStartWithFirstProjectSelected() {
        stubRepositoryAndStart()
        onText("name").isDisplayed()
    }

    @Test
    fun shouldReallyStartWithFirstProjectSelected() {
        stubRepositoryAndStart(listOf(newProject(name = "Project name")))
        onText("Project name").isDisplayed()
    }

    @Test
    fun shouldStartProjectChooserOnProjectClicked() {
        stubRepositoryAndStart()
        onText("name").click()
        checkIntent(ProjectChooseActivity::class.java)
    }

    @Test
    fun shouldShowHoursInput() {
        stubRepositoryAndStart()
        onId(R.id.reportAddHours).hasText("8")
    }

    @Test
    fun shouldRemovePreviousTextWhenHoursTyped() {
        stubRepositoryAndStart()
        onId(R.id.reportAddHours).typeText("4").hasText("4")
    }

    @Test
    fun shouldShowDescriptionInput() {
        stubRepositoryAndStart()
        onId(R.id.reportAddDescription).typeText("Description").hasText("Description")
    }

    @Test
    fun shouldShowDateToUser() {
        stubRepositoryAndStart(date = "2016-09-23")
        onId(R.id.reportAddDate).hasText("2016-09-23")
    }

    private fun stubRepositoryAndStart(projects: List<Project> = listOf(newProject()), date: String = "2016-01-01") {
        whenever(repository.getPossibleProjects()).thenReturn(projects)
        ProjectRepositoryProvider.override = { repository }
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), date))
    }
}

