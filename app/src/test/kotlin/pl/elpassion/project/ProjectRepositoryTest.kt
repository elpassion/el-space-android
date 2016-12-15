package pl.elpassion.project

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import pl.elpassion.report.list.ReportList


class ProjectRepositoryTest {

    val cachedProjectRepository = mock<CachedProjectRepository>()
    val projectApi = mock<ReportList.ProjectApi>()
    val projectRepository = ProjectRepositoryImpl(projectApi, cachedProjectRepository)

    @Test
    fun shouldCallToApiWhenCacheIsEmpty() {
        whenever(cachedProjectRepository.hasProjects()).thenReturn(false)

        projectRepository.getProjects()

        verify(projectApi).getProjects()
    }

    @Test
    fun shouldGetOnlyFromCachedProjectsWhenProjectsCached() {
        whenever(cachedProjectRepository.hasProjects()).thenReturn(true)

        projectRepository.getProjects()

        verify(cachedProjectRepository).getPossibleProjects()
        verify(projectApi, never()).getProjects()
    }
}