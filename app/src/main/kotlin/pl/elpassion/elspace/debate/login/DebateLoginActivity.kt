package pl.elpassion.elspace.debate.login

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.debate_login_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.debate.DebateTokenRepositoryProvider
import pl.elpassion.elspace.debate.details.DebateScreen
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class DebateLoginActivity : AppCompatActivity(), DebateLogin.View {

    private val controller by lazy {
        DebateLoginController(this, DebateTokenRepositoryProvider.get(), object : DebateLogin.Api {
            override fun login(code: String): Observable<DebateLogin.Api.LoginResponse> {
                showLoader()
                return Observable.never()
            }
        }, SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_login_activity)
        loginButton.setOnClickListener {
            controller.onLogToDebate(debateCode.text.toString())
        }
    }

    override fun openDebateScreen(authToken: String) {
        DebateScreen.start(this, authToken)
    }

    override fun showLoginFailedError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showLoader() {
        showLoader(debateLoginCoordinator)
    }

    override fun hideLoader() {
        hideLoader(debateLoginCoordinator)
    }

    override fun showWrongPinError() {
        Snackbar.make(debateLoginCoordinator, R.string.debate_code_incorrect, Snackbar.LENGTH_INDEFINITE).show()
    }
}