package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Test
import pl.elpassion.elspace.dabate.chat.createComment
import pl.elpassion.elspace.dabate.chat.createCommentToSend
import pl.elpassion.elspace.debate.chat.service.DebateChatServiceImpl


class DebateChatServiceTest {

    private val commentsFromApiSubject = SingleSubject.create<List<Comment>>()
    private val sendCommentsApiSubject = CompletableSubject.create()
    private val api = mock<DebateChat.Api>().apply {
        whenever(comment(any())).thenReturn(commentsFromApiSubject)
        whenever(comment(any(), any(), any(), any())).thenReturn(sendCommentsApiSubject)
    }
    private val commentsFromSocketSubject = PublishSubject.create<Comment>()
    private val socket = mock<DebateChat.Socket>().apply {
        whenever(commentsObservable(any())).thenReturn(commentsFromSocketSubject)
    }
    private val debateChatServiceImpl = DebateChatServiceImpl(api, socket)

    @Test
    fun shouldCallApiCommentWithRealToken() {
        debateChatServiceImpl.initialsCommentsObservable("someToken")
        verify(api).comment("someToken")
    }

    @Test
    fun shouldReturnCommentsReceivedFromApiComment() {
        val commentsFromApi: ArrayList<Comment> = arrayListOf(createComment(name = "FirstTestName"), createComment(name = "TestName"))
        val testObserver = debateChatServiceImpl
                .initialsCommentsObservable("token")
                .test()
        commentsFromApiSubject.onSuccess(commentsFromApi)
        testObserver.assertValues(*commentsFromApi.toTypedArray())
    }

    @Test
    fun shouldSortCommentsReceivedFromApiComment() {
        val commentsFromApi: ArrayList<Comment> = arrayListOf(createComment(createdAt = 3), createComment(createdAt = 1), createComment(createdAt = 2))
        val testObserver = debateChatServiceImpl
                .initialsCommentsObservable("token")
                .test()
        commentsFromApiSubject.onSuccess(commentsFromApi)
        val sortedComments = commentsFromApi.sortedBy { it.createdAt }
        testObserver.assertValues(*sortedComments.toTypedArray())
    }

    @Test
    fun shouldReturnErrorReceivedFromApiComment() {
        val exception = RuntimeException()
        val testObserver = debateChatServiceImpl
                .initialsCommentsObservable("token")
                .test()
        commentsFromApiSubject.onError(exception)
        testObserver.assertError(exception)
    }

    @Test
    fun shouldCallSocketWithRealDebateCode() {
        debateChatServiceImpl.liveCommentsObservable("someDebateCode")
        verify(socket).commentsObservable("someDebateCode")
    }

    @Test
    fun shouldPropagateCommentsReturnedFromSocket() {
        val commentFirst = createComment(name = "NameSocket")
        val commentSecond = createComment(name = "NameSocketSecond")
        val testObserver = debateChatServiceImpl
                .liveCommentsObservable("code")
                .test()
        commentsFromSocketSubject.onNext(commentFirst)
        commentsFromSocketSubject.onNext(commentSecond)
        testObserver.assertValues(commentFirst, commentSecond)
    }

    @Test
    fun shouldReturnErrorReceivedFromSocket() {
        val exception = RuntimeException()
        val testObserver = debateChatServiceImpl
                .liveCommentsObservable("code")
                .test()
        commentsFromSocketSubject.onError(exception)
        testObserver.assertError(exception)
    }

    @Test
    fun shouldCallApiSendCommentWithRealData() {
        val commentToSend = createCommentToSend()
        debateChatServiceImpl.sendComment(commentToSend)
        commentToSend.run {
            verify(api).comment(token, message, firstName, lastName)
        }
    }

    @Test
    fun shouldReturnErrorReceivedFromApiSendComment() {
        val exception = RuntimeException()
        val testObserver = debateChatServiceImpl
                .sendComment(createCommentToSend())
                .test()
        sendCommentsApiSubject.onError(exception)
        testObserver.assertError(exception)
    }
}