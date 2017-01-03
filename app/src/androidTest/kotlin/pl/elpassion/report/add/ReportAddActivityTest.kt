package pl.elpassion.report.add

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.longClick
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.InitIntentsRule
import pl.elpassion.common.checkIntent
import pl.elpassion.common.onToolbarBackArrow
import pl.elpassion.common.rule
import pl.elpassion.project.Project
import pl.elpassion.project.choose.ProjectChooseActivity
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.last.LastSelectedProjectRepository
import pl.elpassion.project.last.LastSelectedProjectRepositoryProvider
import pl.elpassion.startActivity
import rx.Completable

class ReportAddActivityTest {

    val repository = mock<LastSelectedProjectRepository>()

    @JvmField @Rule
    val rule = rule<ReportAddActivity>(autoStart = false)

    @JvmField @Rule
    val intentsRule = InitIntentsRule()

    @Test
    fun shouldHaveVisibleBackArrow() {
        stubRepositoryAndStart()
        onToolbarBackArrow().isDisplayed()
    }

    @Test
    fun shouldStartWithFirstProjectSelected() {
        stubRepositoryAndStart()
        onText("name").isDisplayed()
    }

    @Test
    @Ignore
    fun shouldAddButtonDisabledWhenNoCachedProjects() {
        stubRepositoryAndStart(null)
        onId(R.id.reportAddAdd).isDisabled()
    }

    @Test
    @Ignore
    fun shouldAddButtonEnableWhenCachedProjectsAvailable() {
        stubRepositoryAndStart()
        onId(R.id.reportAddAdd).isEnabled()
    }

    @Test
    fun shouldReallyStartWithFirstProjectSelected() {
        stubRepositoryAndStart(newProject(name = "Project name"))
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

    @Test
    fun shouldShowLoaderOnReportAddCall() {
        ReportAdd.ApiProvider.override = { mock<ReportAdd.Api>().apply { whenever(addReport(any(), any(), any(), any())).thenReturn(Completable.never()) } }
        stubRepositoryAndStart()
        onId(R.id.reportAddDescription).perform(ViewActions.replaceText("description"))
        onId(R.id.reportAddAdd).click()
        onId(R.id.loader).isDisplayed()
    }

    private fun stubRepositoryAndStart(projects: Project? = newProject(), date: String = "2016-01-01") {
        whenever(repository.getLastProject()).thenReturn(projects)
        LastSelectedProjectRepositoryProvider.override = { repository }
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), date))
    }
}

