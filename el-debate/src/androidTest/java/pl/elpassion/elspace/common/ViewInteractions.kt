package pl.elpassion.elspace.common

import android.support.annotation.IdRes
import android.support.test.espresso.Espresso
import android.support.test.espresso.matcher.ViewMatchers
import android.view.View
import com.elpassion.android.commons.espresso.isDisplayed
import com.elpassion.android.commons.espresso.isNotDisplayed
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import pl.elpassion.R

fun onToolbarBackArrow() = Espresso.onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))

fun withParentId(@IdRes parentId: Int): Matcher<View> = ViewMatchers.withParent(ViewMatchers.withId(parentId))

fun Matcher<View>.isChildDisplayed(@IdRes childId: Int) = Espresso.onView(Matchers.allOf(ViewMatchers.withId(childId), this)).isDisplayed()

fun Matcher<View>.isChildNotDisplayed(@IdRes childId: Int) = Espresso.onView(Matchers.allOf(ViewMatchers.withId(childId), this)).isNotDisplayed()