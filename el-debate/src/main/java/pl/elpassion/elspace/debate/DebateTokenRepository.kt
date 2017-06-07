package pl.elpassion.elspace.debate

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.google.gson.Gson
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.common.Provider

interface DebateTokenRepository {
    fun hasToken(debateCode: String): Boolean
    fun saveDebateToken(debateCode: String, authToken: String)
    fun getTokenForDebate(debateCode: String): String
    fun saveLatestDebateCode(debateCode: String)
}

object DebateTokenRepositoryProvider : Provider<DebateTokenRepository>({
    object : DebateTokenRepository {

        private val repository = createSharedPrefs<String?>({
            PreferenceManager.getDefaultSharedPreferences(ContextProvider.get())
        }, { Gson() })

        override fun hasToken(debateCode: String) = repository.read(debateCode) != null

        override fun saveDebateToken(debateCode: String, authToken: String) = repository.write(debateCode, authToken)

        override fun getTokenForDebate(debateCode: String) = repository.read(debateCode)!!

        override fun saveLatestDebateCode(debateCode: String) = Unit
    }
})
