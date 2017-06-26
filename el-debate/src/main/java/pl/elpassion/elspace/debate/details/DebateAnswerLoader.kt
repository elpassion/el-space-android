package pl.elpassion.elspace.debate.details

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Animatable
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.elpassion.android.view.isVisible
import kotlinx.android.synthetic.main.answer_loader.view.*
import pl.elpassion.R
import pl.elpassion.elspace.common.Animations

class DebateAnswerLoader @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.debate_answer_loader, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DebateAnswerLoader)
            typedArray?.let {
                try {
                    getTintColorFromAttrs(typedArray, context)
                } finally {
                    typedArray.recycle()
                }
            }
        }
    }

    private fun getTintColorFromAttrs(typedArray: TypedArray, context: Context) {
        if (typedArray.hasValue(R.styleable.DebateAnswerLoader_tintColor)) {
            val tintColor = typedArray.getColor(R.styleable.DebateAnswerLoader_tintColor, ContextCompat.getColor(context, R.color.greyDebateInactive))
            applyTintColor(tintColor)
        }
    }

    private fun applyTintColor(tintColor: Int) = DrawableCompat.setTint(answerLoader.drawable, tintColor)

    private val loaderAnimation by lazy { answerLoader.drawable as Animatable }

    fun show() {
        answerLoader.visibility = View.VISIBLE
        answerLoader.alpha = 1f
        if (Animations.areEnabled) {
            loaderAnimation.start()
        }
    }

    fun hide(hidingFinishedListener: () -> Unit) {
        if (!Animations.areEnabled && !answerLoader.isVisible()) {
            stopAnimation(hidingFinishedListener)
        } else {
            createFadeOffAnimation(hidingFinishedListener).start()
        }
    }

    private fun createFadeOffAnimation(hidingFinishedListener: () -> Unit) = ObjectAnimator.ofFloat(answerLoader, "alpha", 1f, 0f).apply {
        duration = 1000
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                stopAnimation(hidingFinishedListener)
            }
        })
    }

    private fun stopAnimation(hidingFinishedListener: () -> Unit) {
        loaderAnimation.stop()
        answerLoader.visibility = GONE
        hidingFinishedListener()
    }
}