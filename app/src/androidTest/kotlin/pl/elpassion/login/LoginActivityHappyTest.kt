package pl.elpassion.login

import com.elpassion.android.commons.espresso.InitIntentsRule
import com.elpassion.android.commons.espresso.checkIntent
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.common.rule
import pl.elpassion.report.list.ReportList
import pl.elpassion.report.list.ReportListActivity
import pl.elpassion.startActivity
import rx.Observable

class LoginActivityHappyTest {

    val loginRepository = mock<Login.Repository>().apply { whenever(readToken()).thenReturn("token ") }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<LoginActivity>(autoStart = false) {
        ReportList.ServiceProvider.override = { mock<ReportList.Service>().apply { whenever(getReports()).thenReturn(Observable.just(emptyList())) } }
        LoginRepositoryProvider.override = { loginRepository }
    }

    @Test
    fun shouldOpenReportListScreenWhenTokenIsProvided() {
        rule.startActivity()
        checkIntent(ReportListActivity::class.java)
    }

}

