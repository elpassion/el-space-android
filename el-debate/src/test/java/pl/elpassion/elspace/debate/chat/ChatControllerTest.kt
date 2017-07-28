package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test


class ChatControllerTest {

    val api = mock<DebateChat.Api>()
    val controller = ChatController(api)

    @Test
    fun shouldCallApiWhenSendMessage() {
        controller.sendMessage()
        verify(api).sendMessage()
    }
}

interface DebateChat {
    interface Api {
        fun sendMessage()
    }
}

class ChatController(val api: DebateChat.Api) {
    fun sendMessage() {
        api.sendMessage()
    }
}
