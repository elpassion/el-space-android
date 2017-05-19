package pl.elpassion.elspace.debate.details

import io.reactivex.disposables.CompositeDisposable
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.addTo

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
                .subscribeOn(schedulers.subscribeOn)
                .observeOn(schedulers.observeOn)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe(view::showDebateDetails, view::showDebateDetailsError)
                .addTo(compositeDisposable)
    }

    fun onVote(token: String, answer: Answer) {
        api.vote(token, answer)
                .subscribeOn(schedulers.subscribeOn)
                .observeOn(schedulers.observeOn)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe(view::showVoteSuccess, view::showVoteError)
                .addTo(compositeDisposable)
    }

    fun onDestroy() {
        compositeDisposable.dispose()
    }
}