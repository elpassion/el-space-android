package pl.elpassion.elspace.common

import android.app.Activity
import android.app.Instrumentation
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers

fun stubAllIntents() {
    Intents.intending(IntentMatchers.anyIntent()).respondWith(Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null))
}

