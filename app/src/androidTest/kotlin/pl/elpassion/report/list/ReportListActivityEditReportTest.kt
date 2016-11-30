package pl.elpassion.report.list

import android.support.test.espresso.action.ViewActions.clearText
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.withText
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.onId
import com.elpassion.android.commons.espresso.onText
import com.elpassion.android.commons.espresso.typeText
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.common.rule
import pl.elpassion.commons.stubCurrentTime
import pl.elpassion.project.ProjectRepository
import pl.elpassion.project.ProjectRepositoryProvider
import pl.elpassion.project.dto.newProject
import pl.elpassion.project.dto.newReport
import pl.elpassion.report.edit.ReportEdit
import rx.Observable

class ReportListActivityEditReportTest {

    private val service = mock<ReportList.Service>()
    private val editReportApi = mock<ReportEdit.EditApi>().apply { whenever(editReport(any(), any(), any(), any(), any())).thenReturn(Observable.just(Unit)) }

    @JvmField @Rule
    val rule = rule<ReportListActivity> {
        ReportEdit.EditApiProvider.override = { editReportApi }
        ProjectRepositoryProvider.override = { mock<ProjectRepository>().apply { whenever(getPossibleProjects()).thenReturn(listOf(newProject())) } }
        stubCurrentTime(year = 2016, month = 10, day = 1)
        whenever(service.getReports())
                .thenReturn(Observable.just(listOf(newReport(year = 2016, month = 10, day = 1, projectName = "Project", description = "Description", reportedHours = 8.0))))
                .thenReturn(Observable.just(listOf(newReport(year = 2016, month = 10, day = 1, projectName = "Project", description = "new Description", reportedHours = 8.0))))
        ReportList.ServiceProvider.override = { service }
    }

    @Test
    fun shouldCloseEditReportActivityAndMakeSecondCallToUpdateReports() {
        onText("Description").click()
        onId(R.id.reportEditDescription).perform(clearText()).typeText("new Description").perform(closeSoftKeyboard())
        onId(R.id.reportEditSaveButton).click()
        onId(R.id.reportsContainer).check(matches(hasDescendant(withText("new Description"))))
    }
}

