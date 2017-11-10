package pl.elpassion.elspace.hub.report.edit

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions.scrollTo
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.hasNoChildWithId
import pl.elpassion.elspace.common.isBottomNavigationItemChecked
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.stubAllIntents
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.Project
import pl.elpassion.elspace.hub.project.ProjectRepository
import pl.elpassion.elspace.hub.project.choose.ProjectChooseActivity
import pl.elpassion.elspace.hub.project.choose.ProjectRepositoryProvider
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newPaidVacationHourlyReport
import pl.elpassion.elspace.hub.project.dto.newProject
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.DailyReportType
import pl.elpassion.elspace.hub.report.Report

class ReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditActivity>(autoStart = false)

    @JvmField @Rule
    val intentsRule = InitIntentsRule()

    @Test
    fun shouldDisplayScreenName() {
        stubReportAndStart()
        onText("Edit report").isDisplayed()
    }

    @Test
    fun shouldExitScreenOnBackArrowClick() {
        stubReportAndStart()
        onToolbarBackArrow().click()
        assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldShowReportDate() {
        stubReportAndStart(newDailyReport(year = 2016, month = 10, day = 1))
        onId(R.id.reportEditDate).hasText("2016-10-01")
    }

    @Test
    fun shouldShowDatePickerOnDateClick() {
        stubCurrentTime(year = 2016, month = 3, day = 3)
        stubReportAndStart()
        Espresso.closeSoftKeyboard()
        onId(R.id.reportEditDate).click()
        onText("OK").isDisplayed()
    }

    @Test
    fun shouldShowUpdatedDateOnNewDatePick() {
        stubCurrentTime(year = 2016, month = 3, day = 3)
        stubReportAndStart(newDailyReport(year = 2016, month = 3, day = 2))
        onId(R.id.reportEditDate).click()
        onText("OK").click()
        onId(R.id.reportEditDate).hasText("2016-03-03")
    }

    @Test
    fun shouldShowReportedHoursForHourlyReport() {
        stubReportAndStart(newRegularHourlyReport(reportedHours = 6.5))
        onId(R.id.reportEditHours).hasText("6.5")
    }

    @Test
    fun shouldShowReportedHoursForHourlyReportWithoutTrailingZeroes() {
        stubReportAndStart(newRegularHourlyReport(reportedHours = 6.0))
        onId(R.id.reportEditHours).hasText("6")
    }

    @Test
    fun shouldShowProjectNameForRegularReport() {
        stubReportAndStart(newRegularHourlyReport(project = newProject(name = "Slack Time")))
        onId(R.id.reportEditProjectName).hasText("Slack Time")
    }

    @Test
    fun shouldStartProjectChooserOnProjectClicked() {
        stubReportAndStart(newRegularHourlyReport(project = newProject(name = "Slack Time")))
        stubAllIntents()
        Espresso.closeSoftKeyboard()
        onText("Slack Time").click()
        checkIntent(ProjectChooseActivity::class.java)
    }

    @Test
    fun shouldShowUpdatedProjectNameOnProjectChanged() {
        stubProjectsRepository(listOf(newProject(name = "Project 1"), newProject(name = "Project 2")))
        stubReportAndStart(newRegularHourlyReport(project = newProject(name = "Project 1")))
        Espresso.closeSoftKeyboard()
        onText("Project 1").click()
        onText("Project 2").click()
        onId(R.id.reportEditProjectName).hasText("Project 2")
    }

    @Test
    fun shouldShowDescriptionForRegularReport() {
        stubReportAndStart(newRegularHourlyReport(description = "EL Space"))
        onId(R.id.reportEditDescription).hasText("EL Space")
    }

    @Test
    fun shouldShowAdditionalInfoForSickLeave() {
        stubReportAndStart(newDailyReport(reportType = DailyReportType.SICK_LEAVE))
        onId(R.id.reportEditAdditionalInfo).hasText(R.string.report_add_sick_leave_info)
    }

    @Test
    fun shouldShowAdditionalInfoForUnpaidVacations() {
        stubReportAndStart(newDailyReport(reportType = DailyReportType.UNPAID_VACATIONS))
        onId(R.id.reportEditAdditionalInfo).hasText(R.string.report_add_unpaid_vacations_info)
    }

    @Test
    fun shouldShowAdditionalInfoForPaidConference() {
        stubReportAndStart(newDailyReport(reportType = DailyReportType.PAID_CONFERENCE))
        onId(R.id.reportEditAdditionalInfo).hasText(R.string.report_add_paid_conference_info)
    }

    @Test
    fun shouldShowOnlyRegularFormOnRegularReport() {
        stubReportAndStart(newRegularHourlyReport())
        verifyIsRegularFormDisplayed()
    }

    @Test
    fun shouldShowOnlyPaidVacationsFormOnPaidVacations() {
        stubReportAndStart(newPaidVacationHourlyReport())
        verifyIsPaidVacationsFormDisplayed()
    }

    @Test
    fun shouldShowOnlyDailyFormOnUnpaidVacations() {
        stubReportAndStart(newDailyReport(reportType = DailyReportType.UNPAID_VACATIONS))
        verifyIsUnpaidVacationsFormDisplayed()
    }

    @Test
    fun shouldShowOnlyDailyFormOnSickLeave() {
        stubReportAndStart(newDailyReport(reportType = DailyReportType.SICK_LEAVE))
        verifyIsSickLeaveFormDisplayed()
    }

    @Test
    fun shouldShowOnlyDailyFormOnPaidConference() {
        stubReportAndStart(newDailyReport(reportType = DailyReportType.PAID_CONFERENCE))
        verifyIsPaidConferenceFormDisplayed()
    }

    @Test
    fun shouldShowRegularFormOnRegularReportActionCheck() {
        stubReportAndStart(newDailyReport())
        onId(R.id.action_regular_report).click()
        verifyIsRegularFormDisplayed()
    }

    @Test
    fun shouldShowPaidVacationsFormOnPaidVacationsReportActionCheck() {
        stubReportAndStart(newRegularHourlyReport())
        Espresso.closeSoftKeyboard()
        onId(R.id.action_paid_vacations_report).click()
        verifyIsPaidVacationsFormDisplayed()
    }

    @Test
    fun shouldShowUnpaidVacationsFormOnUnpaidVacationsReportActionCheck() {
        stubReportAndStart(newRegularHourlyReport())
        Espresso.closeSoftKeyboard()
        onId(R.id.action_unpaid_vacations_report).click()
        verifyIsUnpaidVacationsFormDisplayed()
    }

    @Test
    fun shouldShowSickLeaveFormOnSickLeaveReportActionCheck() {
        stubReportAndStart(newRegularHourlyReport())
        Espresso.closeSoftKeyboard()
        onId(R.id.action_sick_leave_report).perform(scrollTo()).click()
        verifyIsSickLeaveFormDisplayed()
    }

    @Test
    fun shouldShowPaidConferenceFormOnPaidConferenceReportActionCheck() {
        stubReportAndStart(newRegularHourlyReport())
        Espresso.closeSoftKeyboard()
        onId(R.id.action_paid_conference_report).click()
        verifyIsPaidConferenceFormDisplayed()
    }

    @Test
    fun shouldShowLoaderOnReportEditCall() {
        stubReportEditApiToNeverComplete()
        stubReportAndStart(newRegularHourlyReport())
        onId(R.id.reportEditHours).replaceText("7.5")
        onId(R.id.editReport).click()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldCloseScreenOnReportEditCallCompleted() {
        stubReportEditApiToImmediatelyComplete()
        stubReportAndStart(newRegularHourlyReport())
        onId(R.id.reportEditHours).replaceText("7.5")
        onId(R.id.editReport).click()
        assertTrue(rule.activity.isFinishing)
    }

    @Test
    fun shouldShowConnectionErrorOnCallError() {
        stubReportEditApiToCompleteWith(Completable.error(RuntimeException()))
        stubReportAndStart()
        onId(R.id.editReport).click()
        onId(R.id.reportEditCoordinator).hasNoChildWithId(R.id.loader)
        onText(R.string.internet_connection_error).isDisplayed()
    }

    @Test
    fun shouldShowEmptyProjectErrorOnProjectNotSelected() {
        stubReportAndStart(newDailyReport())
        onId(R.id.action_regular_report).click()
        onId(R.id.editReport).click()
        onId(R.id.reportEditCoordinator).hasNoChildWithId(R.id.loader)
        onText(R.string.empty_project_error).isDisplayed()
    }

    @Test
    fun shouldShowEmptyDescriptionErrorOnDescriptionNotFilled() {
        stubProjectsRepository(listOf(newProject(name = "Project 1")))
        stubReportAndStart(newDailyReport())
        onId(R.id.action_regular_report).click()
        onId(R.id.reportEditProjectName).click()
        onText("Project 1").click()
        onId(R.id.editReport).click()
        onId(R.id.reportEditCoordinator).hasNoChildWithId(R.id.loader)
        onText(R.string.empty_description_error).isDisplayed()
    }

    @Test
    fun shouldShowLoaderOnReportRemoveCall() {
        stubReportEditApiToNeverComplete()
        stubReportAndStart()
        onId(R.id.removeReport).click()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldCloseScreenOnReportRemoveCallCompleted() {
        stubReportEditApiToImmediatelyComplete()
        stubReportAndStart()
        onId(R.id.removeReport).click()
        assertTrue(rule.activity.isFinishing)
    }

    private fun stubReportAndStart(report: Report = newRegularHourlyReport()) {
        rule.startActivity(ReportEditActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }

    private fun stubProjectsRepository(projects: List<Project>) {
        mock<ProjectRepository> {
            ProjectRepositoryProvider.override = { it }
            whenever(it.getProjects()).thenReturn(Observable.just(projects))
        }
    }

    private fun stubReportEditApiToNeverComplete() {
        stubReportEditApiToCompleteWith(Completable.never())
    }

    private fun stubReportEditApiToImmediatelyComplete() {
        stubReportEditApiToCompleteWith(Completable.complete())
    }

    private fun stubReportEditApiToCompleteWith(observable: Completable) {
        ReportEdit.ApiProvider.override = {
            object : ReportEdit.Api {
                override fun removeReport(reportId: Long) = observable

                override fun editReport(id: Long, reportType: Int, date: String, reportedHour: String?,
                                        description: String?, projectId: Long?) = observable
            }
        }
    }

    private fun verifyIsRegularFormDisplayed() {
        Espresso.closeSoftKeyboard()
        onId(R.id.action_regular_report).isBottomNavigationItemChecked()
        onId(R.id.reportEditDateLayout).isDisplayed()
        onId(R.id.reportEditHoursLayout).isDisplayed()
        onId(R.id.reportEditProjectNameLayout).isDisplayed()
        onId(R.id.reportEditDescriptionLayout).isDisplayed()
        onId(R.id.reportEditAdditionalInfo).isNotDisplayed()
    }

    private fun verifyIsPaidVacationsFormDisplayed() {
        Espresso.closeSoftKeyboard()
        onId(R.id.action_paid_vacations_report).isBottomNavigationItemChecked()
        onId(R.id.reportEditDateLayout).isDisplayed()
        onId(R.id.reportEditHoursLayout).isDisplayed()
        onId(R.id.reportEditProjectNameLayout).isNotDisplayed()
        onId(R.id.reportEditDescriptionLayout).isNotDisplayed()
        onId(R.id.reportEditAdditionalInfo).isNotDisplayed()
    }

    private fun verifyIsUnpaidVacationsFormDisplayed() {
        Espresso.closeSoftKeyboard()
        onId(R.id.action_unpaid_vacations_report).isBottomNavigationItemChecked()
        onId(R.id.reportEditDateLayout).isDisplayed()
        onId(R.id.reportEditHoursLayout).isNotDisplayed()
        onId(R.id.reportEditProjectNameLayout).isNotDisplayed()
        onId(R.id.reportEditDescriptionLayout).isNotDisplayed()
        onId(R.id.reportEditAdditionalInfo).isDisplayed()
    }

    private fun verifyIsSickLeaveFormDisplayed() {
        Espresso.closeSoftKeyboard()
        onId(R.id.action_sick_leave_report).isBottomNavigationItemChecked()
        onId(R.id.reportEditDateLayout).isDisplayed()
        onId(R.id.reportEditHoursLayout).isNotDisplayed()
        onId(R.id.reportEditProjectNameLayout).isNotDisplayed()
        onId(R.id.reportEditDescriptionLayout).isNotDisplayed()
        onId(R.id.reportEditAdditionalInfo).isDisplayed()
    }

    private fun verifyIsPaidConferenceFormDisplayed() {
        Espresso.closeSoftKeyboard()
        onId(R.id.action_paid_conference_report).isBottomNavigationItemChecked()
        onId(R.id.reportEditDateLayout).isDisplayed()
        onId(R.id.reportEditHoursLayout).isNotDisplayed()
        onId(R.id.reportEditProjectNameLayout).isNotDisplayed()
        onId(R.id.reportEditDescriptionLayout).isNotDisplayed()
        onId(R.id.reportEditAdditionalInfo).isDisplayed()
    }
}