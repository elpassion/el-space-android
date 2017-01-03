package pl.elpassion.report.add

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.longClick
import android.support.test.espresso.action.ViewActions.swipeLeft
import android.support.test.espresso.matcher.ViewMatchers.*
import android.view.View
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
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
        withId(R.id.reportAddHours).withDisplayedParent().hasText("8")
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
        withText(R.string.report_add_hours_header).withDisplayedParent().isDisplayed()
    }

    @Test
    fun shouldHaveCommentHeader() {
        stubRepositoryAndStart()
        onText(R.string.report_add_comment_header).isDisplayed()
    }

    @Test
    fun shouldClearTextAfterClickOnHoursInput() {
        stubRepositoryAndStart()
        withId(R.id.reportAddHours).withDisplayedParent().click().hasText("")
    }

    @Test
    fun shouldNotCrashOnLongClickOnHoursInput() {
        stubRepositoryAndStart()
        withId(R.id.reportAddHours).withDisplayedParent().perform(longClick())
    }

    @Test
    @Ignore //There is no way to test this right know
    fun shouldRegularReportActionBeSelectedOnStart() {
        stubRepositoryAndStart()
        onId(R.id.action_regular_report).isChecked()
    }

    @Test
    @Ignore //There is no way to test this right know
    fun shouldSelectPaidVacationsActionAfterSwipe() {
        stubRepositoryAndStart()
        onId(R.id.reportAddReportDetailsForm).perform(swipeLeft())
        onId(R.id.action_paid_vacations_report).isChecked()
    }

    @Test
    fun shouldShowPaidVacationsDetailsAfterClickOnSickLeaveReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_paid_vacations_report).click()

        withText(R.string.report_add_comment_header).withDisplayedParent().doesNotExist()
        withText(R.string.report_add_project_header).withDisplayedParent().doesNotExist()
        withText("name").withDisplayedParent().doesNotExist()

        withText(R.string.report_add_hours_header).withDisplayedParent().isDisplayed()
        withId(R.id.reportAddHours).withDisplayedParent().isDisplayed()
    }

    @Test
    fun shouldShowRegularDetailsAfterReturnToRegularReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_paid_vacations_report).click()
        onId(R.id.action_regular_report).click()

        withText(R.string.report_add_comment_header).withDisplayedParent().isDisplayed()
        withText(R.string.report_add_project_header).withDisplayedParent().isDisplayed()
        withText("name").withDisplayedParent().isDisplayed()
        withText(R.string.report_add_hours_header).withDisplayedParent().isDisplayed()
        withId(R.id.reportAddHours).withDisplayedParent().isDisplayed()
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

    private fun Matcher<View>.withDisplayedParent() = onView(allOf(this, withParent(isDisplayed())))
}

