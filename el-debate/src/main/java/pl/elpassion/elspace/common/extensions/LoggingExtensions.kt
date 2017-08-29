package pl.elpassion.elspace.common.extensions

import android.util.Log
import pl.elpassion.BuildConfig

fun logExceptionIfDebug(source: String, exception: Throwable) {
    if (BuildConfig.DEBUG) Log.e("Exception has occurred:", source, exception)
}

fun logMessageIfDebug(source: String, message: String) {
    if (BuildConfig.DEBUG) Log.i(source, message)
}
