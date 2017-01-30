package pl.elpassion.elspace.hub.project.last

import pl.elpassion.elspace.common.Provider
import pl.elpassion.elspace.hub.project.CachedProjectRepositoryProvider

object LastSelectedProjectRepositoryProvider : Provider<LastSelectedProjectRepository>({
    LastSelectedProjectRepositoryImpl(CachedProjectRepositoryProvider.get())
})