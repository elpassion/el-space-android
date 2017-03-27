package pl.elpassion.elspace.hub.report.edit

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.*
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
import rx.Completable
import rx.Observable

class ReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditActivity>(autoStart = false)

    @JvmField @Rule
    val intentsRule = InitIntentsRule()

    @Test
    fun shouldHaveVisibleBackArrow() {
        stubReportAndStart()
        onToolbarBackArrow().isDisplayed()
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
        onId(R.id.reportEditDate).click()
        onText("Mar 3, ").isDisplayed()
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
        onText("Slack Time").click()
        checkIntent(ProjectChooseActivity::class.java)
    }

    @Test
    fun shouldShowUpdatedProjectNameOnProjectChanged() {
        stubProjectsRepository(listOf(newProject(name = "Project 1"), newProject(name = "Project 2")))
        stubReportAndStart(newRegularHourlyReport(project = newProject(name = "Project 1")))
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
    fun shouldShowOnlyDailyFormOnSickLeave() {
        stubReportAndStart(newDailyReport(reportType = DailyReportType.SICK_LEAVE))
        verifyIsSickLeaveFormDisplayed()
    }

    @Test
    fun shouldShowOnlyDailyFormOnUnpaidVacations() {
        stubReportAndStart(newDailyReport(reportType = DailyReportType.UNPAID_VACATIONS))
        verifyIsUnpaidVacationsFormDisplayed()
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
        onId(R.id.action_paid_vacations_report).click()
        verifyIsPaidVacationsFormDisplayed()
    }

    @Test
    fun shouldShowSickLeaveFormOnSickLeaveReportActionCheck() {
        stubReportAndStart(newRegularHourlyReport())
        onId(R.id.action_sick_leave_report).click()
        verifyIsSickLeaveFormDisplayed()
    }

    @Test
    fun shouldShowUnpaidVacationsFormOnUnpaidVacationsReportActionCheck() {
        stubReportAndStart(newRegularHourlyReport())
        onId(R.id.action_unpaid_vacations_report).click()
        verifyIsUnpaidVacationsFormDisplayed()
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
    fun shouldHideLoaderOnReportEditCallCompleted() {
        stubReportEditApiToImmediatelyComplete()
        stubReportAndStart(newRegularHourlyReport())
        onId(R.id.reportEditHours).replaceText("7.5")
        onId(R.id.editReport).click()
        onId(R.id.reportEditCoordinator).hasNoChildWithId(R.id.loader)
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
        ReportEdit.ApiProvider.override = {
            object : ReportEdit.Api {
                override fun removeReport(reportId: Long) = Completable.never()

                override fun editReport(id: Long, reportType: Int, date: String, reportedHour: String?, description: String?, projectId: Long?): Completable = Completable.never()
            }
        }
    }

    private fun stubReportEditApiToImmediatelyComplete() {
        ReportEdit.ApiProvider.override = {
            object : ReportEdit.Api {
                override fun removeReport(reportId: Long) = Completable.complete()

                override fun editReport(id: Long, reportType: Int, date: String, reportedHour: String?, description: String?, projectId: Long?): Completable = Completable.complete()
            }
        }
    }

    private fun verifyIsRegularFormDisplayed() {
        onId(R.id.action_regular_report).isBottomNavigationItemChecked()
        onId(R.id.reportEditDateLayout).isDisplayed()
        onId(R.id.reportEditHoursLayout).isDisplayed()
        onId(R.id.reportEditProjectNameLayout).isDisplayed()
        onId(R.id.reportEditDescriptionLayout).isDisplayed()
        onId(R.id.reportEditAdditionalInfo).isNotDisplayed()
    }

    private fun verifyIsPaidVacationsFormDisplayed() {
        onId(R.id.action_paid_vacations_report).isBottomNavigationItemChecked()
        onId(R.id.reportEditDateLayout).isDisplayed()
        onId(R.id.reportEditHoursLayout).isDisplayed()
        onId(R.id.reportEditProjectNameLayout).isNotDisplayed()
        onId(R.id.reportEditDescriptionLayout).isNotDisplayed()
        onId(R.id.reportEditAdditionalInfo).isNotDisplayed()
    }

    private fun verifyIsSickLeaveFormDisplayed() {
        onId(R.id.action_sick_leave_report).isBottomNavigationItemChecked()
        onId(R.id.reportEditDateLayout).isDisplayed()
        onId(R.id.reportEditHoursLayout).isNotDisplayed()
        onId(R.id.reportEditProjectNameLayout).isNotDisplayed()
        onId(R.id.reportEditDescriptionLayout).isNotDisplayed()
        onId(R.id.reportEditAdditionalInfo).isDisplayed()
    }

    private fun verifyIsUnpaidVacationsFormDisplayed() {
        onId(R.id.action_unpaid_vacations_report).isBottomNavigationItemChecked()
        onId(R.id.reportEditDateLayout).isDisplayed()
        onId(R.id.reportEditHoursLayout).isNotDisplayed()
        onId(R.id.reportEditProjectNameLayout).isNotDisplayed()
        onId(R.id.reportEditDescriptionLayout).isNotDisplayed()
        onId(R.id.reportEditAdditionalInfo).isDisplayed()
    }
}