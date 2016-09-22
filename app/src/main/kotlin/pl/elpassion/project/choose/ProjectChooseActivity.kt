package pl.elpassion.project.choose

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.elpassion.android.commons.recycler.BaseRecyclerViewAdapter
import kotlinx.android.synthetic.main.project_choose_activity.*
import pl.elpassion.R
import pl.elpassion.project.dto.Project

class ProjectChooseActivity : AppCompatActivity(), ProjectChoose.View {

    val controller by lazy { ProjectChooseController(this, ProjectChoose.RepositoryProvider.get()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_choose_activity)
        controller.onCreate()
    }

    override fun showPossibleProjects(projects: List<Project>) {
        projectsContainer.layoutManager = LinearLayoutManager(this)
        projectsContainer.adapter = BaseRecyclerViewAdapter(projects.map { ProjectItemAdapter(it) })
    }

    override fun selectProject(project: Project) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
