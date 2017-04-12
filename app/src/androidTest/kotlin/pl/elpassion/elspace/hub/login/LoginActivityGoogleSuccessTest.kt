package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.TextView
import com.elpassion.android.commons.espresso.*
import com.elpassion.android.commons.rxjavatest.thenJust
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.getAutoFinishingIntent
import pl.elpassion.elspace.common.prepareAutoFinishingIntent
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.rxMockJust
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.ReportList
import pl.elpassion.elspace.hub.report.list.ReportListActivity
import rx.Observable

class LoginActivityGoogleSuccessTest {

    private val loginHubTokenApi = mock<Login.HubTokenApi>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<LoginActivity> {
        wheneverLoginWithGoogleToken().thenJust(HubTokenFromApi("token"))
        LoginRepositoryProvider.override = { mock<Login.Repository>() }
        GoogleSingInControllerProvider.override = { GoogleSuccessSingInTestController() }
        LoginHubTokenApiProvider.override = { loginHubTokenApi }
        ReportList.ServiceProvider.override = { rxMockJust(emptyList<Report>()) }
    }

    @Test
    fun shouldOpenReportListScreenWhenSignedInWithGoogle() {
        wheneverLoginWithGoogleToken().thenJust(HubTokenFromApi("token"))
        prepareAutoFinishingIntent()
        Espresso.closeSoftKeyboard()
        onText(SIGN_IN_TEXT).click()
        checkIntent(ReportListActivity::class.java)
    }

    @Test
    fun shouldShowLoaderWhileSigningInWithGoogle() {
        wheneverLoginWithGoogleToken().thenReturn(Observable.never())
        prepareAutoFinishingIntent()
        Espresso.closeSoftKeyboard()
        onText(SIGN_IN_TEXT).click()
        onId(R.id.loader).isDisplayed()
    }

    private fun wheneverLoginWithGoogleToken() =
            whenever(loginHubTokenApi.loginWithGoogleToken(GoogleTokenForHubTokenApi(GOOGLE_TOKEN)))

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
