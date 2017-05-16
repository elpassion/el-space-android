package pl.elpassion.elspace.hub.login

import android.app.Activity
import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.v4.app.FragmentActivity
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

class HubLoginActivityGoogleSuccessTest {

    private val hubLoginTokenApi = mock<HubLogin.TokenApi>()

    @JvmField @Rule
    val intents = InitIntentsRule()

    @JvmField @Rule
    val rule = rule<HubLoginActivity> {
        wheneverLoginWithGoogleToken().thenJust(HubTokenFromApi("token"))
        HubLoginRepositoryProvider.override = { mock<HubLogin.Repository>() }
        GoogleSingInControllerProvider.override = { GoogleSingInSuccessTestController() }
        HubLoginTokenApiProvider.override = { hubLoginTokenApi }
        ReportList.ServiceProvider.override = { rxMockJust(emptyList<Report>()) }
    }

    @Before
    fun setupTests() {
        prepareAutoFinishingIntent()
        Espresso.closeSoftKeyboard()
    }

    @Test
    fun shouldHaveGoogleSignInButton() {
        onId(R.id.hubLoginGoogleSignInButton).hasText(R.string.hub_login_button_google_sign_in)
    }

    @Test
    fun shouldOpenReportListScreenWhenSignedInWithGoogle() {
        onId(R.id.hubLoginGoogleSignInButton).click()
        checkIntent(ReportListActivity::class.java)
    }

    @Test
    fun shouldShowLoaderWhileSigningInWithGoogle() {
        wheneverLoginWithGoogleToken().thenReturn(Observable.never())
        onId(R.id.hubLoginGoogleSignInButton).click()
        onId(R.id.loader).isDisplayed()
    }

    @Test
    fun shouldHideLoaderWhenSigningInWithGoogleFinished() {
        onId(R.id.hubLoginGoogleSignInButton).click()
        onId(R.id.loader).doesNotExist()
    }

    @Test
    fun shouldShowErrorWhenGoogleAccessTokenFailed() {
        wheneverLoginWithGoogleToken().thenReturn(Observable.error(RuntimeException()))
        onId(R.id.hubLoginGoogleSignInButton).click()
        onText(R.string.google_token_error).isDisplayed()
    }

    private fun wheneverLoginWithGoogleToken() =
            whenever(hubLoginTokenApi.loginWithGoogleToken(GoogleTokenForHubTokenApi(GOOGLE_TOKEN)))

    class GoogleSingInSuccessTestController : GoogleSingInController {
        private lateinit var onSuccess: (String) -> Unit
        private lateinit var onGoogleClick: OnGoogleClick

        override fun initializeGoogleSignIn(activity: FragmentActivity, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
            this.onSuccess = onSuccess
            this.onGoogleClick = { simulateGoogleSingInActivity(activity) }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) = onSuccess(GOOGLE_TOKEN)

        override fun onGoogleSignInClick() {
            onGoogleClick()
        }

        private fun simulateGoogleSingInActivity(activity: Activity) = activity.startActivityForResult(getAutoFinishingIntent(), 1111)
    }

    companion object {
        private val GOOGLE_TOKEN = "google token"
    }
}
