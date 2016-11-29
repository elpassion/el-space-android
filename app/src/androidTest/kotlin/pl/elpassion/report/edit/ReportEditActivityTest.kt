package pl.elpassion.report.edit

import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
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
        onText("2010-02-10").isDisplayed()
    }

    @Test
    fun shouldHaveProjectHeader() {
        startActivity()
        onText(R.string.report_edit_project_header).isDisplayed()
    }

    @Test
    fun shouldHaveCorrectProjectName() {
        startActivity(newReport(projectName = "newProject"))
        onText("newProject").isDisplayed()
    }

    @Test
    fun shouldReallyHaveCorrectProjectName() {
        startActivity(newReport(projectName = "project 123"))
        onText("project 123").isDisplayed()
    }

    private fun startActivity(report: Report = newReport()) {
        rule.startActivity(ReportEditActivity.intent(report))
    }
}

