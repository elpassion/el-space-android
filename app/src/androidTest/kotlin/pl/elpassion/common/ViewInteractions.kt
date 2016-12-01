package pl.elpassion.common

import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import pl.elpassion.R

fun onToolbarBackArrow() = Espresso.onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))