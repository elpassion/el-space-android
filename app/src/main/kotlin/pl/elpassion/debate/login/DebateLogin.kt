package pl.elpassion.debate.login

import rx.Observable

interface DebateLogin {
    interface View {
        fun openDebateScreen(authToken: String)
        fun showLoginFailedError()
        fun showLoader()
        fun hideLoader()
        fun showWrongPinError()
    }

    interface Api {
        fun login(code: String): Observable<LoginResponse>
        data class LoginResponse(val authToken: String)
    }

}