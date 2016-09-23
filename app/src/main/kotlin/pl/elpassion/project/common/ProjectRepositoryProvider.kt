package pl.elpassion.project.common

import android.content.Context
import android.preference.PreferenceManager
import com.elpassion.android.commons.sharedpreferences.createSharedPrefs
import com.google.gson.Gson
import pl.elpassion.common.Provider

object ProjectRepositoryProvider : Provider<ProjectRepository>({

    object : ProjectRepository {
        private val PROJECTS_KEY = "projects_key"
        val repository = createSharedPrefs<List<Project>>({ PreferenceManager.getDefaultSharedPreferences(ContextProvider.get()) }, { Gson() })

        override fun saveProjects(projects: List<Project>) {
            repository.write(PROJECTS_KEY, projects)
        }

        override fun getPossibleProjects(): List<Project> {
            return repository.read(PROJECTS_KEY) ?: emptyList()
        }
    }
})

object ContextProvider : Provider<Context>({
    throw NotImplementedError()
})
