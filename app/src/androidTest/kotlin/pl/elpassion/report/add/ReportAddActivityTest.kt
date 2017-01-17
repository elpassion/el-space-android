package pl.elpassion.report.add

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
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
        onId(R.id.reportAddDate).textInputEditTextHasHint(R.string.report_add_date_header).isDisplayed()
    }

    @Test
    fun shouldHaveProjectHint() {
        stubRepositoryAndStart()
        withId(R.id.reportAddProjectName).withDisplayedParent().textInputEditTextHasHint(R.string.report_add_project_header)
    }

    @Test
    fun shouldHaveHoursHint() {
        stubRepositoryAndStart()
        withId(R.id.reportAddHours).withDisplayedParent().textInputEditTextHasHint(R.string.report_add_hours_header)
    }

    @Test
    fun shouldHaveCommentHint() {
        stubRepositoryAndStart()
        withId(R.id.reportAddDescription).withDisplayedParent().textInputEditTextHasHint(R.string.report_add_description_hint)
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
    @Ignore //There is no way to test this right now
    fun shouldRegularReportActionBeSelectedOnStart() {
        stubRepositoryAndStart()
        onId(R.id.action_regular_report).isChecked()
    }

    @Test
    @Ignore //There is no way to test this right now
    fun shouldSelectPaidVacationsActionAfterSwipe() {
        stubRepositoryAndStart()
        onId(R.id.reportAddReportDetailsForm).perform(swipeLeft())
        onId(R.id.action_paid_vacations_report).isChecked()
    }

    @Test
    fun shouldShowPaidVacationsDetailsAfterClickOnPaidVacationsReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_paid_vacations_report).click()

        withText("name").withDisplayedParent().doesNotExist()
        withId(R.id.reportAddDescription).withDisplayedParent().doesNotExist()

        withId(R.id.reportAddHours).withDisplayedParent().isDisplayed()
    }

    @Test
    fun shouldShowRegularDetailsAfterReturnToRegularReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_paid_vacations_report).click()
        onId(R.id.action_regular_report).click()

        withId(R.id.reportAddDescription).withDisplayedParent().textInputEditTextHasHint(R.string.report_add_description_hint)
        withId(R.id.reportAddProjectName).withDisplayedParent().isDisplayed()
        withText("name").withDisplayedParent().isDisplayed()
        withId(R.id.reportAddHours).withDisplayedParent().textInputEditTextHasHint(R.string.report_add_hours_header)
        withId(R.id.reportAddHours).withDisplayedParent().isDisplayed()
    }

    @Test
    fun shouldShowUnpaidVacationsDetailsAfterClickOnUnpaidVacationsReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_unpaid_vacations_report).click()

        withId(R.id.reportAddDescription).withDisplayedParent().doesNotExist()
        withText("name").withDisplayedParent().doesNotExist()
        withId(R.id.reportAddHours).withDisplayedParent().doesNotExist()

        onText(R.string.report_add_unpaid_vacations_info).isDisplayed()
    }

    @Test
    fun shouldShowSickLeaveDetailsAfterClickOnSickLeaveReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_sick_leave_report).click()

        withId(R.id.reportAddDescription).withDisplayedParent().doesNotExist()
        withText("name").withDisplayedParent().doesNotExist()
        withId(R.id.reportAddHours).withDisplayedParent().doesNotExist()

        onText(R.string.report_add_sick_leave_info).isDisplayed()
    }

    @Test
    fun shouldShowLoaderOnReportAddCall() {
        ReportAdd.ApiProvider.override = { mock<ReportAdd.Api>().apply { whenever(addRegularReport(any(), any(), any(), any())).thenReturn(Completable.never()) } }
        stubRepositoryAndStart()
        onId(R.id.reportAddDescription).perform(ViewActions.replaceText("description"))
        onId(R.id.addReport).click()
        onId(R.id.loader).isDisplayed()
    }

    private fun stubRepositoryAndStart(projects: Project? = newProject(), date: String = "2016-01-01") {
        whenever(repository.getLastProject()).thenReturn(projects)
        LastSelectedProjectRepositoryProvider.override = { repository }
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), date))
    }

    private fun Matcher<View>.withDisplayedParent() = onView(allOf(this, withParent(isDisplayed())))
}

