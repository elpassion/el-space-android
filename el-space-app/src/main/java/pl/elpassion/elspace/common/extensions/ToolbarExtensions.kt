package pl.elpassion.elspace.common.extensions

import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jakewharton.rxbinding2.support.v7.widget.itemClicks
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers

fun Observable<MenuItem>.onMenuItemClicks(menuId: Int): Observable<Unit> = onMenuItemAction(menuId).map { Unit }

fun Observable<MenuItem>.onMenuItemAction(menuId: Int): Observable<MenuItem> = this.filter { it.itemId == menuId }

fun Toolbar.menuClicks(): Observable<MenuItem> = itemClicks()
        .subscribeOn(AndroidSchedulers.mainThread())
        .share()