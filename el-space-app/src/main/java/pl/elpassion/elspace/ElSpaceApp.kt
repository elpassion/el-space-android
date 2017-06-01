package pl.elpassion.elspace

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.hub.UnauthenticatedRetrofitProvider
import pl.elpassion.elspace.hub.login.GoogleSingInDI
import pl.elpassion.elspace.hub.login.InstantGoogleHubLoginRepositoryProvider
import pl.elpassion.elspace.hub.login.instant.InstantGoogleHubLogin
import pl.elpassion.elspace.hub.login.instant.InstantGoogleHubLoginActivity
import pl.elpassion.elspace.hub.report.list.ReportListActivity

class ElSpaceApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics.Builder().core(createCrashlyticsCore()).build())
        ContextProvider.override = { applicationContext }
        setupHubLoginActivity()
    }

    private fun createCrashlyticsCore() = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()

    private fun setupHubLoginActivity() {
        InstantGoogleHubLoginActivity.provideApi = { UnauthenticatedRetrofitProvider.get().create(InstantGoogleHubLogin.Api::class.java) }
        InstantGoogleHubLoginActivity.provideRepository = { InstantGoogleHubLoginRepositoryProvider.get() }
        InstantGoogleHubLoginActivity.openOnLoggedInScreen = { ReportListActivity.start(it) }
        InstantGoogleHubLoginActivity.startGoogleSignInActivity = GoogleSingInDI.startGoogleSignInActivity
        InstantGoogleHubLoginActivity.getHubGoogleSignInResult = GoogleSingInDI.getHubGoogleSignInResult
        InstantGoogleHubLoginActivity.logoutFromGoogle = GoogleSingInDI.logoutFromGoogle
    }
}