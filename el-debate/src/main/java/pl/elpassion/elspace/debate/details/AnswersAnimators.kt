package pl.elpassion.elspace.debate.details

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.debate_details_activity.view.*
import pl.elpassion.R
import pl.elpassion.elspace.common.Animations

class AnswersAnimators(private val view: View, private val context: Context) {

    private val positiveAnswerAnimator by lazy { view.debatePositiveAnswerImage.drawable as Animatable }

    private val negativeAnswerAnimator by lazy { view.debateNegativeAnswerImage.drawable as Animatable }

    private val neutralAnswerAnimator by lazy { view.debateNeutralAnswerImage.drawable as Animatable }

    private fun setPositiveActive() = view.run {
        debatePositiveAnswerImage.setTintColor(R.color.blueDebatePositive)
        debateNegativeAnswerImage.setTintColor(R.color.greyDebateInactive)
        debateNeutralAnswerImage.setTintColor(R.color.greyDebateInactive)
    }

    private fun setNegativeActive() = view.run {
        debatePositiveAnswerImage.setTintColor(R.color.greyDebateInactive)
        debateNegativeAnswerImage.setTintColor(R.color.redDebateNegative)
        debateNeutralAnswerImage.setTintColor(R.color.greyDebateInactive)
    }

    private fun setNeutralActive() = view.run {
        debatePositiveAnswerImage.setTintColor(R.color.greyDebateInactive)
        debateNegativeAnswerImage.setTintColor(R.color.greyDebateInactive)
        debateNeutralAnswerImage.setTintColor(R.color.greyDebateNeutral)
    }

    private fun ImageView.setTintColor(@ColorRes tintColor: Int) {
        tag = tintColor
        setDrawableTintColor(drawable, tintColor)
    }

    private fun setDrawableTintColor(drawable: Drawable, color: Int) = DrawableCompat.setTint(drawable, ContextCompat.getColor(context, color))

    fun startPositiveAnswerAnimation() {
        setPositiveActive()
        startAnimation(positiveAnswerAnimator)
    }

    fun startNegativeAnswerAnimation() {
        setNegativeActive()
        startAnimation(negativeAnswerAnimator)
    }

    fun startNeutralAnswerAnimation() {
        setNeutralActive()
        startAnimation(neutralAnswerAnimator)
    }

    private fun startAnimation(animatable: Animatable) {
        if (Animations.areEnabled) {
            animatable.start()
        }
    }
}