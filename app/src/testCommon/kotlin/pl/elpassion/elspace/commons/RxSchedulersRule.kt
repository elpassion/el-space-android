package pl.elpassion.elspace.commons

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaHooks
import rx.schedulers.Schedulers

@Deprecated(message = "Schedulers should be passed as a clear dependency")
class RxSchedulersRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            RxJavaHooks.setOnIOScheduler { Schedulers.immediate() }
            RxAndroidPlugins.getInstance().reset()
            RxAndroidPlugins.getInstance().registerSchedulersHook(object : RxAndroidSchedulersHook() {
                override fun getMainThreadScheduler() = Schedulers.immediate()
            })
            base.evaluate()
        }
    }
}