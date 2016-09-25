package pl.elpassion.login

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.login_activity.*
import pl.elpassion.R
import pl.elpassion.report.list.ReportListActivity

class LoginActivity : AppCompatActivity(), Login.View {

    val controller = LoginController(this, LoginRepositoryProvider.get())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        loginButton.setOnClickListener { controller.onLogin(tokenInput.text.toString()) }
    }

    override fun showEmptyLoginError() {

    }

    override fun openReportListScreen() {
        ReportListActivity.start(this)
    }

}