package pl.elpassion.project

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.report.list.service.ProjectListService


class ProjectRepositoryTest {

    val cachedProjectRepository = mock<CachedProjectRepository>()
    val projectService = mock<ProjectListService>()
    val projectRepository = ProjectRepositoryImpl(projectService, cachedProjectRepository)

    @Test
    fun shouldCallToApiWhenCacheIsEmpty() {
        whenever(cachedProjectRepository.hasProjects()).thenReturn(false)

        projectRepository.getProjects()

        verify(projectService).getProjects()
    }

    @Test
    fun shouldGetOnlyFromCachedProjectsWhenProjectsCached() {
        whenever(cachedProjectRepository.hasProjects()).thenReturn(true)

        projectRepository.getProjects()

        verify(cachedProjectRepository).getPossibleProjects()
        verify(projectService, never()).getProjects()
    }
}