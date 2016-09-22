package pl.elpassion

import android.content.Intent
import android.support.test.rule.ActivityTestRule

fun ActivityTestRule<*>.startActivity() {
    launchActivity(Intent(Intent.ACTION_MAIN))
}
