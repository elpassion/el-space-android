package pl.elpassion.elspace.debate.details

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Animatable
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.debate_details_activity.view.*
import pl.elpassion.R

class AnswersAnimators(private val view: View, private val context: Context) {

    private val positiveAnswerAnimator by lazy { view.debatePositiveAnswerImage.drawable as Animatable }

    private val negativeAnswerAnimator by lazy { view.debateNegativeAnswerImage.drawable as Animatable }

    private val neutralAnswerAnimator by lazy { view.debateNeutralAnswerImage.drawable as Animatable }

    @SuppressLint("NewApi")
    private fun setPositiveActive() {
        view.debatePositiveAnswerImage.drawable.setTint(ContextCompat.getColor(context, R.color.blueDebatePositive))
        view.debateNegativeAnswerImage.drawable.setTint(ContextCompat.getColor(context, R.color.greyDebateInactive))
        view.debateNeutralAnswerImage.drawable.setTint(ContextCompat.getColor(context, R.color.greyDebateInactive))
    }

    @SuppressLint("NewApi")
    private fun setNegativeActive() {
        view.debatePositiveAnswerImage.drawable.setTint(ContextCompat.getColor(context, R.color.greyDebateInactive))
        view.debateNegativeAnswerImage.drawable.setTint(ContextCompat.getColor(context, R.color.redDebateNegative))
        view.debateNeutralAnswerImage.drawable.setTint(ContextCompat.getColor(context, R.color.greyDebateInactive))
    }

    @SuppressLint("NewApi")
    private fun setNeutralActive() {
        view.debatePositiveAnswerImage.drawable.setTint(ContextCompat.getColor(context, R.color.greyDebateInactive))
        view.debateNegativeAnswerImage.drawable.setTint(ContextCompat.getColor(context, R.color.greyDebateInactive))
        view.debateNeutralAnswerImage.drawable.setTint(ContextCompat.getColor(context, R.color.greyDebateNeutral))
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