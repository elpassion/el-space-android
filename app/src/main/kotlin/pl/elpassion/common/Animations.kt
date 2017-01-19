package pl.elpassion.common

object Animations {
    private var enabled = true
    fun disable() {
        enabled = false
    }
    fun areEnabled() = enabled
}