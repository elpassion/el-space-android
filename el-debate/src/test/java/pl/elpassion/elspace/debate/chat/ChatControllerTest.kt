package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test


class ChatControllerTest {

    val api = mock<DebateChat.Api>()
    val controller = ChatController(api)

    @Test
    fun shouldCallApiWithGivenMessage() {
        controller.sendMessage("Message")
        verify(api).sendMessage("Message")
    }

    @Test
    fun shouldCallApiWithReallyGivenMessage() {
        val message = "SomeMessage"
        controller.sendMessage(message)
        verify(api).sendMessage(message)
    }
}

interface DebateChat {
    interface Api {
        fun sendMessage(message: String)
    }
}

class ChatController(val api: DebateChat.Api) {
    fun sendMessage(message: String) {
        api.sendMessage(message)
    }
}
