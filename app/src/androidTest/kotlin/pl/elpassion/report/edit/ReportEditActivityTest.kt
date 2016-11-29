package pl.elpassion.report.edit

import com.elpassion.android.commons.espresso.*
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.rule
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

    private fun startActivity(report: Report = newReport()) {
        rule.startActivity(ReportEditActivity.intent(report))
    }
}

