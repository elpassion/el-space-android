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
        serviceGetLatestComments(token)
    }

    private fun serviceGetLatestComments(token: String) {
        service.getLatestComments(token)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe(this::onServiceGetLatestCommentsSuccess, view::showGetLatestCommentsError)
                .addTo(subscriptions)
    }

    private fun onServiceGetLatestCommentsSuccess(comments: List<Comment>) {
        view.showLatestComments(comments)
        service.updateComments()
    }

    fun sendComment(token: String, message: String) {
        when {
            debateRepo.areCredentialsMissing(token) -> view.showCredentialsDialog()
            message.isBlank() -> view.showInvalidInputError()
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
                .subscribe(view::showSendCommentSuccess, view::showSendCommentError)
                .addTo(subscriptions)
    }

    fun onDestroy() {
        subscriptions.dispose()
    }

    fun onCancel() {
        view.closeScreen()
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