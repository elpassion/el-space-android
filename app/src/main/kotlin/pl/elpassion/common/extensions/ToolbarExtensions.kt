package pl.elpassion.common.extensions

import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jakewharton.rxbinding.support.v7.widget.itemClicks
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

fun Observable<MenuItem>.onMenuItemClicks(menuId: Int): Observable<Unit> = onMenuItemAction(menuId).map { Unit }

fun Observable<MenuItem>.onMenuItemAction(menuId: Int): Observable<MenuItem> = this.filter { it.itemId == menuId }

fun Toolbar.menuClicks() = itemClicks()
        .subscribeOn(AndroidSchedulers.mainThread())
        .share()