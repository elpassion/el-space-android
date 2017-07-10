package pl.elpassion.elspace.debate.details

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import pl.elpassion.elspace.common.SchedulersSupplier
import retrofit2.HttpException

class DebateDetailsController(
        private val api: DebateDetails.Api,
        private val view: DebateDetails.View,
        private val schedulers: SchedulersSupplier) {

    private val compositeDisposable = CompositeDisposable()

    fun onCreate(token: String) {
        getDebateDetails(token)
    }

    fun onDebateDetailsRefresh(token: String) {
        getDebateDetails(token)
    }

    private fun getDebateDetails(token: String) {
        api.getDebateDetails(token)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe({ view.showDebateDetails(it) }, onDebateDetailsError)
                .addTo(compositeDisposable)
    }

    private val onDebateDetailsError: (Throwable) -> Unit = { error ->
        if (error is HttpException && error.code() == 406) {
            view.showDebateClosedError(error)
        } else {
            view.showDebateDetailsError(error)
        }
    }

    fun onVote(token: String, answer: Answer) {
        api.vote(token, answer)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showVoteLoader(answer) }
                .doFinally(view::hideVoteLoader)
                .subscribe({ view.showVoteSuccess(answer) }, onVoteError)
                .addTo(compositeDisposable)
    }

    private val onVoteError: (Throwable) -> Unit = { error ->
        if (error is HttpException && error.code() == 429) {
            view.showSlowDownInformation()
        } else {
            view.showVoteError(error)
        }
    }

    fun onComment() {
        view.openCommentScreen()
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }
}