package pl.elpassion.report.add

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test

class ReportControllerTest {

    @Test
    fun shouldShowPossibleProjects() {
        val view = mock<Report.View>()
        ReportController(view).onCreate()
        verify(view).showPossibleProjects()
    }
}

interface Report {
    interface View {
        fun showPossibleProjects()
    }
}

class ReportController(val view: Report.View) {
    fun onCreate() {
        view.showPossibleProjects()
    }
}
