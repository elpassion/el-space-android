package pl.elpassion.project.choose

import com.elpassion.android.commons.recycler.BaseRecyclerViewAdapter

class ProjectRecyclerViewAdapter() : BaseRecyclerViewAdapter() {
    fun updateList(projectList: List<ProjectItemAdapter>) {
        adapters.clear()
        adapters.addAll(projectList)
        notifyDataSetChanged()
    }
}