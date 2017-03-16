package pl.elpassion.elspace.hub.report.edit

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity
import pl.elpassion.elspace.hub.project.dto.newDailyReport
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.Report

class ReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditActivity>(autoStart = false)

    @Test
    fun shouldShowReportDate() {
        stubReportAndStart(newDailyReport(year = 2016, month = 10, day = 1))
        onId(R.id.reportEditDate).hasText("2016-10-01")
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

    private fun stubReportAndStart(report: Report = newRegularHourlyReport()) {
        rule.startActivity(ReportEditActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}