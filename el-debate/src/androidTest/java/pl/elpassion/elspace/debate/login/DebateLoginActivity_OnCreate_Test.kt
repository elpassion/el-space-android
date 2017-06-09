package pl.elpassion.elspace.debate.login

import com.elpassion.android.commons.espresso.hasText
import com.elpassion.android.commons.espresso.onId
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Rule
import org.junit.Test
import pl.elpassion.R
import pl.elpassion.elspace.common.rule
import pl.elpassion.elspace.common.startActivity
import pl.elpassion.elspace.debate.DebatesRepository
import pl.elpassion.elspace.debate.DebatesRepositoryProvider

class DebateLoginActivity_OnCreate_Test {

    private val tokenRepo = mock<DebatesRepository>()

    @JvmField @Rule
    val rule = rule<DebateLoginActivity>(autoStart = false) {
        DebatesRepositoryProvider.override = { tokenRepo }
        DebateLogin.ApiProvider.override = { mock() }
    }

    @Test
    fun shouldFillDebateCodeWhenSaved() {
        whenever(tokenRepo.getLatestDebateCode()).thenReturn("12345")
        rule.startActivity()
        onId(R.id.debateLoginInputText).hasText("12345")
    }
}
