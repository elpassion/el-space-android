package pl.elpassion.login.schortcut

interface ShortcutService {

    fun isSupportingShortcuts(): Boolean

    fun creteAppShortcuts()
}