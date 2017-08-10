package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Test
import pl.elpassion.elspace.dabate.chat.createComment


class DebateChatServiceTest {

    private val commentsFromApiSubject = SingleSubject.create<List<Comment>>()
    private val api = mock<DebateChat.Api>().apply {
        whenever(comment(any())).thenReturn(commentsFromApiSubject)
    }
    private val commentsFromSocketSubject = PublishSubject.create<Comment>()
    private val socket = mock<CommentsSocket>().apply {
        whenever(commentsObservable(any())).thenReturn(commentsFromSocketSubject)
    }
    private val debateChatServiceImpl = DebateChatServiceImpl(api, socket)

    @Test
    fun shouldCallApiWithRealToken() {
        debateChatServiceImpl.commentsObservable("someToken", "code")
        verify(api).comment("someToken")
    }

    @Test
    fun shouldReturnCommentsReceivedFromApi() {
        val commentsFromApi: ArrayList<Comment> = arrayListOf(createComment(name = "FirstTestName"), createComment(name = "TestName"))
        debateChatServiceImpl
                .commentsObservable("token", "code")
                .test()
                .apply { commentsFromApiSubject.onSuccess(commentsFromApi) }
                .assertValues(*commentsFromApi.toTypedArray())
    }

    @Test
    fun shouldReturnErrorReceivedFromApi() {
        val exception = RuntimeException()
        debateChatServiceImpl
                .commentsObservable("token", "code")
                .test()
                .apply { commentsFromApiSubject.onError(exception) }
                .assertError(exception)
    }

    @Test
    fun shouldCallSocketWithRealDebateCode() {
        debateChatServiceImpl.commentsObservable("token", "someDebateCode")
        verify(socket).commentsObservable("someDebateCode")
    }

    @Test
    fun shouldPropagateCommentsReturnedFromSocket() {
        val comment = createComment(name = "NameSocket")
        debateChatServiceImpl
                .commentsObservable("token", "code")
                .test()
                .apply {
                    commentsFromApiSubject.onSuccess(emptyList())
                    commentsFromSocketSubject.onNext(comment)
                }
                .assertValue(comment)
    }

    @Test
    fun shouldReturnErrorReceivedFromSocket() {
        val exception = RuntimeException()
        debateChatServiceImpl
                .commentsObservable("token", "code")
                .test()
                .apply {
                    commentsFromApiSubject.onSuccess(emptyList())
                    commentsFromSocketSubject.onError(exception)
                }
                .assertError(exception)
    }

    @Test
    fun shouldCallApiSendCommentWithRealData() {
        val commentToSend = CommentToSend("token", "message", "first", "last")
        debateChatServiceImpl.sendComment(commentToSend)
        commentToSend.run {
            verify(api).comment(token, message, firstName, lastName)
        }
    }
}

interface ChatService {
    fun commentsObservable(token: String, debateCode: String): Observable<Comment>
    fun sendComment(commentToSend: CommentToSend): Completable
}

class DebateChatServiceImpl(private val api: DebateChat.Api, private val socket: CommentsSocket) : ChatService {

    override fun commentsObservable(token: String, debateCode: String): Observable<Comment> = Observable.concat(api.comment(token).flattenAsObservable { it }, socket.commentsObservable(debateCode))
    override fun sendComment(commentToSend: CommentToSend): Completable = commentToSend.run { api.comment(token, message, firstName, lastName) }
}

interface CommentsSocket {
    fun commentsObservable(debateCode: String): Observable<Comment>
}
