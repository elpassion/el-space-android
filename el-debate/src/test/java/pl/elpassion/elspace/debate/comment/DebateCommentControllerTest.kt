package pl.elpassion.elspace.debate.comment

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.subjects.CompletableSubject
import org.junit.Before
import org.junit.Test

class DebateCommentControllerTest {

    private val view = mock<DebateComment.View>()
    private val api = mock<DebateComment.Api>()
    private val commentSubject = CompletableSubject.create()
    private val controller = DebateCommentController(view, api)

    @Before
    fun setUp() {
        whenever(api.comment(any())).thenReturn(commentSubject)
    }

    @Test
    fun shouldCallApiWithMessageOnSendComment() {
        controller.sendComment("message")
        verify(api).comment("message")
    }

    @Test
    fun shouldShowLoaderOnSendComment() {
        controller.sendComment("message")
        verify(view).showLoader()
    }

    @Test
    fun shouldHideLoaderWhenApiCallFinished() {
        controller.sendComment("message")
        commentSubject.onComplete()
        verify(view).hideLoader()
    }
}