package pl.elpassion.elspace.debate.details

import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions
import com.elpassion.android.commons.espresso.*
import org.hamcrest.core.IsEqual
import pl.elpassion.R

open class DebateDetailsViewInteractor : DebateDetailsScreen() {
    fun ViewInteraction.hasInactiveColor() {
        hasTag(IsEqual.equalTo(R.color.answerInactive))
    }

    fun ViewInteraction.hasPositiveColor() {
        hasTag(IsEqual.equalTo(R.color.answerPositive))
    }

    fun ViewInteraction.hasNegativeColor() {
        hasTag(IsEqual.equalTo(R.color.answerNegative))
    }

    fun ViewInteraction.hasNeutralColor() {
        hasTag(IsEqual.equalTo(R.color.answerNeutral))
    }

    fun ViewInteraction.isNothighlighted() {
        hasTag(IsEqual.equalTo(null))
    }

    fun ViewInteraction.scrollAndClick() {
        perform(ViewActions.scrollTo()).click()
    }

    fun clickChatButton() {
        debateChatButton.click()
    }

    fun slowDownInfoIsDisplayed() {
        slowDownView.title.isDisplayed()
        slowDownView.info.isDisplayed()
    }

    fun slowDownInfoIsNotDisplayed() {
        slowDownView.title.doesNotExist()
        slowDownView.info.doesNotExist()
    }

    fun toolbarHasText(text: Int) {
        toolbar
                .isDisplayed()
                .hasChildWithText(text)
    }
}