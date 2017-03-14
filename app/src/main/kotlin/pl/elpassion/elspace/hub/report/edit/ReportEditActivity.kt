package pl.elpassion.elspace.hub.report.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.R

class ReportEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_edit_activity)
        setSupportActionBar(toolbar)
    }

    companion object {

        fun startForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(intent(activity), requestCode)
        }

        fun intent(context: Context) = Intent(context, ReportEditActivity::class.java)
    }
}