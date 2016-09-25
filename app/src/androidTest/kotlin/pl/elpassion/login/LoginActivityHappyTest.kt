package pl.elpassion.login

import android.support.test.rule.ActivityTestRule
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.common.InitIntentsRule
import pl.elpassion.common.checkIntent
import pl.elpassion.report.list.ReportListActivity
import pl.elpassion.startActivity

class LoginActivityHappyTest {

    val loginRepository = mock<Login.Repository>().apply { whenever(readToken()).thenReturn("token ") }

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = object : ActivityTestRule<LoginActivity>(LoginActivity::class.java, false, false) {
        override fun beforeActivityLaunched() {
            LoginRepositoryProvider.override = { loginRepository }
        }
    }

    @Test
    fun shouldOpenReportListScreenWhenTokenIsProvided() {
        rule.startActivity()
        checkIntent(ReportListActivity::class.java)
    }

}

