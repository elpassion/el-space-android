package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Intent
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
import pl.elpassion.elspace.common.getAutoFinishingIntent
import pl.elpassion.elspace.common.prepareAutoFinishingIntent
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.rxMockJust
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.ReportList
import pl.elpassion.elspace.hub.report.list.ReportListActivity

class LoginActivityGoogleSuccessTest {

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<LoginActivity> {
        LoginRepositoryProvider.override = { mock<Login.Repository>() }
        GoogleSingInControllerProvider.override = { GoogleSuccessSingInTestController() }
        LoginHubTokenApiProvider.override = { mock<Login.HubTokenApi>().apply { whenever(loginWithGoogleToken(GOOGLE_TOKEN)).thenJust("token") } }
        ReportList.ServiceProvider.override = { rxMockJust(emptyList<Report>()) }
    }

    @Test
    fun shouldOpenReportListScreenWhenSignedInWithGoogle() {
        prepareAutoFinishingIntent()
        onText(SIGN_IN_TEXT).click()
        checkIntent(ReportListActivity::class.java)
    }

    class GoogleSuccessSingInTestController : GoogleSingInController {

        private lateinit var onSuccess: (String) -> Unit

        override fun initializeGoogleSingInButton(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit): View {
            this.onSuccess = onSuccess
            return TextView(activity).apply {
                text = SIGN_IN_TEXT
                setOnClickListener { simulateGoogleSingInActivity(activity) }
            }
        }

        private fun simulateGoogleSingInActivity(activity: Activity) {
            activity.startActivityForResult(getAutoFinishingIntent(), 1111)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            onSuccess(GOOGLE_TOKEN)
        }
    }

    companion object {
        private val SIGN_IN_TEXT = "Sign in"
        private val GOOGLE_TOKEN = "google token"
    }
}
