package pl.elpassion.elspace.common

object Animations {
    private var enabled = true
    fun disable() {
        enabled = false
    }
    fun areEnabled() = enabled
}