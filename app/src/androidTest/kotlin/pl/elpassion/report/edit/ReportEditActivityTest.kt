package pl.elpassion.report.edit

import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.rule

class ReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditActivity>()

    @Test
    fun shouldHavePerformedAtHeader() {
        onText(R.string.report_edit_date_header).isDisplayed()
    }

}

