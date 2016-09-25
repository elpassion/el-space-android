package pl.elpassion.common

import android.content.Context
import pl.elpassion.common.Provider

object ContextProvider : Provider<Context>({
    throw NotImplementedError()
})