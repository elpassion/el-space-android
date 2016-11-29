package pl.elpassion.report.edit

import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.onText
import org.junit.Rule
import org.junit.Test
import pl.elpassion.common.rule

class ReportEditActivityTest {

    @JvmField @Rule
    val rule = rule<ReportEditActivity>()

    @Test
    fun shouldHavePerformedAtHeader() {
        onText("PERFORMED AT").isDisplayed()
    }

}

