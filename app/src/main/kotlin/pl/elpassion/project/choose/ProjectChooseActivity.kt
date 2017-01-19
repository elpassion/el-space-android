package pl.elpassion.project.choose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat.getActionView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.jakewharton.rxbinding.support.v7.widget.queryTextChanges
import kotlinx.android.synthetic.main.project_choose_activity.*
import pl.elpassion.R
import pl.elpassion.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.common.extensions.showBackArrowOnActionBar
import pl.elpassion.common.hideLoader
import pl.elpassion.common.showLoader
import pl.elpassion.project.Project


class ProjectChooseActivity : AppCompatActivity(), ProjectChoose.View {

    private val controller by lazy { ProjectChooseController(this, ProjectRepositoryProvider.get()) }
    private val projectListAdapter by lazy { ProjectRecyclerViewAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_choose_activity)
        showBackArrowOnActionBar()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        projectsContainer.layoutManager = LinearLayoutManager(this)
        projectsContainer.adapter = projectListAdapter
    }

    override fun showPossibleProjects(projects: List<Project>) {
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
        val searchView = menu.getSearchView()
        controller.onCreate(searchView.queryTextChanges())
        return true
    }

    private fun Menu.getSearchView(): SearchView {
        val searchItem = findItem(R.id.action_search)
        return getActionView(searchItem) as SearchView
    }

    override fun showError(ex: Throwable) {
        Crashlytics.logException(ex)
        Snackbar.make(projectsCoordinator, R.string.internet_connection_error, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun hideLoader() = hideLoader(projectsCoordinator)

    override fun showLoader() = showLoader(projectsCoordinator)

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }

    companion object {
        private val SELECTED_PROJECT = "selected_project"

        fun startForResult(activity: Activity, requestCode: Int) {
            activity.startActivityForResult(Intent(activity, ProjectChooseActivity::class.java), requestCode)
        }

        fun getProject(data: Intent): Project = data.getSerializableExtra(ProjectChooseActivity.SELECTED_PROJECT) as Project
    }
}
