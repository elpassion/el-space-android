package pl.elpassion.elspace.hub.project.choose

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.MenuItemCompat.getActionView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.crashlytics.android.Crashlytics
import com.elpassion.android.commons.recycler.adapters.basicAdapterWithLayoutAndBinder
import com.jakewharton.rxbinding2.support.v7.widget.queryTextChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.project_choose_activity.*
import kotlinx.android.synthetic.main.project_item.view.*
import pl.elpassion.elspace.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.hub.project.Project

class ProjectChooseActivity : AppCompatActivity(), ProjectChoose.View {

    private val controller by lazy { ProjectChooseController(this, ProjectRepositoryProvider.get(), SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread())) }
    private var projects = mutableListOf<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_choose_activity)
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        projectsContainer.layoutManager = LinearLayoutManager(this)
        projectsContainer.adapter = basicAdapterWithLayoutAndBinder(projects, R.layout.project_item) { holder, item ->
            holder.itemView.projectName.text = item.name
            holder.itemView.setOnClickListener { controller.onProjectClicked(item) }
        }
    }

    override fun showProjects(projects: List<Project>) {
        this.projects.clear()
        this.projects.addAll(projects)
        projectsContainer.adapter.notifyDataSetChanged()
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
