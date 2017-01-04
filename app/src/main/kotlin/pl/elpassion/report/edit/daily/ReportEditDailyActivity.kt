package pl.elpassion.report.edit.daily

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.elpassion.R
import pl.elpassion.common.extensions.showBackArrowOnActionBar

class ReportEditDailyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.daily_report_edit_activity)
        showBackArrowOnActionBar()
    }
}