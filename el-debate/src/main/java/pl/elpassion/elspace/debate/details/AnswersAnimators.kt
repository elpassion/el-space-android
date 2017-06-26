package pl.elpassion.elspace.debate.details

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Animatable
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.debate_details_activity.view.*
import pl.elpassion.R

class AnswersAnimators(private val view: View, private val context: Context) {

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
        blueDebatePositive.run {
            view.debatePositiveAnswerImage.tag = this
            view.debatePositiveAnswerImage.drawable.setColorFilter(ContextCompat.getColor(context, this), PorterDuff.Mode.SRC_IN)
        }
        greyDebateInactive.run {
            view.debateNegativeAnswerImage.tag = this
            view.debateNegativeAnswerImage.drawable.setColorFilter(ContextCompat.getColor(context, this), PorterDuff.Mode.SRC_IN)
            view.debateNeutralAnswerImage.tag = this
            view.debateNeutralAnswerImage.drawable.setColorFilter(ContextCompat.getColor(context, this), PorterDuff.Mode.SRC_IN)
        }
    }

    private fun setNegativeActive() {
        redDebateNegative.run {
            view.debateNegativeAnswerImage.tag = this
            view.debateNegativeAnswerImage.drawable.setColorFilter(ContextCompat.getColor(context, this), PorterDuff.Mode.SRC_IN)
        }
        greyDebateInactive.run {
            view.debatePositiveAnswerImage.tag = this
            view.debatePositiveAnswerImage.drawable.setColorFilter(ContextCompat.getColor(context, this), PorterDuff.Mode.SRC_IN)
            view.debateNeutralAnswerImage.tag = this
            view.debateNeutralAnswerImage.drawable.setColorFilter(ContextCompat.getColor(context, this), PorterDuff.Mode.SRC_IN)
        }
    }

    private fun setNeutralActive() {
        greyDebateNeutral.run {
            view.debateNeutralAnswerImage.tag = this
            view.debateNeutralAnswerImage.drawable.setColorFilter(ContextCompat.getColor(context, this), PorterDuff.Mode.SRC_IN)
        }
        greyDebateInactive.run {
            view.debatePositiveAnswerImage.tag = this
            view.debatePositiveAnswerImage.drawable.setColorFilter(ContextCompat.getColor(context, this), PorterDuff.Mode.SRC_IN)
            view.debateNegativeAnswerImage.tag = this
            view.debateNegativeAnswerImage.drawable.setColorFilter(ContextCompat.getColor(context, this), PorterDuff.Mode.SRC_IN)
        }
    }

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