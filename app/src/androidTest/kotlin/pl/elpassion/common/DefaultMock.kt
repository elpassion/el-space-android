package pl.elpassion.common

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import pl.elpassion.report.add.ReportAdd
import rx.Observable

class DeaultMocksRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            stubReportAddApi()
            base.evaluate()
        }
    }
}


fun stubReportAddApi() {
    ReportAdd.ApiProvider.override = {
        object : ReportAdd.Api {
            override fun addReport() = Observable.just(Unit)
        }
    }
}