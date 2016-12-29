package pl.elpassion.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.login_activity.*
import pl.elpassion.R
import pl.elpassion.login.shortcut.ShortcutServiceImpl
import pl.elpassion.report.list.ReportListActivity


class LoginActivity : AppCompatActivity(), Login.View {

    private val controller = LoginController(this, LoginRepositoryProvider.get(), ShortcutServiceImpl(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        loginButton.setOnClickListener { controller.onLogin(tokenInput.text.toString()) }
        controller.onCreate()
    }

    override fun showEmptyLoginError() {
        emptyTokenError.visibility = VISIBLE
    }

    override fun openReportListScreen() {
        ReportListActivity.start(this)
        finish()
    }

}