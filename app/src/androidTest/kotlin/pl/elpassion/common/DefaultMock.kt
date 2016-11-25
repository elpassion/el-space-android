package pl.elpassion.common

import com.nhaarman.mockito_kotlin.mock
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import pl.elpassion.project.ProjectRepositoryProvider
import pl.elpassion.report.add.ReportAdd
import rx.Observable

class DefaultMocksRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            stubReportAddApi()
            stubProjectRepository()
            base.evaluate()
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