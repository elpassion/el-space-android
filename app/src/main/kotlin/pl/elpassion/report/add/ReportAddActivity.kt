package pl.elpassion.report.add

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.report_add_activity.*
import pl.elpassion.R
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepositoryProvider

class ReportAddActivity : AppCompatActivity(), ReportAdd.View {

    val controller by lazy { ReportAddController(this, ProjectRepositoryProvider.get()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.report_add_activity)
        controller.onCreate()
    }

    override fun showSelectedProject(projects: Project) {
        reportAddProjectName.text = projects.name
    }
}