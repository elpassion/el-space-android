package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Test
import pl.elpassion.elspace.dabate.chat.createComment
import pl.elpassion.elspace.dabate.chat.createCommentToSend
import pl.elpassion.elspace.dabate.chat.createInitialsComments
import pl.elpassion.elspace.debate.chat.service.DebateChatServiceImpl


class DebateChatServiceTest {

    private val getCommentsFromApiSubject = SingleSubject.create<InitialsComments>()
    private val getNextCommentsFromApiSubject = SingleSubject.create<InitialsComments>()
    private val sendCommentsApiSubject = SingleSubject.create<Comment>()
    private val api = mock<DebateChat.Api>().apply {
        whenever(getComments(any())).thenReturn(getCommentsFromApiSubject)
        whenever(getNextComments(any(), any())).thenReturn(getNextCommentsFromApiSubject)
        whenever(comment(any(), any(), any(), any())).thenReturn(sendCommentsApiSubject)
    }
    private val commentsFromSocketSubject = PublishSubject.create<Comment>()
    private val socket = mock<DebateChat.Socket>().apply {
        whenever(commentsObservable(any(), any())).thenReturn(commentsFromSocketSubject)
    }
    private val debateChatServiceImpl = DebateChatServiceImpl(api, socket)

    @Test
    fun shouldCallApiGetCommentsWhenNextPositionIsNull() {
        debateChatServiceImpl.initialsCommentsObservable("token", null)
        verify(api).getComments(any())
    }

    @Test
    fun shouldCallApiGetCommentsWithRealToken() {
        debateChatServiceImpl.initialsCommentsObservable("someToken", null)
        verify(api).getComments("someToken")
    }

    @Test
    fun shouldReturnInitialsCommentsReceivedFromApiGetComments() {
        val initialsCommentsFromApi = createInitialsComments(debateClosed = false, comments = listOf(createComment(name = "FirstTestName"), createComment(name = "TestName")), nextPosition = 123)
        val testObserver = debateChatServiceImpl
                .initialsCommentsObservable("token", null)
                .test()
        getCommentsFromApiSubject.onSuccess(initialsCommentsFromApi)
        testObserver.assertValues(initialsCommentsFromApi)
    }

    @Test
    fun shouldSortCommentsReceivedFromApiGetComments() {
        val initialsCommentsFromApi = createInitialsComments(comments = listOf(createComment(createdAt = 3), createComment(createdAt = 1), createComment(createdAt = 2)))
        val testObserver = debateChatServiceImpl
                .initialsCommentsObservable("token", null)
                .test()
        getCommentsFromApiSubject.onSuccess(initialsCommentsFromApi)
        val sortedComments = initialsCommentsFromApi.comments.sortedBy { it.createdAt }
        testObserver.assertValues(createInitialsComments(comments = sortedComments))
    }

    @Test
    fun shouldReturnErrorReceivedFromApiGetComments() {
        val exception = RuntimeException()
        val testObserver = debateChatServiceImpl
                .initialsCommentsObservable("token", null)
                .test()
        getCommentsFromApiSubject.onError(exception)
        testObserver.assertError(exception)
    }

    @Test
    fun shouldCallApiGetNextCommentsWhenNextPositionIsNotNull() {
        debateChatServiceImpl.initialsCommentsObservable("token", 123)
        verify(api).getNextComments(any(), any())
    }

    @Test
    fun shouldCallApiGetNextCommentsWithRealData() {
        debateChatServiceImpl.initialsCommentsObservable("token", 123)
        verify(api).getNextComments("token", 123)
    }

    @Test
    fun shouldReturnInitialsCommentsReceivedFromApiGetNextComments() {
        val initialsCommentsFromApi = createInitialsComments(debateClosed = false, comments = listOf(createComment(name = "FirstTestName"), createComment(name = "TestName")), nextPosition = 123)
        val testObserver = debateChatServiceImpl
                .initialsCommentsObservable("token", 123)
                .test()
        getNextCommentsFromApiSubject.onSuccess(initialsCommentsFromApi)
        testObserver.assertValues(initialsCommentsFromApi)
    }

    @Test
    fun shouldReturnErrorReceivedFromApiGetNextComments() {
        val exception = RuntimeException()
        val testObserver = debateChatServiceImpl
                .initialsCommentsObservable("token", 123)
                .test()
        getNextCommentsFromApiSubject.onError(exception)
        testObserver.assertError(exception)
    }

    @Test
    fun shouldCallSocketWithRealData() {
        debateChatServiceImpl.liveCommentsObservable("someDebateCode", 123)
        verify(socket).commentsObservable("someDebateCode", 123)
    }

    @Test
    fun shouldPropagateCommentsReturnedFromSocket() {
        val commentFirst = createComment(name = "NameSocket")
        val commentSecond = createComment(name = "NameSocketSecond")
        val testObserver = debateChatServiceImpl
                .liveCommentsObservable("code", 1)
                .test()
        commentsFromSocketSubject.onNext(commentFirst)
        commentsFromSocketSubject.onNext(commentSecond)
        testObserver.assertValues(commentFirst, commentSecond)
    }

    @Test
    fun shouldReturnErrorReceivedFromSocket() {
        val exception = RuntimeException()
        val testObserver = debateChatServiceImpl
                .liveCommentsObservable("code", 1)
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

    @Test
    fun shouldReturnSendCommentResponseFromApiSendComment() {
        val comment = createComment()
        val testObserver = debateChatServiceImpl
                .sendComment(createCommentToSend())
                .test()
        sendCommentsApiSubject.onSuccess(comment)
        testObserver.assertValue(comment)
    }
}