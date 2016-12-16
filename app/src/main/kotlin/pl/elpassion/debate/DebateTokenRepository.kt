package pl.elpassion.debate

interface DebateTokenRepository {
    fun hasToken(): Boolean
    fun saveDebateToken(debateCode: String, authToken: String)
}