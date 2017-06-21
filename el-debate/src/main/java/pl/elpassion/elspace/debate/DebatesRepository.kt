package pl.elpassion.elspace.debate

import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.google.gson.Gson
import pl.elpassion.elspace.common.ContextProvider
import pl.elpassion.elspace.common.Provider

interface DebatesRepository {
    fun hasToken(debateCode: String): Boolean
    fun saveDebateToken(debateCode: String, authToken: String)
    fun getTokenForDebate(debateCode: String): String
    fun saveLatestDebateCode(debateCode: String)
    fun getLatestDebateCode(): String?
    fun saveLatestDebateNickname(nickname: String)
    fun getLatestDebateNickname(): String?
}

object DebatesRepositoryProvider : Provider<DebatesRepository>({
    object : DebatesRepository {

        private val latestDebateCode = "LATEST_DEBATE_CODE"
        private val latestDebateNickname = "LATEST_DEBATE_NICKNAME"
        private val repository = createSharedPrefs<String?>({
            PreferenceManager.getDefaultSharedPreferences(ContextProvider.get())
        }, { Gson() })

        override fun hasToken(debateCode: String) = repository.read(debateCode) != null

        override fun saveDebateToken(debateCode: String, authToken: String) = repository.write(debateCode, authToken)

        override fun getTokenForDebate(debateCode: String) = repository.read(debateCode)!!

        override fun saveLatestDebateCode(debateCode: String) = repository.write(latestDebateCode, debateCode)

        override fun getLatestDebateCode() = repository.read(latestDebateCode)

        override fun saveLatestDebateNickname(nickname: String) = repository.write(latestDebateNickname, nickname)

        override fun getLatestDebateNickname() = repository.read(latestDebateNickname)
    }
})
