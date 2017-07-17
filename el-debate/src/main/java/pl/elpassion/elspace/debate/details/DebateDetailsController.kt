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
                .subscribe(view::showDebateDetails, this::onDebateDetailsError)
                .addTo(compositeDisposable)
    }

    private fun onDebateDetailsError(error: Throwable) {
        if (error is HttpException) {
            onHttpException(error)
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
                .subscribe({ view.showVoteSuccess(answer) }, this::onVoteError)
                .addTo(compositeDisposable)
    }

    private fun onVoteError(error: Throwable) {
        if (error is HttpException) {
            onHttpException(error)
        } else {
            view.showVoteError(error)
        }
    }

    private fun onHttpException(error: HttpException) {
        when {
            error.code() == 406 -> view.showDebateClosedError()
            error.code() == 429 -> view.showSlowDownInformation()
        }
    }

    fun onComment() {
        view.openCommentScreen()
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }
}