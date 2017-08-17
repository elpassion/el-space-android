package pl.elpassion.elspace.debate

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.gsonadapter.gsonConverterAdapter
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.debate.chat.TokenCredentials

interface DebatesRepository {
    fun hasToken(debateCode: String): Boolean
    fun saveDebateToken(debateCode: String, authToken: AuthToken)
    fun getTokenForDebate(debateCode: String): AuthToken
    fun saveLatestDebateCode(debateCode: String)
    fun getLatestDebateCode(): String?
    fun areCredentialsMissing(token: String): Boolean
    fun saveTokenCredentials(token: String, credentials: TokenCredentials)
    fun getTokenCredentials(token: String): TokenCredentials
}

object DebatesRepositoryProvider : Provider<DebatesRepository>({
    object : DebatesRepository {

        private val latestDebateCode = "LATEST_DEBATE_CODE"

        private val defaultSharedPreferences = { PreferenceManager.getDefaultSharedPreferences(ContextProvider.get()) }

        private val authTokenRepository = createSharedPrefs<AuthToken>(defaultSharedPreferences, gsonConverterAdapter())

        private val authTokenCredentialsRepository = createSharedPrefs<TokenCredentials>(defaultSharedPreferences, gsonConverterAdapter())

        private val latestDebateCodeRepository = createSharedPrefs<String>(defaultSharedPreferences, gsonConverterAdapter())

        override fun hasToken(debateCode: String) = authTokenRepository.read(debateCode) != null

        override fun saveDebateToken(debateCode: String, authToken: AuthToken) = authTokenRepository.write(debateCode, authToken)

        override fun getTokenForDebate(debateCode: String) = authTokenRepository.read(debateCode)!!

        override fun saveLatestDebateCode(debateCode: String) = latestDebateCodeRepository.write(latestDebateCode, debateCode)

        override fun getLatestDebateCode() = latestDebateCodeRepository.read(latestDebateCode)

        override fun areCredentialsMissing(token: String): Boolean = authTokenCredentialsRepository.read(token) == null

        override fun saveTokenCredentials(token: String, credentials: TokenCredentials) = authTokenCredentialsRepository.write(token, credentials)

        override fun getTokenCredentials(token: String) = authTokenCredentialsRepository.read(token)!!

    }
})
