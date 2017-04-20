package pl.elpassion.elspace.debate.details

import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.addTo
import rx.subscriptions.CompositeSubscription

class DebateDetailsController(private val api: DebateDetails.Api, private val view: DebateDetails.View, private val schedulers: SchedulersSupplier) {

    private val compositeSubscription = CompositeSubscription()

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
                .doOnSubscribe(view::showLoader)
                .doOnUnsubscribe(view::hideLoader)
                .subscribe(view::showDebateDetails, view::showError)
                .addTo(compositeSubscription)
    }

    fun onVote(token: String, answer: Answer) {
        api.vote(token, answer)
                .subscribeOn(schedulers.subscribeOn)
                .observeOn(schedulers.observeOn)
                .doOnSubscribe(view::showLoader)
                .doOnUnsubscribe(view::hideLoader)
                .subscribe { view.showVoteSuccess() }
                .addTo(compositeSubscription)
    }

    fun onDestroy() {
        compositeSubscription.unsubscribe()
    }
}