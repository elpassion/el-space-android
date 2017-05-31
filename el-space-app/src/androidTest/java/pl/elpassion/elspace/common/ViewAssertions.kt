package pl.elpassion.elspace.common

import android.support.annotation.IdRes
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.CoreMatchers.not

fun ViewInteraction.hasNoChildWithId(@IdRes id: Int): ViewInteraction = check(matches(not(hasDescendant(withId(id)))))

fun ViewInteraction.isBottomNavigationItemChecked(): ViewInteraction = check(matches(isBottomNavigationItemDataChecked()))