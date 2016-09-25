package pl.elpassion.project.choose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.project_choose_activity.*
import pl.elpassion.R
import pl.elpassion.project.common.Project
import pl.elpassion.project.common.ProjectRepositoryProvider

class ProjectChooseActivity : AppCompatActivity(), ProjectChoose.View {

    val controller by lazy { ProjectChooseController(this, ProjectRepositoryProvider.get()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_choose_activity)
        controller.onCreate()
    }

    override fun showPossibleProjects(projects: List<Project>) {
        projectsContainer.layoutManager = LinearLayoutManager(this)
        projectsContainer.adapter = BaseRecyclerViewAdapter(projects.sortedBy { it.name }.map {
            ProjectItemAdapter(it, {
                controller.onProjectClicked(it)
            })
        })
    }

    override fun selectProject(project: Project) {
        setResult(Activity.RESULT_OK, Intent().putExtra(SELECTED_PROJECT, project))
        finish()
    }

    companion object {
        val SELECTED_PROJECT = "selected_project"
        fun startForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(Intent(activity, ProjectChooseActivity::class.java), requestCode)
        }
    }
}
