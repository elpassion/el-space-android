package pl.elpassion.elspace.common

import android.content.Intent
import android.support.test.rule.ActivityTestRule

fun ActivityTestRule<*>.startActivity() {
    startActivity(Intent(Intent.ACTION_MAIN))
}

fun ActivityTestRule<*>.startActivity(intent: Intent) {
    launchActivity(intent)
}
