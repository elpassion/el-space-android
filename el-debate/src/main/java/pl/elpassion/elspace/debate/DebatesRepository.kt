package pl.elpassion.elspace.debate

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.gsonadapter.gsonConverterAdapter
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.debate.chat.model.TokenCredentials

interface DebatesRepository {
    fun hasLoginCredentials(debateCode: String): Boolean
    fun saveLoginCredentials(debateCode: String, loginCredentials: LoginCredentials)
    fun getLoginCredentialsForDebate(debateCode: String): LoginCredentials
    fun saveLatestDebateCode(debateCode: String)
    fun getLatestDebateCode(): String?
    fun areTokenCredentialsMissing(token: String): Boolean
    fun saveTokenCredentials(token: String, credentials: TokenCredentials)
    fun getTokenCredentials(token: String): TokenCredentials
}

object DebatesRepositoryProvider : Provider<DebatesRepository>({
    object : DebatesRepository {

        private val latestDebateCode = "LATEST_DEBATE_CODE"

        private val defaultSharedPreferences = { PreferenceManager.getDefaultSharedPreferences(ContextProvider.get()) }

        private val loginCredentialsRepository = createSharedPrefs<LoginCredentials>(defaultSharedPreferences, gsonConverterAdapter())

        private val authTokenCredentialsRepository = createSharedPrefs<TokenCredentials>(defaultSharedPreferences, gsonConverterAdapter())

        private val latestDebateCodeRepository = createSharedPrefs<String>(defaultSharedPreferences, gsonConverterAdapter())

        override fun hasLoginCredentials(debateCode: String) = loginCredentialsRepository.read(debateCode) != null

        override fun saveLoginCredentials(debateCode: String, loginCredentials: LoginCredentials) = loginCredentialsRepository.write(debateCode, loginCredentials)

        override fun getLoginCredentialsForDebate(debateCode: String) = loginCredentialsRepository.read(debateCode)!!

        override fun saveLatestDebateCode(debateCode: String) = latestDebateCodeRepository.write(latestDebateCode, debateCode)

        override fun getLatestDebateCode() = latestDebateCodeRepository.read(latestDebateCode)

        override fun areTokenCredentialsMissing(token: String): Boolean = authTokenCredentialsRepository.read(token) == null

        override fun saveTokenCredentials(token: String, credentials: TokenCredentials) = authTokenCredentialsRepository.write(token, credentials)

        override fun getTokenCredentials(token: String) = authTokenCredentialsRepository.read(token)!!

    }
})
