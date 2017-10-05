package pl.elpassion.elspace.debate.chat

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.LoginCredentials
import retrofit2.HttpException

class DebateChatController(
        private val view: DebateChat.View,
        private val events: DebateChat.Events,
        private val debateRepo: DebatesRepository,
        private val service: DebateChat.Service,
        private val schedulers: SchedulersSupplier,
        private val maxMessageLength: Int) {

    private val serviceSubscriptions = CompositeDisposable()
    private var onNextCommentsEventDisposable: Disposable? = null
    private var liveCommentsDisposable: Disposable? = null
    private var nextPosition: Long? = null

    fun onCreate(loginCredentials: LoginCredentials) {
        callServiceInitialsComments(loginCredentials)
        onNextCommentsEventDisposable = events.onNextComments()
                .doOnNext { callServiceInitialsComments(loginCredentials) }
                .subscribe()
    }

    fun onInitialsCommentsRefresh(loginCredentials: LoginCredentials) {
        serviceSubscriptions.clear()
        callServiceInitialsComments(loginCredentials)
    }

    private fun callServiceInitialsComments(loginCredentials: LoginCredentials) {
        service.initialsCommentsObservable(loginCredentials.authToken, nextPosition)
                .subscribeOn(schedulers.backgroundScheduler)
                .observeOn(schedulers.uiScheduler)
                .doOnSubscribe { if (!view.isDuringOnNextComments()) view.showLoader() }
                .doFinally(view::hideLoader)
                .doOnSuccess { (debateClosed, _, nextPositionFromService) ->
                    nextPosition = nextPositionFromService
                    if (debateClosed) view.showDebateClosedError()
                    else if (isLiveCommentsUnsubscribed()) subscribeToLiveComments(loginCredentials.userId)
                }
                .subscribe(
                        { initialsComments -> view.showInitialsComments(initialsComments.comments) },
                        view::showInitialsCommentsError)
                .addTo(serviceSubscriptions)
    }

    private fun isLiveCommentsUnsubscribed() = liveCommentsDisposable?.isDisposed ?: true

    fun onLiveCommentsRefresh(userId: Long) {
        serviceSubscriptions.clear()
        subscribeToLiveComments(userId)
    }

    private fun subscribeToLiveComments(userId: Long) {
        debateRepo.getLatestDebateCode()?.let {
            liveCommentsDisposable = service.liveCommentsObservable(it, userId)
                    .subscribeOn(schedulers.backgroundScheduler)
                    .observeOn(schedulers.uiScheduler)
                    .subscribe(view::showLiveComment, view::showLiveCommentsError)
                    .addTo(serviceSubscriptions)
        }
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
                .subscribe(this::showSendCommentSuccess, this::checkSendCommentError)
                .addTo(serviceSubscriptions)
    }

    private fun showSendCommentSuccess(comment: Comment) {
        when (comment.commentStatus) {
            CommentStatus.PENDING -> view.showSendCommentSuccessPending(comment)
            else -> view.clearSendCommentInput()
        }
    }

    private fun checkSendCommentError(error: Throwable) {
        when {
            error is HttpException && error.code() == 403 -> view.showDebateClosedError()
            else -> view.showSendCommentError(error)
        }
    }

    fun onDestroy() {
        serviceSubscriptions.dispose()
        onNextCommentsEventDisposable?.dispose()
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