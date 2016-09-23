package pl.elpassion.common

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

object SchedulersTransformerProvider : Provider<SchedulersTransformerProvider.SchedulersTransformer>({
    InstantSchedulersTransformer
}) {

    interface SchedulersTransformer {
        fun <T> getTransformer(): Observable.Transformer<T, T>
    }

    object InstantSchedulersTransformer : SchedulersTransformer {
        override fun <T> getTransformer(): Observable.Transformer<T, T> {
            return Observable.Transformer<T, T> { it }
        }
    }

    object AndroidSchedulersTransformer : SchedulersTransformer {
        override fun <T> getTransformer(): Observable.Transformer<T, T> {
            return Observable.Transformer<T, T> {
                it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}