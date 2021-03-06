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
        debatePositiveAnswerImage.setTintColor(R.color.answerPositive)
        debateNegativeAnswerImage.setTintColor(R.color.answerInactive)
        debateNeutralAnswerImage.setTintColor(R.color.answerInactive)
    }

    private fun setNegativeActive() = view.run {
        debatePositiveAnswerImage.setTintColor(R.color.answerInactive)
        debateNegativeAnswerImage.setTintColor(R.color.answerNegative)
        debateNeutralAnswerImage.setTintColor(R.color.answerInactive)
    }

    private fun setNeutralActive() = view.run {
        debatePositiveAnswerImage.setTintColor(R.color.answerInactive)
        debateNegativeAnswerImage.setTintColor(R.color.answerInactive)
        debateNeutralAnswerImage.setTintColor(R.color.answerNeutral)
    }

    private fun ImageView.setTintColor(@ColorRes tintColor: Int) {
        tag = tintColor
        setDrawableTintColor(drawable, tintColor)
    }

    private fun setDrawableTintColor(drawable: Drawable, color: Int) = DrawableCompat.setTint(drawable, ContextCompat.getColor(context, color))

    fun startPositiveAnswerAnimation() {
        setPositiveActive()
        positiveAnswerAnimator.startAnimation()
    }

    fun startNegativeAnswerAnimation() {
        setNegativeActive()
        negativeAnswerAnimator.startAnimation()
    }

    fun startNeutralAnswerAnimation() {
        setNeutralActive()
        neutralAnswerAnimator.startAnimation()
    }

    private fun Animatable.startAnimation() {
        if (Animations.areEnabled) {
            this.start()
        }
    }
}