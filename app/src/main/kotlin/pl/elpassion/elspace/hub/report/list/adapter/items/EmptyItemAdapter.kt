package pl.elpassion.elspace.hub.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_ID
import android.view.View
import com.elpassion.android.commons.recycler.components.stable.StableItemAdapter
import pl.elpassion.R

class EmptyItemAdapter() : StableItemAdapter<EmptyItemAdapter.VH>(NO_ID, R.layout.empty_adapter_item) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}