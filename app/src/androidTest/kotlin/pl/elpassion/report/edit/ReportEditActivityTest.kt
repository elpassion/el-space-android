package pl.elpassion.report.edit

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.action.ViewActions.*
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.onToolbarBackArrow
import pl.elpassion.common.rule
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.ProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.Report
import pl.elpassion.startActivity
import rx.Completable

class ReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditActivity>(autoStart = false)

    private val reportEditApi = mock<ReportEdit.EditApi>()

    @Before
    fun setUp() {
        whenever(reportEditApi.editReport(any(), any(), any(), any(), any())).thenReturn(Completable.complete())
        ReportEdit.EditApiProvider.override = { reportEditApi }
    }

    @Test
    fun shouldHaveVisibleBackArrow() {
        startActivity()
        onToolbarBackArrow().isDisplayed()
    }

    @Test
    fun shouldHavePerformedAtHeader() {
        startActivity()
        onText(R.string.report_edit_date_header).isDisplayed()
    }

    @Test
    fun shouldShowCorrectReportDate() {
        startActivity(newReport(year = 2010, month = 2, day = 10))
        onId(R.id.reportEditDate).hasText("2010-02-10")
    }

    @Test
    fun shouldHaveProjectHeader() {
        startActivity()
        onText(R.string.report_edit_project_header).isDisplayed()
    }

    @Test
    fun shouldHaveCorrectProjectName() {
        startActivity(newReport(projectName = "newProject"))
        onId(R.id.reportEditProjectName).hasText("newProject")
    }

    @Test
    fun shouldReallyHaveCorrectProjectName() {
        startActivity(newReport(projectName = "project 123"))
        onId(R.id.reportEditProjectName).hasText("project 123")
    }

    @Test
    fun shouldHaveCorrectHoursHeader() {
        startActivity()
        onText(R.string.report_edit_hours_header).isDisplayed()
    }

    @Test
    fun shouldHaveOldPreviousHoursValueAtTheBegging() {
        startActivity(newReport(reportedHours = 2.32))
        onId(R.id.reportEditHours).hasText("2.32")
    }

    @Test
    fun shouldWipeHoursOnClick() {
        startActivity()
        onId(R.id.reportEditHours).click().hasText("")
    }

    @Test
    fun shouldHaveDescriptionHeader() {
        startActivity()
        onText(R.string.report_edit_comment_header).isDisplayed()
    }

    @Test
    fun shouldHaveCorrectDescription() {
        startActivity(newReport(description = "Sample description"))
        onId(R.id.reportEditDescription).hasText("Sample description")
    }

    @Test
    fun shouldHaveCorrectProjectDisplayedIfItHasBeenChanged() {
        startActivity(newReport(projectName = "project1"))
        stubRepositoryAndStart(newProject(name = "project2"))
        onId(R.id.reportEditProjectName).click()
        onText("project2").click()
        onId(R.id.reportEditProjectName).hasText("project2")
    }

    @Test
    fun shouldHaveSaveButton() {
        startActivity()
        onId(R.id.reportEditSaveButton).hasText(R.string.report_edit_save_button).isDisplayed()
    }

    @Test
    fun shouldCallApiWithCorrectData() {
        startActivity(newReport(projectId = 1, description = "test1", reportedHours = 2.0, year = 2010, month = 10, day = 1, id = 2))
        stubRepositoryAndStart(newProject(name = "project2", id = "2"))
        insertData(reportedHours = "5.5", newDescription = "test2")
        verify(reportEditApi, times(1)).editReport(id = 2, date = "2010-10-01", reportedHour = "5.5", description = "test2", projectId = "2")
    }

    @Test
    fun shouldHaveVisibleRemoveReportIcon() {
        startActivity()
        onId(R.id.action_remove_report).isDisplayed()
    }

    private fun insertData(reportedHours: String, newDescription: String) {
        onId(R.id.reportEditProjectName).click()
        onText("project2").click()
        onId(R.id.reportEditHours).perform(replaceText(reportedHours), closeSoftKeyboard())
        onId(R.id.reportEditDescription).perform(clearText(), replaceText(newDescription), closeSoftKeyboard())
        onId(R.id.reportEditSaveButton).click()
    }

    private fun stubRepositoryAndStart(newProject: Project) {
        ProjectRepositoryProvider.override = {
            mock<ProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject)) }
        }
    }

    private fun startActivity(report: Report = newReport()) {
        rule.startActivity(ReportEditActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}

