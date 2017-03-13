package pl.elpassion.elspace.api

import rx.Completable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

fun Completable.applySchedulers(): Completable =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
