package pl.elpassion.report.edit

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.contrib.PickerActions
import android.support.test.espresso.matcher.ViewMatchers.withClassName
import android.widget.DatePicker
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.getTimeFrom
import pl.elpassion.common.rule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.Report
import pl.elpassion.startActivity

class DateChooserReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditActivity>(autoStart = false)

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


    private fun startActivity(report: Report = newReport()) {
        rule.startActivity(ReportEditActivity.intent(InstrumentationRegistry.getTargetContext(), report))
    }
}

