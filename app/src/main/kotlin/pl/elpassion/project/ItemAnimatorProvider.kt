package pl.elpassion.project

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import pl.elpassion.common.Provider

object ItemAnimatorProvider : Provider<RecyclerView.ItemAnimator>({
    DefaultItemAnimator()
})