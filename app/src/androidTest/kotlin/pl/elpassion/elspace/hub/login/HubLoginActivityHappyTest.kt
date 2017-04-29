package pl.elpassion.elspace.hub.login

import com.elpassion.android.commons.espresso.InitIntentsRule
import com.elpassion.android.commons.espresso.checkIntent
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity
import pl.elpassion.elspace.hub.report.list.ReportList
import pl.elpassion.elspace.hub.report.list.ReportListActivity
import io.reactivex.Observable

class HubLoginActivityHappyTest {

    val loginRepository = mock<HubLogin.Repository>().apply { whenever(readToken()).thenReturn("token ") }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<HubLoginActivity>(autoStart = false) {
        ReportList.ServiceProvider.override = { mock<ReportList.Service>().apply { whenever(getReports(any())).thenReturn(Observable.just(emptyList())) } }
        HubLoginRepositoryProvider.override = { loginRepository }
    }

    @Test
    fun shouldOpenReportListScreenWhenTokenIsProvided() {
        rule.startActivity()
        checkIntent(ReportListActivity::class.java)
    }

}

