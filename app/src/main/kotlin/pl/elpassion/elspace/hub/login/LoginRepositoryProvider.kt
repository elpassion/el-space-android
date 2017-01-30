package pl.elpassion.elspace.hub.login

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.google.gson.Gson
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.common.Provider

object LoginRepositoryProvider : Provider<Login.Repository>({
    object : Login.Repository {
        private val TOKEN_KEY = "tokenKey"
        private val repository = createSharedPrefs<String?>({
            PreferenceManager.getDefaultSharedPreferences(ContextProvider.get())
        }, { Gson() })

        override fun saveToken(token: String) = repository.write(TOKEN_KEY, token)

        override fun readToken() = repository.read(TOKEN_KEY)

    }
})