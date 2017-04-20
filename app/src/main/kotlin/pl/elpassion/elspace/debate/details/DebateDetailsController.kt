package pl.elpassion.elspace.debate.details

import pl.elpassion.elspace.common.SchedulersSupplier
import rx.Subscription

class DebateDetailsController(private val api: DebateDetails.Api, private val view: DebateDetails.View, private val schedulers: SchedulersSupplier) {

    private var debateDetailsSubscription: Subscription? = null

    fun onCreate(token: String) {
        getDebateDetails(token)
    }

    fun onDebateDetailsRefresh(token: String) {
        getDebateDetails(token)
    }

    private fun getDebateDetails(token: String) {
        debateDetailsSubscription = api.getDebateDetails(token)
                .subscribeOn(schedulers.subscribeOn)
                .observeOn(schedulers.observeOn)
                .doOnSubscribe(view::showLoader)
                .doOnUnsubscribe(view::hideLoader)
                .subscribe(view::showDebateDetails, view::showError)
    }

    fun onVote(token: String, answer: Answer) {
        api.sendAnswer(token, answer)
                .subscribeOn(schedulers.subscribeOn)
                .observeOn(schedulers.observeOn)
                .doOnSubscribe(view::showLoader)
                .doOnUnsubscribe(view::hideLoader)
                .subscribe()
    }

    fun onDestroy() {
        debateDetailsSubscription?.unsubscribe()
    }
}