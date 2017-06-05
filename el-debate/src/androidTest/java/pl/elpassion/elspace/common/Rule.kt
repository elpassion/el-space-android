package pl.elpassion.elspace.common

import android.app.Activity
import android.support.test.rule.ActivityTestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import pl.elpassion.elspace.api.DebateRetrofitProvider
import pl.elpassion.elspace.debate.DebateTokenRepositoryProvider

inline fun <reified T : Activity> rule(autoStart: Boolean = true, noinline beforeActivity: () -> Unit = { Unit }): ActivityTestRule<T> {
    return object : ActivityTestRule<T>(T::class.java, false, autoStart) {
        override fun apply(base: Statement?, description: Description?): Statement {
            DebateTokenRepositoryProvider.override = { throw RuntimeException("Inner memory in tests not allowed!") }
            DebateRetrofitProvider.override = { throw RuntimeException("Internet in tests not allowed!") }
            Animations.areEnabled = false
            return super.apply(base, description)
        }

        override fun beforeActivityLaunched() {
            beforeActivity.invoke()
        }
    }
}