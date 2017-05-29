package pl.elpassion.elspace.common

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet
import pl.elpassion.R

//Workaround for https://code.google.com/p/android/issues/detail?id=230171
class FixedTextInputEditText : TextInputEditText {

    private var delayedText: String = ""

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        delayedText = extractTextFromAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        delayedText = extractTextFromAttrs(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        post {
            setText(delayedText)
        }
    }

    private fun extractTextFromAttrs(attrs: AttributeSet?): String {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.FixedTextInputEditText, 0, 0)
        return try {
            styledAttrs.getString(R.styleable.FixedTextInputEditText_text)
        } finally {
            styledAttrs.recycle()
        }
    }
}