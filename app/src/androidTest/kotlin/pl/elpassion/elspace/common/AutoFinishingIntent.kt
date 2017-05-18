package pl.elpassion.elspace.common

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers

private val autoFinishingIntentActionName = "com.elpassion.android.commons.espresso.AutoFinishingIntent"

fun prepareAutoFinishingIntent() = Intents.intending(IntentMatchers.hasAction(autoFinishingIntentActionName)).respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, null))

fun getAutoFinishingIntent(): Intent {
    return Intent(autoFinishingIntentActionName)
}
