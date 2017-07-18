package pl.elpassion.elspace.debate.details

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.debate_details_activity.view.*
import pl.elpassion.R
import pl.elpassion.elspace.common.Animations

class AnswersAnimators(private val view: View, private val context: Context) {

    companion object {
        val GREY_DEBATE_INACTIVE = R.color.greyDebateInactive
        val BLUE_DEBATE_POSITIVE = R.color.blueDebatePositive
        val RED_DEBATE_NEGATIVE = R.color.redDebateNegative
        val GREY_DEBATE_NEUTRAL = R.color.greyDebateNeutral
    }

    private val positiveAnswerAnimator by lazy { view.debatePositiveAnswerImage.drawable as Animatable }

    private val negativeAnswerAnimator by lazy { view.debateNegativeAnswerImage.drawable as Animatable }

    private val neutralAnswerAnimator by lazy { view.debateNeutralAnswerImage.drawable as Animatable }

    private fun setPositiveActive() = view.run {
        debatePositiveAnswerImage.setTintColor(BLUE_DEBATE_POSITIVE)
        debateNegativeAnswerImage.setTintColor(GREY_DEBATE_INACTIVE)
        debateNeutralAnswerImage.setTintColor(GREY_DEBATE_INACTIVE)
    }

    private fun setNegativeActive() = view.run {
        debatePositiveAnswerImage.setTintColor(GREY_DEBATE_INACTIVE)
        debateNegativeAnswerImage.setTintColor(RED_DEBATE_NEGATIVE)
        debateNeutralAnswerImage.setTintColor(GREY_DEBATE_INACTIVE)
    }

    private fun setNeutralActive() = view.run {
        debatePositiveAnswerImage.setTintColor(GREY_DEBATE_INACTIVE)
        debateNegativeAnswerImage.setTintColor(GREY_DEBATE_INACTIVE)
        debateNeutralAnswerImage.setTintColor(GREY_DEBATE_NEUTRAL)
    }

    private fun ImageView.setTintColor(tintColor: Int) {
        tag = tintColor
        setDrawableTintColor(drawable, tintColor)
    }

    private fun setDrawableTintColor(drawable: Drawable, color: Int) = DrawableCompat.setTint(drawable, ContextCompat.getColor(context, color))

    fun startPositiveAnswerAnimation() {
        setPositiveActive()
        if (Animations.areEnabled) {
            positiveAnswerAnimator.start()
        }
    }

    fun startNegativeAnswerAnimation() {
        setNegativeActive()
        if (Animations.areEnabled) {
            negativeAnswerAnimator.start()
        }
    }

    fun startNeutralAnswerAnimation() {
        setNeutralActive()
        if (Animations.areEnabled) {
            neutralAnswerAnimator.start()
        }
    }
}