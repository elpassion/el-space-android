package pl.elpassion.elspace.hub.report.add

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.longClick
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.onToolbarBackArrow
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity
import pl.elpassion.elspace.common.stubAllIntents
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.choose.ProjectChooseActivity
import pl.elpassion.elspace.hub.project.choose.ProjectRepositoryProvider
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepository
import pl.elpassion.elspace.hub.project.last.LastSelectedProjectRepositoryProvider
import rx.Observable

class ReportAddActivityTest {

    val repository = mock<LastSelectedProjectRepository>()

    @JvmField @Rule
    val rule = rule<ReportAddActivity>(autoStart = false) {
        ProjectRepositoryProvider.override = { mock() }
    }

    @JvmField @Rule
    val intentsRule = InitIntentsRule()

    @Test
    fun shouldDisplayScreenName() {
        stubRepositoryAndStart()
        onText("Add new report").isDisplayed()
    }

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
        stubAllIntents()
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
        onId(R.id.reportAddDate).textInputEditTextHasHint(R.string.report_add_date_header).isDisplayed()
    }

    @Test
    fun shouldHaveProjectHint() {
        stubRepositoryAndStart()
        onId(R.id.reportAddProjectName).textInputEditTextHasHint(R.string.report_add_project_header)
    }

    @Test
    fun shouldHaveHoursHint() {
        stubRepositoryAndStart()
        onId(R.id.reportAddHours).textInputEditTextHasHint(R.string.report_add_hours_header)
    }

    @Test
    fun shouldHaveCommentHint() {
        stubRepositoryAndStart()
        onId(R.id.reportAddDescription).textInputEditTextHasHint(R.string.report_add_description_hint)
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
    fun shouldShowPaidVacationsDetailsAfterClickOnPaidVacationsReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_paid_vacations_report).click()

        onText("name").isNotDisplayed()
        onId(R.id.reportAddDescription).isNotDisplayed()

        onId(R.id.reportAddHours).isDisplayed()
    }

    @Test
    fun shouldShowRegularDetailsAfterReturnToRegularReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_paid_vacations_report).click()
        onId(R.id.action_regular_report).click()

        onId(R.id.reportAddDescription).textInputEditTextHasHint(R.string.report_add_description_hint)
        onId(R.id.reportAddProjectName).isDisplayed()
        onText("name").isDisplayed()
        onId(R.id.reportAddHours).textInputEditTextHasHint(R.string.report_add_hours_header)
        onId(R.id.reportAddHours).isDisplayed()
    }

    @Test
    fun shouldShowUnpaidVacationsDetailsAfterClickOnUnpaidVacationsReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_unpaid_vacations_report).click()

        onId(R.id.reportAddDescription).isNotDisplayed()
        onText("name").isNotDisplayed()
        onId(R.id.reportAddHours).isNotDisplayed()

        onText(R.string.report_add_unpaid_vacations_info).isDisplayed()
    }

    @Test
    fun shouldShowSickLeaveDetailsAfterClickOnSickLeaveReportType() {
        stubRepositoryAndStart()
        onId(R.id.action_sick_leave_report).click()

        onId(R.id.reportAddDescription).isNotDisplayed()
        onText("name").isNotDisplayed()
        onId(R.id.reportAddHours).isNotDisplayed()

        onText(R.string.report_add_sick_leave_info).isDisplayed()
    }

    @Test
    fun shouldShowLoaderOnReportAddCall() {
        ReportAdd.ApiProvider.override = { mock<ReportAdd.Api>().apply { whenever(addRegularReport(any(), any(), any(), any())).thenReturn(Observable.never()) } }
        stubRepositoryAndStart()
        onId(R.id.reportAddDescription).perform(ViewActions.replaceText("description"))
        onId(R.id.addReport).click()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldShowPickedDateOnScreen() {
        stubCurrentTime(2017, 1, 28)
        stubRepositoryAndStart(date = "2017-01-27")
        onId(R.id.reportAddDate).click()
        onText("OK").click()
        onId(R.id.reportAddDate).hasText("2017-01-28")
    }

    @Test
    fun shouldShowOnlyRegularFormOnRegularReport() {
        stubRepositoryAndStart()
        onId(R.id.action_regular_report).click()
        onId(R.id.reportAddDescriptionLayout).isDisplayed()
        onId(R.id.reportAddProjectNameLayout).isDisplayed()
        onId(R.id.reportAddHoursLayout).isDisplayed()
        onId(R.id.reportAddAdditionalInfo).isNotDisplayed()
    }

    @Test
    fun shouldShowOnlyPaidVacationsFormOnPaidVacations() {
        stubRepositoryAndStart()
        onId(R.id.action_paid_vacations_report).click()
        onId(R.id.reportAddHoursLayout).isDisplayed()
        onId(R.id.reportAddProjectNameLayout).isNotDisplayed()
        onId(R.id.reportAddDescriptionLayout).isNotDisplayed()
        onId(R.id.reportAddAdditionalInfo).isNotDisplayed()
    }

    @Test
    fun shouldShowOnlySickLeaveFormFormOnSickLeave() {
        stubRepositoryAndStart()
        onId(R.id.action_sick_leave_report).click()
        onText(R.string.report_add_sick_leave_info).isDisplayed()
        onId(R.id.reportAddHoursLayout).isNotDisplayed()
        onId(R.id.reportAddDescriptionLayout).isNotDisplayed()
        onId(R.id.reportAddProjectNameLayout).isNotDisplayed()
    }

    @Test
    fun shouldShowOnlyUnpaidVacationFormOnUnpaidVacation() {
        stubRepositoryAndStart()
        onId(R.id.action_unpaid_vacations_report).click()
        onText(R.string.report_add_unpaid_vacations_info).isDisplayed()
        onId(R.id.reportAddHoursLayout).isNotDisplayed()
        onId(R.id.reportAddDescriptionLayout).isNotDisplayed()
        onId(R.id.reportAddProjectNameLayout).isNotDisplayed()
    }

    private fun stubRepositoryAndStart(projects: Project? = newProject(), date: String = "2016-01-01") {
        whenever(repository.getLastProject()).thenReturn(projects)
        LastSelectedProjectRepositoryProvider.override = { repository }
        rule.startActivity(ReportAddActivity.intent(InstrumentationRegistry.getTargetContext(), date))
    }
}

