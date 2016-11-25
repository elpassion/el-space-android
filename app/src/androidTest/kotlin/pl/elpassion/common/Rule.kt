package pl.elpassion.common

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import com.nhaarman.mockito_kotlin.mock
import org.junit.runner.Description
import org.junit.runners.model.Statement
import pl.elpassion.project.ProjectRepositoryProvider
import pl.elpassion.report.add.ReportAdd
import rx.Observable

inline fun <reified T : Activity> rule(autoStart: Boolean = true, noinline beforeActivity: () -> Unit = { Unit }): ActivityTestRule<T> {
    return object : ActivityTestRule<T>(T::class.java, false, autoStart) {
        override fun apply(base: Statement?, description: Description?): Statement {
            stubReportAddApi()
            stubProjectRepository()
            return super.apply(base, description)
        }

        override fun beforeActivityLaunched() {
            beforeActivity.invoke()
        }
    }
}

fun stubReportAddApi() {
    ReportAdd.ApiProvider.override = {
        object : ReportAdd.Api {
            override fun addReport(date: String, projectId: String, hours: String, description: String) = Observable.just(Unit)
        }
    }
}

fun stubProjectRepository() {
    ProjectRepositoryProvider.override = {
        mock()
    }
}