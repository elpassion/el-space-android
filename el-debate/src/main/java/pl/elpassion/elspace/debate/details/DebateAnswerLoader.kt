package pl.elpassion.elspace.debate.details

import android.animation.*
import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.answer_loader.view.*
import pl.elpassion.elspace.common.Animations

class DebateAnswerLoader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    private val preLollipopAnimation by lazy {
        AnimatorSet().apply {
            playTogether(translationX, scaleX)
            duration = 1000
        }
    }

    private val translationX: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(answerLoader, "translationX", -width.toFloat(), width.toFloat()).apply {
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    private val scaleX: ObjectAnimator by lazy {
        ObjectAnimator.ofFloat(answerLoader, "scaleX", 1f, 0f).apply {
            repeatCount = ValueAnimator.INFINITE
        }
    }

    fun setColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            answerLoader.drawable.setTint(ContextCompat.getColor(context, color))
        } else {
            answerLoader.setBackgroundColor(ContextCompat.getColor(context, color))
        }
    }

    fun show() {
        answerLoader.visibility = View.VISIBLE
        answerLoader.alpha = 1f
        when {
            !Animations.areEnabled -> return
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> (answerLoader.drawable as? Animatable)?.start()
            !preLollipopAnimation.isRunning -> preLollipopAnimation.start()
        }
    }

    fun hide() {
        if (Animations.areEnabled) {
            ObjectAnimator.ofFloat(answerLoader, "alpha", 1f, 0f).run {
                duration = 500
                addListener(animatorListenerAdapter)
                start()
            }
        }
    }

    private val animatorListenerAdapter: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            stopAnimation()
        }
    }

    private fun stopAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            (answerLoader.drawable as? Animatable)?.stop()
        } else {
            preLollipopAnimation.end()
        }
        answerLoader.visibility = GONE
    }
}