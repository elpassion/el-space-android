package pl.elpassion.common

import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.CoreMatchers.not

fun ViewInteraction.hasChildWithText(text: String) = check(matches(hasDescendant(withText(text))))

fun ViewInteraction.hasNoChildWithText(text: String) = check(matches(not(hasDescendant(withText(text)))))