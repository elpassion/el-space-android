package pl.elpassion.elspace.common

import android.support.design.internal.BottomNavigationItemView
import android.support.test.espresso.matcher.BoundedMatcher
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher

fun isBottomNavigationItemDataChecked(): Matcher<View> {
    return object : BoundedMatcher<View, BottomNavigationItemView>(BottomNavigationItemView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("with BottomNavigationItem state: is checked")
        }

        override fun matchesSafely(item: BottomNavigationItemView): Boolean = item.itemData.isChecked
    }
}