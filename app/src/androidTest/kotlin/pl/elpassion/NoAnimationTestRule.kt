package pl.elpassion


import android.app.Activity
import android.content.Context
import android.os.IBinder
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.rule.ActivityTestRule
import android.support.test.uiautomator.UiDevice
import org.junit.runner.Description
import org.junit.runners.model.Statement

class NoAnimationTestRule<T : Activity>(activityClass: Class<T>) : ActivityTestRule<T>(activityClass) {

    private val ANIMATION_SCALE_PERMISSION = "android.permission.SET_ANIMATION_SCALE"
    private val context: Context = getInstrumentation().context
    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    override fun apply(userTest: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                grantScalePermission()
                runTest(userTest)
            }
        }
    }

    private fun runTest(base: Statement) {
        changeAnimations(false)
        try {
            base.evaluate()
        } finally {
            changeAnimations(true)
        }
    }

    private fun changeAnimations(enableAnimation: Boolean) {
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
            currentScales[i] = getAnimationStateValue(enableAnimation)
        }
        setAnimationScales.invoke(windowManagerObj, currentScales)
    }

    fun getAnimationStateValue(enableAnimation: Boolean) = when (enableAnimation) {
        true -> 1.00f
        else -> 0.00f
    }

    private fun grantScalePermission() {
        device.executeShellCommand("pm grant ${context.packageName} $ANIMATION_SCALE_PERMISSION")
    }

}