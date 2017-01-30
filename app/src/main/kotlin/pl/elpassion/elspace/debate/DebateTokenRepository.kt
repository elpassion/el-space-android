package pl.elpassion.elspace.debate

interface DebateTokenRepository {
    fun hasToken(debateCode: String): Boolean
    fun saveDebateToken(debateCode: String, authToken: String)
    fun getTokenForDebate(debateCode: String): String
}