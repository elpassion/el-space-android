package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.TextView
import com.elpassion.android.commons.espresso.InitIntentsRule
import com.elpassion.android.commons.espresso.checkIntent
import com.elpassion.android.commons.espresso.click
import com.elpassion.android.commons.espresso.onText
import com.elpassion.android.commons.rxjavatest.thenJust
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import org.mockito.stubbing.Answer
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.ReportList
import pl.elpassion.elspace.hub.report.list.ReportListActivity
import rx.Observable

class LoginActivityGoogleSuccessTest {

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<LoginActivity> {
        LoginRepositoryProvider.override = { mock<Login.Repository>() }
        GoogleSingInControllerProvider.override =  { GoogleSuccessSingInTestController() }
        LoginHubTokenApiProvider.override = { mock<Login.HubTokenApi>().apply { whenever(loginWithGoogleToken("google token")).thenJust("token") } }
        ReportList.ServiceProvider.override = { rxMockJust(emptyList<Report>()) }
    }

    @Test
    fun shouldOpenReportListScreenWhenSignedInWithGoogle() {
        onText("Sign in").click()
        checkIntent(ReportListActivity::class.java)
    }

    class GoogleSuccessSingInTestController : GoogleSingInController {

        private var onSuccess: ((String) -> Unit)? = null

        override fun initializeGoogleSingInButton(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit): View {
            this.onSuccess = onSuccess
            return TextView(activity).apply {
                text = "Sign in"
                setOnClickListener { activity.startActivityForResult(getSignInIntent(activity), 1111) }
            }
        }

        private fun getSignInIntent(activity: FragmentActivity): Intent {
            return Intent(activity, GoogleSignInActivity::class.java)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            onSuccess?.invoke("google token")
        }

        class GoogleSignInActivity : Activity(){
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                finish()
            }
        }
    }
}

private inline fun <reified T : Any> rxMockJust(value: Any?): T {
    return mock(defaultAnswer = Answer<Any> { Observable.just(value) })
}
