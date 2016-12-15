package pl.elpassion.debate

interface DebateTokenRepository {
    fun hasToken(): Boolean
    fun saveToken(authToken: String)
}