package pl.elpassion.elspace.debate.comment

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Test
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.dabate.details.createAnswer
import pl.elpassion.elspace.dabate.details.createDebateData

class DebateCommentControllerTest {

    private val api = mock<DebateComment.Api>()

    @Test
    fun shouldCallApiWithMessageOnSendComment() {
        val controller = DebateCommentController(api)
        controller.sendComment("message")
        verify(api).comment("message")
    }
}