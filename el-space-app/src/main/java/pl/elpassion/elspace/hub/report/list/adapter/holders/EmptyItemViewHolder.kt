package pl.elpassion.elspace.hub.report.list.adapter.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import pl.elpassion.elspace.R
import pl.elpassion.elspace.hub.report.list.AdapterItem

class EmptyItemViewHolder(itemView: View) : ViewHolderBinder<AdapterItem>(itemView) {

    override fun bind(item: AdapterItem) {}

    companion object {
        operator fun invoke() =
                R.layout.empty_adapter_item to ::EmptyItemViewHolder
    }
}