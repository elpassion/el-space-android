package pl.elpassion.elspace.common

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.runner.Description
import org.junit.runners.model.Statement
import pl.elpassion.elspace.hub.HubRetrofitProvider
import pl.elpassion.elspace.hub.UnauthenticatedRetrofitProvider
import pl.elpassion.elspace.hub.login.GoogleSingInDI
import pl.elpassion.elspace.hub.project.CachedProjectRepositoryProvider
import pl.elpassion.elspace.hub.report.add.ReportAdd
import pl.elpassion.elspace.hub.report.edit.ReportEdit

inline fun <reified T : Activity> rule(autoStart: Boolean = true, noinline beforeActivity: () -> Unit = { Unit }): ActivityTestRule<T> {
    return object : ActivityTestRule<T>(T::class.java, false, autoStart) {
        override fun apply(base: Statement?, description: Description?): Statement {
            GoogleSingInDI.getELPGoogleSignInResultFromIntent = { throw RuntimeException("Google api not allowed in tests") }
            GoogleSingInDI.startGoogleSignInActivity = { _, _, _ -> throw RuntimeException("Google api not allowed in tests") }
            HubRetrofitProvider.override = { throw RuntimeException("Internet in tests not allowed!") }
            UnauthenticatedRetrofitProvider.override = { throw RuntimeException("Internet in tests not allowed!") }
            Animations.areEnabled = false
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

            override fun editReport(id: Long, reportType: Int, date: String, reportedHour: String?, description: String?, projectId: Long?) = Completable.complete()
        }
    }
}

fun stubReportAddApi() {
    ReportAdd.ApiProvider.override = {
        object : ReportAdd.Api {
            override fun addRegularReport(date: String, projectId: Long, hours: String, description: String) = Observable.just(Unit)

            override fun addPaidVacationsReport(date: String, hours: String) = Observable.just(Unit)

            override fun addSickLeaveReport(date: String) = Observable.just(Unit)

            override fun addUnpaidVacationsReport(date: String) = Observable.just(Unit)
        }
    }
}

fun stubProjectRepository() {
    CachedProjectRepositoryProvider.override = {
        mock()
    }
}