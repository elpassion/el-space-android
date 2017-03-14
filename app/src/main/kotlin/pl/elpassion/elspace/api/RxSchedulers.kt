package pl.elpassion.elspace.api

import rx.Completable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

@Deprecated(
        message = "Schedulers schould be passed as a clear dependency",
        replaceWith = ReplaceWith("subscribeOn(schedulers.subscribeOn).observeOn(schedulers.observeOn)"))
fun Completable.applySchedulers(): Completable =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
