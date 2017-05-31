package pl.elpassion.elspace.hub.login.shortcut

interface ShortcutService {

    fun isSupportingShortcuts(): Boolean

    fun creteAppShortcuts()
}