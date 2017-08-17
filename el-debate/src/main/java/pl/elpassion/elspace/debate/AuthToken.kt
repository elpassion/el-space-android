package pl.elpassion.elspace.debate

import java.io.Serializable

data class AuthToken(val token: String, val userId: String) : Serializable