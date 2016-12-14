package pl.elpassion.project.choose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.project_choose_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.project.Project
import pl.elpassion.project.CachedProjectRepositoryProvider


class ProjectChooseActivity : AppCompatActivity(), ProjectChoose.View {

    private val controller by lazy { ProjectChooseController(this, CachedProjectRepositoryProvider.get()) }
    private val projectListAdapter by lazy { ProjectRecyclerViewAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_choose_activity)
        showBackArrowOnActionBar()
        initRecyclerView()
        controller.onCreate()
    }

    private fun initRecyclerView() {
        projectsContainer.layoutManager = LinearLayoutManager(this)
        projectsContainer.adapter = projectListAdapter
    }

    override fun showPossibleProjects(projects: List<Project>) {
        updateAdapterList(projects)
    }

    override fun showFilteredProjects(projects: List<Project>) {
        updateAdapterList(projects)
    }

    private fun updateAdapterList(projects: List<Project>) {
        projectListAdapter.updateList(projects.map {
            ProjectItemAdapter(it) { controller.onProjectClicked(it) }
        })
    }

    override fun selectProject(project: Project) {
        setResult(Activity.RESULT_OK, Intent().putExtra(SELECTED_PROJECT, project))
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            return true
        } else {
            return handleClickOnBackArrowItem(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.project_choose_menu, menu)
        initSearchView(menu)
        return true
    }

    private fun initSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        val textChangeListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                controller.searchQuery(query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                controller.searchQuery(query)
                return true
            }

        }
        searchView.setOnQueryTextListener(textChangeListener)
    }

    companion object {
        private val SELECTED_PROJECT = "selected_project"
        fun startForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(Intent(activity, ProjectChooseActivity::class.java), requestCode)
        }

        fun getProject(data: Intent): Project = data.getSerializableExtra(ProjectChooseActivity.SELECTED_PROJECT) as Project
    }
}
