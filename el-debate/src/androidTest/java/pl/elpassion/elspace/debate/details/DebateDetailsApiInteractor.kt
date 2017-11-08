package pl.elpassion.elspace.debate.details

import com.nhaarman.mockito_kotlin.*
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.SingleSubject

open class DebateDetailsApiInteractor : DebateDetailsViewInteractor() {

    private val debateDetailsSubject = SingleSubject.create<DebateData>()
    private val sendVoteSubject = CompletableSubject.create()

    private val apiMock by lazy {
        mock<DebateDetails.Api>().apply {
            whenever(getDebateDetails(any())).thenReturn(debateDetailsSubject)
            whenever(vote(any(), any())).thenReturn(sendVoteSubject)
        }
    }

    val votingApi: VotingApiInteractor = VotingApiInteractor()
    val debateApi: DebateApiInteractor = DebateApiInteractor()

    fun inject() {
        DebateDetails.ApiProvider.override = { apiMock }
    }

    inner class DebateApiInteractor {
        fun assertCalled(tokenMatcher: () -> String = { any() }) {
            verify(apiMock, times(1)).getDebateDetails(tokenMatcher())
        }

        fun success(debateData: DebateData) {
            debateDetailsSubject.onSuccess(debateData)
        }

        fun error(runtimeException: Exception) {
            debateDetailsSubject.onError(runtimeException)
        }

        fun resetInvocations() {
            clearInvocations(apiMock)
        }
    }

    inner class VotingApiInteractor {

        fun error(httpException: Exception) {
            sendVoteSubject.onError(httpException)
        }

        fun assertCalled(tokenMatcher: () -> String = { any() }, answerMatcher: () -> Answer = { any() }) {
            verify(apiMock).vote(tokenMatcher(), answerMatcher())
        }

        fun success() {
            sendVoteSubject.onComplete()
        }
    }
}