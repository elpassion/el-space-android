package pl.elpassion.elspace.debate.chat

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
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

    @Test
    fun shouldNotCallApiWhenMessageIsEmpty() {
        controller.sendMessage("")
        verify(api, never()).sendMessage(any())
    }

    @Test
    fun shouldNotCallApiWhenMessageIsBlank() {
        controller.sendMessage(" ")
        verify(api, never()).sendMessage(any())
    }
}

interface DebateChat {
    interface Api {
        fun sendMessage(message: String)
    }
}

class ChatController(val api: DebateChat.Api) {
    fun sendMessage(message: String) {
        if (!message.isBlank()) callApi(message)
    }

    private fun callApi(message: String) {
        api.sendMessage(message)
    }
}
