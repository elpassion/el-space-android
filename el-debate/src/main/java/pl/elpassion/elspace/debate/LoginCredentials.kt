package pl.elpassion.elspace.debate

import java.io.Serializable

data class LoginCredentials(val authToken: String, val userId: String) : Serializable