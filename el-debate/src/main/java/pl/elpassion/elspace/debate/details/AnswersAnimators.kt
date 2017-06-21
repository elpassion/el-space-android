package pl.elpassion.elspace.debate.details

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.debate_details_activity.view.*
import pl.elpassion.R


class AnswersAnimators(private val view: View, private val context: Context) {

    companion object{
        private val ANSWER_ANMIATION_DURATION: Long = 1000
    }

    private val positiveAnswerAnimator by lazy {
        AnimatorSet().apply {
            duration = ANSWER_ANMIATION_DURATION
            play(createObjectAnimator(view.debatePositiveAnswerImage))
            addListener(getAnimatorListenerAdapter { setPositiveActive() })
        }
    }

    private val neutralAnswerAnimator by lazy {
        AnimatorSet().apply {
            duration = ANSWER_ANMIATION_DURATION
            play(createObjectAnimator(view.debateNeutralAnswerImage))
            addListener(getAnimatorListenerAdapter { setNeutralActive() })
        }
    }

    private val negativeAnswerAnimator by lazy {
        AnimatorSet().apply {
            duration = ANSWER_ANMIATION_DURATION
            play(createObjectAnimator(view.debateNegativeAnswerImage))
            addListener(getAnimatorListenerAdapter { setNegativeActive() })
        }
    }

    private fun setPositiveActive() {
        view.debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hand_positive_active))
        view.debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hand_negative_inactive))
        view.debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hand_neutral_inactive))
    }

    private fun setNegativeActive() {
        view.debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hand_positive_inactive))
        view.debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hand_negative_active))
        view.debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hand_neutral_inactive))
    }

    private fun setNeutralActive() {
        view.debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hand_positive_inactive))
        view.debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hand_negative_inactive))
        view.debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.hand_neutral_active))
    }

    private fun getAnimatorListenerAdapter(setActiveAnswer: () -> Unit) = object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            setActiveAnswer()
        }
    }

    fun startPositiveAnswerAnimation() = positiveAnswerAnimator.start()

    fun startNeutralAnswerAnimation() = neutralAnswerAnimator.start()

    fun startNegativeAnswerAnimation() = negativeAnswerAnimator.start()

    private fun createObjectAnimator(target: ImageView): ObjectAnimator = ObjectAnimator.ofFloat(target, "alpha", 0f, 1f)

}