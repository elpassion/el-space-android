package pl.elpassion.common

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import com.nhaarman.mockito_kotlin.mock
import org.junit.runner.Description
import org.junit.runners.model.Statement
import pl.elpassion.project.CachedProjectRepositoryProvider
import pl.elpassion.report.add.ReportAdd
import pl.elpassion.report.edit.ReportEdit
import rx.Completable
import rx.Observable

inline fun <reified T : Activity> rule(autoStart: Boolean = true, noinline beforeActivity: () -> Unit = { Unit }): ActivityTestRule<T> {
    return object : ActivityTestRule<T>(T::class.java, false, autoStart) {
        override fun apply(base: Statement?, description: Description?): Statement {
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
    ReportEdit.EditApiProvider.override = {
        object : ReportEdit.EditApi {
            override fun editReport(id: Long, date: String, reportedHour: String, description: String, projectId: Long) = Completable.complete()
        }
    }
    ReportEdit.RemoveApiProvider.override = {
        object : ReportEdit.RemoveApi {
            override fun removeReport(reportId: Long) = Completable.complete()

        }
    }
}

fun stubReportAddApi() {
    ReportAdd.ApiProvider.override = {
        object : ReportAdd.Api {
            override fun addReport(date: String, projectId: Long, hours: String, description: String) = Completable.complete()
        }
    }
}

fun stubProjectRepository() {
    CachedProjectRepositoryProvider.override = {
        mock()
    }
}