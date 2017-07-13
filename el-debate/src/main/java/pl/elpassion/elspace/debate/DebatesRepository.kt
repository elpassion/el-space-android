package pl.elpassion.elspace.debate

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.elpassion.sharedpreferences.gsonadapter.gsonConverterAdapter
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.common.Provider

interface DebatesRepository {
    fun hasToken(debateCode: String): Boolean
    fun saveDebateToken(debateCode: String, authToken: String)
    fun getTokenForDebate(debateCode: String): String
    fun saveLatestDebateCode(debateCode: String)
    fun getLatestDebateCode(): String?
    fun getLatestDebateNickname(): String?
    fun areCredentialsMissing(token: String): Boolean
}

object DebatesRepositoryProvider : Provider<DebatesRepository>({
    object : DebatesRepository {

        private val latestDebateCode = "LATEST_DEBATE_CODE"
        private val latestDebateNickname = "LATEST_DEBATE_NICKNAME"
        private val defaultSharedPreferences = { PreferenceManager.getDefaultSharedPreferences(ContextProvider.get()) }
        private val repository = createSharedPrefs<String?>(defaultSharedPreferences, gsonConverterAdapter())

        override fun hasToken(debateCode: String) = repository.read(debateCode) != null

        override fun saveDebateToken(debateCode: String, authToken: String) = repository.write(debateCode, authToken)

        override fun getTokenForDebate(debateCode: String) = repository.read(debateCode)!!

        override fun saveLatestDebateCode(debateCode: String) = repository.write(latestDebateCode, debateCode)

        override fun getLatestDebateCode() = repository.read(latestDebateCode)

        override fun getLatestDebateNickname() = repository.read(latestDebateNickname)

        override fun areCredentialsMissing(token: String): Boolean = true
    }
})
