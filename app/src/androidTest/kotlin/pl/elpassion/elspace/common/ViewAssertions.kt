package pl.elpassion.elspace.common

import android.support.annotation.IdRes
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.not

fun ViewInteraction.hasChildWithText(text: String): ViewInteraction = check(matches(hasDescendant(withText(text))))

fun ViewInteraction.hasNoChildWithText(text: String): ViewInteraction = check(matches(not(hasDescendant(withText(text)))))

fun ViewInteraction.hasNoChildWithId(@IdRes id: Int): ViewInteraction = check(matches(not(hasDescendant(withId(id)))))

fun ViewInteraction.isBottomNavigationItemChecked(): ViewInteraction = check(matches(isBottomNavigationItemDataChecked()))