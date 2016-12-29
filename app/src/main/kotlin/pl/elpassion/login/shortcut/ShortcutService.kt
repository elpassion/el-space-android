package pl.elpassion.login.shortcut

interface ShortcutService {

    fun isSupportingShortcuts(): Boolean

    fun creteAppShortcuts()
}