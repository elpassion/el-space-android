package pl.elpassion.common

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.util.AttributeSet
import pl.elpassion.R

//Workaround for https://code.google.com/p/android/issues/detail?id=230171
class FixedTextInputEditText : TextInputEditText {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        applyAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        applyAttrs(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        post {
            setText(R.string.report_hours_default_value)
        }
    }

    private fun applyAttrs(attrs: AttributeSet?) {
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.FixedTextInputEditText, 0, 0)
        try {
            val text = styledAttrs.getString(R.styleable.FixedTextInputEditText_ftext)
            setText(text)
        } finally {
            styledAttrs.recycle()
        }
    }
}