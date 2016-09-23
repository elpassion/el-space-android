package pl.elpassion.report.add

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.R
import pl.elpassion.project.choose.ProjectChooseActivity
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepositoryProvider

class ReportAddActivity : AppCompatActivity(), ReportAdd.View {

    val controller by lazy { ReportAddController(this, ProjectRepositoryProvider.get(), ReportAdd.ApiProvider.get()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_add_activity)
        controller.onCreate()
        reportAddDate.text = intent.getStringExtra(ADD_DATE_KEY)
        reportAddProjectName.setOnClickListener { controller.onProjectClicked() }
        reportAddHours.setOnFocusChangeListener { view, b -> if (b) reportAddHours.setText("") }
        reportAddAdd.setOnClickListener { controller.onReportAdd(
                reportAddDate.text.toString(),
                reportAddDescription.text.toString()
        ) }
    }

    override fun showSelectedProject(project: Project) {
        reportAddProjectName.text = project.name
    }

    override fun openProjectChooser() {
        ProjectChooseActivity.startForResult(this, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            controller.onSelectProject(data!!.getSerializableExtra(ProjectChooseActivity.SELECTED_PROJECT) as Project)
        }
    }

    override fun close() {
        finish()
    }

    override fun showError() {

    }

    companion object {
        private val REQUEST_CODE = 10001
        private val ADD_DATE_KEY = "dateKey"

        fun start(context: Context, date: String) {
            context.startActivity(intent(context, date))
        }

        fun intent(context: Context, date: String) = Intent(context, ReportAddActivity::class.java).apply { putExtra(ADD_DATE_KEY, date) }
    }
}