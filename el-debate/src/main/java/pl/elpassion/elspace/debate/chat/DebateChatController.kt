package pl.elpassion.elspace.debate.chat

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebatesRepository

class DebateChatController(
        private val view: DebateChat.View,
        private val debateRepo: DebatesRepository,
        private val service: DebateChat.Service,
        private val schedulers: SchedulersSupplier,
        private val maxMessageLength: Int) {

    private val subscriptions = CompositeDisposable()

    fun onCreate(token: String) {
        getInitialComments(token)
    }

    fun onInitialsCommentsRefresh(token: String) {
        subscriptions.clear()
        getInitialComments(token)
    }

    private fun getInitialComments(token: String) {
        service.initialsCommentsObservable(token)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .doOnSuccess { (isDebateClosed) ->
                    if (isDebateClosed) view.showDebateClosedError()
                    else subscribeToLiveComments()
                }
                .subscribe(
                        { initialsComments -> view.showInitialsComments(initialsComments.comments) },
                        view::showInitialsCommentsError)
                .addTo(subscriptions)
    }

    fun onLiveCommentsRefresh() {
        subscriptions.clear()
        subscribeToLiveComments()
    }

    private fun subscribeToLiveComments() {
        service.liveCommentsObservable(debateRepo.getLatestDebateCode()!!)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .subscribe(view::showLiveComment, view::showLiveCommentsError)
                .addTo(subscriptions)
    }

    fun sendComment(token: String, message: String) {
        when {
            debateRepo.areTokenCredentialsMissing(token) -> view.showCredentialsDialog()
            message.isBlank() -> return
            message.length > maxMessageLength -> view.showInputOverLimitError()
            else -> serviceSendComment(token, message)
        }
    }

    private fun serviceSendComment(token: String, message: String) {
        val (firstName, lastName) = debateRepo.getTokenCredentials(token)
        val commentToSend = CommentToSend(token, message, firstName, lastName)
        service.sendComment(commentToSend)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe(view::clearSendCommentInput, view::showSendCommentError)
                .addTo(subscriptions)
    }

    fun onDestroy() {
        subscriptions.dispose()
    }

    fun onNewCredentials(token: String, credentials: TokenCredentials) {
        if (credentials.lastName.isBlank()) {
            view.showLastNameError()
        }
        if (credentials.firstName.isBlank()) {
            view.showFirstNameError()
        }
        if (credentials.lastName.isNotBlank() && credentials.firstName.isNotBlank()) {
            debateRepo.saveTokenCredentials(token, credentials)
            view.closeCredentialsDialog()
        }
    }
}