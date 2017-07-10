package pl.elpassion.elspace.hub.login

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.gsonadapter.GsonConverterAdapter
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.common.Provider

object GoogleHubLoginRepositoryProvider : Provider<GoogleHubLogin.Repository>({
    object : GoogleHubLogin.Repository {
        private val TOKEN_KEY = "tokenKey"
        private val defaultSharedPreferences = { PreferenceManager.getDefaultSharedPreferences(ContextProvider.get()) }
        private val repository = createSharedPrefs<String?>(defaultSharedPreferences, GsonConverterAdapter())

        override fun saveToken(token: String) = repository.write(TOKEN_KEY, token)

        override fun readToken() = repository.read(TOKEN_KEY)

    }
})
