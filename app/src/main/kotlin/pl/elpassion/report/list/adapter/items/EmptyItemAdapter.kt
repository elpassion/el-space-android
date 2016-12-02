package pl.elpassion.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.elpassion.android.commons.recycler.ItemAdapter
import pl.elpassion.R

class EmptyItemAdapter() : ItemAdapter<EmptyItemAdapter.VH>(R.layout.empty_adapter_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}