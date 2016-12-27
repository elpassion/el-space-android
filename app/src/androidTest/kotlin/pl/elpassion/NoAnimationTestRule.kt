package pl.elpassion


import android.Manifest.permission.SET_ANIMATION_SCALE
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Build.VERSION_CODES.M
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement


class NoAnimationTestRule() : TestRule {

    private val ANIMATION_SCALE_PERMISSION = "android.permission.SET_ANIMATION_SCALE"
    private val context: Context = getInstrumentation().context
    private val device: UiDevice = UiDevice.getInstance(getInstrumentation())

    override fun apply(userTest: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                val isAnimationEnabledOnStart = isAnyAnimationEnabled()
                if (isAnimationEnabledOnStart) {
                    tryDisableAnimation()
                }
                try {
                    userTest.evaluate()
                } finally {
                    restoreAnimationScale(isAnimationEnabledOnStart)
                }
            }
        }
    }

    private fun tryDisableAnimation() {
        initializePermission()
        disableAnimation()
    }

    private fun initializePermission() {
        when {
            permissionCanBeGrantedDynamic() -> {
                grandAnimationScaleChangePermission()
            }
            isAnimationScalePermissionDenied() -> throw RuntimeException("SET_ANIMATION_SCALE permission should be granted")
        }
    }

    private fun permissionCanBeGrantedDynamic() = Build.VERSION.SDK_INT >= M

    @TargetApi(LOLLIPOP)
    private fun grandAnimationScaleChangePermission() {
        device.executeShellCommand("pm grant ${context.packageName} $ANIMATION_SCALE_PERMISSION")
    }

    private fun isAnimationScalePermissionDenied() = PackageManager.PERMISSION_DENIED == getTargetContext()
            .checkCallingOrSelfPermission(SET_ANIMATION_SCALE)

    private fun restoreAnimationScale(currentAnimation: Boolean) =
            when (currentAnimation) {
                isAnyAnimationEnabled() -> {
                }
                true -> enableAnimation()
                false -> disableAnimation()
            }

}