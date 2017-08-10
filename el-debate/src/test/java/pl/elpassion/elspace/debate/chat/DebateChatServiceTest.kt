package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.subjects.SingleSubject
import org.junit.Test
import pl.elpassion.elspace.dabate.chat.createComment


class DebateChatServiceTest {

    private val commentsFromApi: ArrayList<Comment> = arrayListOf(createComment(name = "FirstTestName"), createComment(name = "TestName"))
    private val commentsFromApiSubject = SingleSubject.create<List<Comment>>()
    private val api = mock<DebateChat.Api>().apply {
        whenever(comment(any())).thenReturn(commentsFromApiSubject)
    }
    private val debateChatServiceImpl = DebateChatServiceImpl(api)

    @Test
    fun shouldReturnCommentsReceivedFromApi() {
        debateChatServiceImpl
                .commentsObservable("token")
                .test()
                .apply { commentsFromApiSubject.onSuccess(commentsFromApi) }
                .assertValues(*commentsFromApi.toTypedArray())
    }

    @Test
    fun shouldCallApiWithRealToken() {
        debateChatServiceImpl.commentsObservable("someToken")
        verify(api).comment("someToken")
    }
}

class DebateChatServiceImpl(private val api: DebateChat.Api) {

    fun commentsObservable(token: String): Observable<Comment> = api.comment(token).flattenAsObservable { it }
}
