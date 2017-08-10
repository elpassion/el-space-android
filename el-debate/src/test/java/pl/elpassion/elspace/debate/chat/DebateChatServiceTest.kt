package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.subjects.SingleSubject
import org.junit.Test
import pl.elpassion.elspace.dabate.chat.createComment


class DebateChatServiceTest {

    @Test
    fun shouldReturnCommentsReceivedFromApi() {
        val commentsFromApi: ArrayList<Comment> = arrayListOf(createComment(name = "FirstTestName"), createComment(name = "TestName"))
        val commentsFromApiSubject = SingleSubject.create<List<Comment>>()
        val api = mock<DebateChat.Api>().apply {
            whenever(comment(any())).thenReturn(commentsFromApiSubject)
        }
        DebateChatServiceImpl(api)
                .commentsObservable()
                .test()
                .apply { commentsFromApiSubject.onSuccess(commentsFromApi) }
                .assertValues(*commentsFromApi.toTypedArray())
    }
}

class DebateChatServiceImpl(private val api: DebateChat.Api) {

    fun commentsObservable(): Observable<Comment> = api.comment("").flattenAsObservable { it }
}
