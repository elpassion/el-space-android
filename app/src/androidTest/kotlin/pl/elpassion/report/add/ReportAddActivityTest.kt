package pl.elpassion.report.add

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.longClick
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
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.ProjectRepositoryProvider
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
    fun shouldShowDescriptionInput() {
        stubRepositoryAndStart()
        onId(R.id.reportAddDescription).isDisplayed()
    }

    @Test
    fun shouldShowDateToUser() {
        stubRepositoryAndStart(date = "2016-09-23")
        onId(R.id.reportAddDate).hasText("2016-09-23")
    }

    @Test
    fun shouldHaveDateHeader() {
        stubRepositoryAndStart()
        onText(R.string.report_add_date_header).isDisplayed()
    }

    @Test
    fun shouldHaveProjectHeader() {
        stubRepositoryAndStart()
        onText(R.string.report_add_project_header).isDisplayed()
    }

    @Test
    fun shouldHaveHoursHeader() {
        stubRepositoryAndStart()
        onText(R.string.report_add_hours_header).isDisplayed()
    }

    @Test
    fun shouldHaveCommentHeader() {
        stubRepositoryAndStart()
        onText(R.string.report_add_comment_header).isDisplayed()
    }

    @Test
    fun shouldClearTextAfterClickOnHoursInput() {
        stubRepositoryAndStart()
        onId(R.id.reportAddHours).click().hasText("")
    }

    @Test
    fun shouldNotCrashOnLongClickOnHoursInput() {
        stubRepositoryAndStart()
        onId(R.id.reportAddHours).perform(longClick())
    }

    private fun stubRepositoryAndStart(projects: List<Project> = listOf(newProject()), date: String = "2016-01-01") {
        whenever(repository.getPossibleProjects()).thenReturn(projects)
        ProjectRepositoryProvider.override = { repository }
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), date))
    }
}

