package pl.elpassion.project.choose

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.ItemAdapter
import kotlinx.android.synthetic.main.project_item.view.*
import pl.elpassion.R
import pl.elpassion.project.dto.Project

class ProjectItemAdapter(val project: Project) : ItemAdapter<ProjectItemAdapter.Holder>(R.layout.project_item) {

    override fun onCreateViewHolder(itemView: View) = Holder(itemView)

    override fun onBindViewHolder(holder: Holder) {
        holder.itemView.projectName.text = project.name
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}