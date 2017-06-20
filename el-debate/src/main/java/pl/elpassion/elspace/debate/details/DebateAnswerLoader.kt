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

    val lineViewAnimation by lazy { AnimatorSet() }

    fun setLoaderColor(color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.answerLoader.drawable.setTint(ContextCompat.getColor(this.context, color))
        } else {
            answerLoader.setBackgroundColor(color)
        }
    }

    fun showLoader() {
        answerLoader.visibility = View.VISIBLE
        when {
            !Animations.areEnabled -> return
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> (answerLoader.drawable as? Animatable)?.start()
            else -> {
                val objectAnimatorTranslationX = ObjectAnimator.ofFloat(answerLoader, "translationX", -width.toFloat(), width.toFloat()).apply {
                    repeatCount = ValueAnimator.INFINITE
                    interpolator = AccelerateDecelerateInterpolator()
                }
                val objectAnimatorScaleX = ObjectAnimator.ofFloat(answerLoader, "scaleX", 1f, 0f).apply {
                    repeatCount = ValueAnimator.INFINITE
                }
                lineViewAnimation.apply {
                    playTogether(objectAnimatorTranslationX, objectAnimatorScaleX)
                    duration = 1000
                }.start()
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