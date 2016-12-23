package pl.elpassion.project.last

import pl.elpassion.common.Provider
import pl.elpassion.project.CachedProjectRepositoryProvider

object LastSelectedProjectRepositoryProvider : Provider<LastSelectedProjectRepository>({
    LastSelectedProjectRepositoryImpl(CachedProjectRepositoryProvider.get())
})