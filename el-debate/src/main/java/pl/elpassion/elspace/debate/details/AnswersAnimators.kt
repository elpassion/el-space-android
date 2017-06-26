package pl.elpassion.elspace.debate.details

import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.View
import kotlinx.android.synthetic.main.debate_details_activity.view.*
import pl.elpassion.R

class AnswersAnimators(private val view: View) {

    companion object {
        val greyDebateInactive = R.color.greyDebateInactive
        val blueDebatePositive = R.color.blueDebatePositive
        val redDebateNegative = R.color.redDebateNegative
        val greyDebateNeutral = R.color.greyDebateNeutral
    }

    private val positiveAnswerAnimator by lazy { view.debatePositiveAnswerImage.drawable as Animatable }

    private val negativeAnswerAnimator by lazy { view.debateNegativeAnswerImage.drawable as Animatable }

    private val neutralAnswerAnimator by lazy { view.debateNeutralAnswerImage.drawable as Animatable }

    private fun setPositiveActive() {
        view.debatePositiveAnswerImage.apply {
            tag = blueDebatePositive
            setDrawableTintColor(drawable, blueDebatePositive)
        }
        view.debateNegativeAnswerImage.apply {
            tag = greyDebateInactive
            setDrawableTintColor(drawable, greyDebateInactive)
        }
        view.debateNeutralAnswerImage.apply {
            tag = greyDebateInactive
            setDrawableTintColor(drawable, greyDebateInactive)
        }
    }

    private fun setNegativeActive() {
        view.debatePositiveAnswerImage.apply {
            tag = greyDebateInactive
            setDrawableTintColor(drawable, greyDebateInactive)
        }
        view.debateNegativeAnswerImage.apply {
            tag = redDebateNegative
            setDrawableTintColor(drawable, redDebateNegative)
        }
        view.debateNeutralAnswerImage.apply {
            tag = greyDebateInactive
            setDrawableTintColor(drawable, greyDebateInactive)
        }
    }

    private fun setNeutralActive() {
        view.debatePositiveAnswerImage.apply {
            tag = greyDebateInactive
            setDrawableTintColor(drawable, greyDebateInactive)
        }
        view.debateNegativeAnswerImage.apply {
            tag = greyDebateInactive
            setDrawableTintColor(drawable, greyDebateInactive)
        }
        view.debateNeutralAnswerImage.apply {
            tag = greyDebateNeutral
            setDrawableTintColor(drawable, greyDebateNeutral)
        }
    }

    private fun setDrawableTintColor(drawable: Drawable, color: Int) = DrawableCompat.setTint(drawable, color)

    fun startPositiveAnswerAnimation() {
        setPositiveActive()
        positiveAnswerAnimator.start()
    }

    fun startNegativeAnswerAnimation() {
        setNegativeActive()
        negativeAnswerAnimator.start()
    }

    fun startNeutralAnswerAnimation() {
        setNeutralActive()
        neutralAnswerAnimator.start()
    }
}