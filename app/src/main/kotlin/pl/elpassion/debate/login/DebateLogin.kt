package pl.elpassion.debate.login

import rx.Observable

interface DebateLogin {
    interface View {
        fun showLogToPreviousDebateView()
        fun openDebateScreen()
        fun showLoginFailedError()
        fun showLoader()
        fun hideLoader()
    }

    interface Api {
        fun login(code: String): Observable<LoginResponse>
        data class LoginResponse(val authToken: String)
    }

}