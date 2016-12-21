package pl.elpassion.project.choose

import com.elpassion.android.commons.recycler.StableRecyclerViewAdapter

class ProjectRecyclerViewAdapter() : StableRecyclerViewAdapter() {
    fun updateList(projectList: List<ProjectItemAdapter>) {
        adapters.clear()
        adapters.addAll(projectList)
        notifyDataSetChanged()
    }
}