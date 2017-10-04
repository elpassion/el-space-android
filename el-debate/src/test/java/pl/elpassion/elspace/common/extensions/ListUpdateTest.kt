package pl.elpassion.elspace.common.extensions

import com.elpassion.android.commons.recycler.basic.WithStableId
import org.junit.Assert.*
import org.junit.Test

class ListUpdateTest {

    class MyType(override val id: Long) : WithStableId

    @Test
    fun shouldRemoveItemWhenIdMatched() {
        val oldItem = MyType(1)
        val list = mutableListOf(oldItem)
        list.update(MyType(1))
        assertNotEquals(list[0], oldItem)
    }

    @Test
    fun shouldAddItemToCorrectPositionWhenIdMatched() {
        val list = mutableListOf(MyType(1))
        val newItem = MyType(1)
        list.update(newItem)
        assertEquals(list[0], newItem)
    }

    @Test
    fun shouldHaveCorrectSizeWhenIdMatched() {
        val list = mutableListOf(MyType(1), MyType(2))
        val newItem = MyType(1)
        list.update(newItem)
        assertTrue(list.size == 2)
    }

    @Test
    fun shouldNotUpdateItemsWhenIdNotMatched() {
        val one = MyType(1)
        val two = MyType(2)
        val list = mutableListOf(one, two)
        list.update(MyType(3))
        assertEquals(list[0], one)
        assertEquals(list[1], two)
    }

    @Test
    fun shouldAddItemToEmptyList() {
        val list = mutableListOf<MyType>()
        val newItem = MyType(1)
        list.update(newItem)
        assertEquals(list[0], newItem)
    }

    @Test
    fun shouldAddItemWhenIdNotMatched() {
        val list = mutableListOf(MyType(1), MyType(2))
        val newItem = MyType(3)
        list.update(newItem)
        assertEquals(list[2], newItem)
    }

    @Test
    fun shouldHaveCorrectSizeWhenIdNotMatched() {
        val list = mutableListOf(MyType(1), MyType(2))
        val newItem = MyType(3)
        list.update(newItem)
        assertTrue(list.size == 3)
    }

    @Test
    fun shouldAddItemOnCorrectListIndex() {
        val list = mutableListOf(MyType(1), MyType(3))
        val newItem = MyType(2)
        list.update(newItem)
        assertEquals(list[1], newItem)
    }

    @Test
    fun shouldHaveCorrectSizeWhenNewItemIdIsLower() {
        val list = mutableListOf(MyType(2), MyType(3))
        list.update(MyType(1))
        assertTrue(list.size == 3)
    }

    @Test
    fun shouldHaveCorrectOrderWhenNewItemIdIsLower() {
        val three = MyType(3)
        val six = MyType(6)
        val list = mutableListOf(three, six)
        val one = MyType(1)
        list.update(one)
        assertEquals(list[0], one)
        assertEquals(list[1], three)
        assertEquals(list[2], six)
    }

    @Test
    fun shouldReturnCorrectPositionWhenListIsEmpty() {
        val list = mutableListOf<MyType>()
        val position = list.update(MyType(1))
        assertEquals(position, 0)
    }

    @Test
    fun shouldReturnCorrectPositionWhenIdMatched() {
        val list = mutableListOf(MyType(1))
        val position = list.update(MyType(1))
        assertEquals(position, 0)
    }

    @Test
    fun shouldReturnCorrectPositionWhenIdNotMatched() {
        val list = mutableListOf(MyType(1))
        val position = list.update(MyType(2))
        assertEquals(position, 1)
    }
}