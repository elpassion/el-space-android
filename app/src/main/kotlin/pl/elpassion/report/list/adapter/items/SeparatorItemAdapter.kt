package pl.elpassion.report.list.adapter.items

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_ID
import android.view.View
import com.elpassion.android.commons.recycler.StableItemAdapter
import pl.elpassion.R

class SeparatorItemAdapter() : StableItemAdapter<SeparatorItemAdapter.VH>(NO_ID, R.layout.hub_separator) {
    override fun onCreateViewHolder(itemView: View) = VH(itemView)

    override fun onBindViewHolder(holder: VH) {
    }

    class VH(view: View) : RecyclerView.ViewHolder(view)

}