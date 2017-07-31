package pl.elpassion.elspace.debate.chat

import io.reactivex.disposables.Disposable
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebatesRepository

class DebateChatController(
        private val view: DebateChat.View,
        private val debateRepo: DebatesRepository,
        private val api: DebateChat.Api,
        private val schedulers: SchedulersSupplier,
        private val maxMessageLength: Int) {

    private var subscription: Disposable? = null

    fun sendComment(token: String, message: String) {
        when {
            debateRepo.areCredentialsMissing(token) -> view.showCredentialsDialog()
            message.isBlank() -> view.showInvalidInputError()
            message.length > maxMessageLength -> view.showInputOverLimitError()
            else -> callApiComment(token, message)
        }
    }

    private fun callApiComment(token: String, message: String) {
        val (firstName, lastName) = debateRepo.getTokenCredentials(token)
        subscription = api.comment(token, message, firstName, lastName)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { view.showLoader() }
                .doFinally(view::hideLoader)
                .subscribe(view::closeScreen, view::showSendCommentError)
    }

    fun getComments() {
        api.getComments()
    }

    fun onDestroy() {
        subscription?.dispose()
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
        if (credentials.lastName.isNotBlank() && credentials.firstName.isNotBlank()){
            debateRepo.saveTokenCredentials(token, credentials)
            view.closeCredentialsDialog()
        }
    }
}