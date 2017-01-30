package pl.elpassion.elspace.hub.report.edit

import android.support.test.InstrumentationRegistry
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity
import pl.elpassion.elspace.commons.stubCurrentTime
import pl.elpassion.elspace.hub.project.dto.newRegularHourlyReport
import pl.elpassion.elspace.hub.report.RegularHourlyReport
import pl.elpassion.elspace.hub.report.edit.regular.ReportEditRegularActivity

class DateChooserReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditRegularActivity>(autoStart = false)

    @Before
    fun setUp() {
        stubCurrentTime(2013, 1, 3)
    }

    @Test
    fun shouldSetTodayDateAfterDateChange() {
        startActivity()
        CurrentTimeProvider.override = { getTimeFrom(2015, 11, 20).timeInMillis }
        onId(R.id.reportEditDate).click()
        onText("OK").click()
        onId(R.id.reportEditDate).hasText("2015-12-20")
    }


    private fun startActivity(report: RegularHourlyReport = newRegularHourlyReport()) {
        rule.startActivity(ReportEditRegularActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}

