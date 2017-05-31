package pl.elpassion.elspace.hub.project.last

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import pl.elpassion.elspace.hub.project.CachedProjectRepository
import pl.elpassion.elspace.hub.project.dto.newProject

class LastSelectedProjectRepositoryImplTest {
    val cache = mock<CachedProjectRepository>()
    val repository = LastSelectedProjectRepositoryImpl(cache)

    @Test
    fun shouldCallCacheForPossibleProject() {
        repository.getLastProject()

        verify(cache).getPossibleProjects()
    }

    @Test
    fun shouldProjectEqualNullWhenCacheReturnEmptyList() {
        whenever(cache.getPossibleProjects()).thenReturn(emptyList())

        assertNull(repository.getLastProject())
    }

    @Test
    fun shouldProjectEqualsFirstValueFormCache() {
        val projectOne = newProject(id = 1)
        val projectTwo = newProject(id = 2)

        whenever(cache.getPossibleProjects()).thenReturn(listOf(projectOne, projectTwo))

        assertEquals(repository.getLastProject(), projectOne)
    }
}