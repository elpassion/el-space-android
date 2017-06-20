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

    val lineViewAnimation by lazy {
        AnimatorSet().apply {
            playTogether(objectAnimatorTranslationX, objectAnimatorScaleX)
            duration = 1000
        }
    }

    val objectAnimatorTranslationX by lazy {
        ObjectAnimator.ofFloat(answerLoader, "translationX", -width.toFloat(), width.toFloat()).apply {
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    val objectAnimatorScaleX by lazy {
        ObjectAnimator.ofFloat(answerLoader, "scaleX", 1f, 0f).apply {
            repeatCount = ValueAnimator.INFINITE
        }
    }

    fun setLoaderColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            answerLoader.drawable.setTint(ContextCompat.getColor(this.context, color))
        } else {
            answerLoader.setBackgroundColor(ContextCompat.getColor(this.context, color))
        }
    }

    fun showLoader() {
        answerLoader.visibility = View.VISIBLE
        answerLoader.alpha = 1f
        when {
            !Animations.areEnabled -> return
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> (answerLoader.drawable as? Animatable)?.start()
            else -> {
                if (!lineViewAnimation.isRunning) lineViewAnimation.start()
            }
        }
    }

    fun hideLoader() {
        when {
            !Animations.areEnabled -> return
            else -> {
                ObjectAnimator.ofFloat(answerLoader, "alpha", 1f, 0f).apply {
                    duration = 500
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            stopAnimation()
                        }
                    })
                }.start()
            }
        }
    }

    private fun stopAnimation() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> (answerLoader.drawable as? Animatable)?.stop()
            else -> lineViewAnimation.end()
        }
        answerLoader.visibility = GONE
    }
}