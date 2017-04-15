package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.TextView
import com.elpassion.android.commons.espresso.*
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.getAutoFinishingIntent
import pl.elpassion.elspace.common.prepareAutoFinishingIntent
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.rxMockJust
import pl.elpassion.elspace.commons.thenJust
import pl.elpassion.elspace.hub.report.Report
import pl.elpassion.elspace.hub.report.list.ReportList
import pl.elpassion.elspace.hub.report.list.ReportListActivity

class LoginActivityGoogleSuccessTest {

    private val loginHubTokenApi = mock<Login.HubTokenApi>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<LoginActivity> {
        wheneverLoginWithGoogleToken().thenJust(HubTokenFromApi("token"))
        LoginRepositoryProvider.override = { mock<Login.Repository>() }
        GoogleSingInControllerProvider.override = { GoogleSingInSuccessTestController() }
        LoginHubTokenApiProvider.override = { loginHubTokenApi }
        ReportList.ServiceProvider.override = { rxMockJust(emptyList<Report>()) }
    }

    @Before
    fun setupTests() {
        prepareAutoFinishingIntent()
        Espresso.closeSoftKeyboard()
    }

    @Test
    fun shouldOpenReportListScreenWhenSignedInWithGoogle() {
        onText(SIGN_IN_TEXT).click()
        checkIntent(ReportListActivity::class.java)
    }

    @Test
    fun shouldShowLoaderWhileSigningInWithGoogle() {
        wheneverLoginWithGoogleToken().thenReturn(Observable.never())
        onText(SIGN_IN_TEXT).click()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldHideLoaderWhenSigningInWithGoogleFinished() {
        onText(SIGN_IN_TEXT).click()
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowErrorWhenGoogleAccessTokenFailed() {
        wheneverLoginWithGoogleToken().thenReturn(Observable.error(RuntimeException()))
        onText(SIGN_IN_TEXT).click()
        onText(R.string.google_token_error).isDisplayed()
    }

    private fun wheneverLoginWithGoogleToken() =
            whenever(loginHubTokenApi.loginWithGoogleToken(GoogleTokenForHubTokenApi(GOOGLE_TOKEN)))

    class GoogleSingInSuccessTestController : GoogleSingInController {

        private lateinit var onSuccess: (String) -> Unit

        override fun initializeGoogleSingInButton(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit): View {
            this.onSuccess = onSuccess
            return TextView(activity).apply {
                text = SIGN_IN_TEXT
                setOnClickListener { simulateGoogleSingInActivity(activity) }
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) = onSuccess(GOOGLE_TOKEN)

        private fun simulateGoogleSingInActivity(activity: Activity) = activity.startActivityForResult(getAutoFinishingIntent(), 1111)
    }

    companion object {
        private val SIGN_IN_TEXT = "Sign in"
        private val GOOGLE_TOKEN = "google token"
    }
}
