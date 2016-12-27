package pl.elpassion


import android.annotation.SuppressLint
import android.content.ContentResolver
import android.os.IBinder
import android.provider.Settings
import android.support.test.InstrumentationRegistry

/**
 * Returns whether or not is any animation or transition enabled on a device.

 * @return True if any animation or transition is enabled on a device. False otherwise.
 */
fun isAnyAnimationEnabled(): Boolean {
    val resolver = InstrumentationRegistry.getTargetContext().contentResolver
    val windowAnimationScale = getWindowAnimationScale(resolver)
    val transitionAnimationScale = getTransitionAnimationScale(resolver)
    val animatorDurationScale = getAnimatorDurationScale(resolver)
    return windowAnimationScale + transitionAnimationScale + animatorDurationScale != 0f
}

@SuppressLint("InlinedApi")
private fun getWindowAnimationScale(resolver: ContentResolver): Float {
    return getSetting(resolver, Settings.Global.WINDOW_ANIMATION_SCALE)
}

@SuppressLint("InlinedApi")
private fun getTransitionAnimationScale(resolver: ContentResolver): Float {
    return getSetting(resolver, Settings.Global.TRANSITION_ANIMATION_SCALE)
}

@SuppressLint("InlinedApi")
private fun getAnimatorDurationScale(resolver: ContentResolver): Float {
    return getSetting(resolver, Settings.Global.ANIMATOR_DURATION_SCALE)
}

private fun getSetting(resolver: ContentResolver, current: String): Float {
    return getGlobalSetting(resolver, current)
}

private fun getGlobalSetting(resolver: ContentResolver, setting: String): Float {
    try {
        return Settings.Global.getFloat(resolver, setting)
    } catch (e: Settings.SettingNotFoundException) {
        return 1.00f
    }
}

private fun changeAnimations(animationScale: Float) {
    val windowManagerStubClazz = Class.forName("android.view.IWindowManager\$Stub")
    val asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder::class.java)
    val serviceManagerClazz = Class.forName("android.os.ServiceManager")
    val getService = serviceManagerClazz.getDeclaredMethod("getService", String::class.java)
    val windowManagerClazz = Class.forName("android.view.IWindowManager")
    val setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales", FloatArray::class.java)
    val getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales")

    val windowManagerBinder = getService.invoke(null, "window") as IBinder
    val windowManagerObj = asInterface.invoke(null, windowManagerBinder)
    val currentScales = getAnimationScales.invoke(windowManagerObj) as FloatArray

    for (i in currentScales.indices) {
        currentScales[i] = animationScale
    }
    setAnimationScales.invoke(windowManagerObj, currentScales)
}

fun disableAnimation() {
    changeAnimations(0.00f)
}

fun enableAnimation() {
    changeAnimations(1.00f)
}