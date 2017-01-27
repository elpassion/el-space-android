package pl.elpassion.common.extensions

import android.support.design.widget.TextInputEditText
import android.view.View
import com.elpassion.android.view.hide
import com.elpassion.android.view.show

fun TextInputEditText.showInput() = (parent as View).show()
fun TextInputEditText.hideInput() = (parent as View).hide()
