package pl.elpassion.elspace.common

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import com.nhaarman.mockito_kotlin.mock
import org.junit.runner.Description
import org.junit.runners.model.Statement
import pl.elpassion.elspace.hub.project.CachedProjectRepositoryProvider
import pl.elpassion.elspace.hub.report.add.ReportAdd
import pl.elpassion.elspace.hub.report.edit.ReportEdit
import rx.Completable

inline fun <reified T : Activity> rule(autoStart: Boolean = true, noinline beforeActivity: () -> Unit = { Unit }): ActivityTestRule<T> {
    return object : ActivityTestRule<T>(T::class.java, false, autoStart) {
        override fun apply(base: Statement?, description: Description?): Statement {
            Animations.disable()
            stubReportAddApi()
            stubProjectRepository()
            stubReportEditApi()
            return super.apply(base, description)
        }

        override fun beforeActivityLaunched() {
            beforeActivity.invoke()
        }
    }
}

fun stubReportEditApi() {
    ReportEdit.ApiProvider.override = {
        object : ReportEdit.Api {
            override fun removeReport(reportId: Long) = Completable.complete()

            override fun editReport(id: Long, date: String, reportedHour: String, description: String, projectId: Long?) = Completable.complete()
        }
    }
}

fun stubReportAddApi() {
    ReportAdd.ApiProvider.override = {
        object : ReportAdd.Api {
            override fun addRegularReport(date: String, projectId: Long, hours: String, description: String) = Completable.complete()

            override fun addPaidVacationsReport(date: String, hours: String) = Completable.complete()

            override fun addSickLeaveReport(date: String) = Completable.complete()

            override fun addUnpaidVacationsReport(date: String) = Completable.complete()
        }
    }
}

fun stubProjectRepository() {
    CachedProjectRepositoryProvider.override = {
        mock()
    }
}