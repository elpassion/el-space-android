package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Observable
import org.junit.Test


class DebateChatServiceTest {

    @Test
    fun shouldCallApiOnSubscribeToServiceCommentsObservable() {
        val api = mock<DebateChat.Api>()
        DebateChatServiceImpl(api).commentsObservable().subscribe()
        verify(api).comment(any())
    }
}

class DebateChatServiceImpl(private val api: DebateChat.Api) {

    fun commentsObservable(): Observable<Comment> {
        api.comment("...")
        return Observable.error(RuntimeException())
    }
}
