package pl.elpassion.report.edit

import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.rule
import pl.elpassion.project.Project
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.ProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.Report
import pl.elpassion.startActivity

class ReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditActivity>(autoStart = false)

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

    private fun stubRepositoryAndStart(newProject: Project) {
        ProjectRepositoryProvider.override = {
            mock<ProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject)) }
        }
    }

    private fun startActivity(report: Report = newReport()) {
        rule.startActivity(ReportEditActivity.intent(report))
    }
}

