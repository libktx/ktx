package ktx.collections

import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * Tests general [PooledList] utilities.
 * @author MJ
 */
class ListsTest {
  @Test
  fun shouldCreateNewLists() {
    val list = gdxListOf<String>()
    assertNotNull(list)
    assertEquals(0, list.size)
    assertTrue(list.isEmpty)
  }

  @Test
  fun shouldCreateNewListsWithElements() {
    val list = gdxListOf("1", "2", "3")
    assertNotNull(list)
    assertEquals(3, list.size)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun shouldCreateNewListsFromArrays() {
    val list = arrayOf("1", "2", "3").toGdxList()
    assertNotNull(list)
    assertEquals(3, list.size)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun shouldCreateNewListsFromIterables() {
    val list = sortedSetOf("1", "2", "3").toGdxList()
    assertNotNull(list)
    assertEquals(3, list.size)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun `should provide alias for compatibility with other LibGDX collections`() {
    assertTrue(GdxList<Any>(NodePool) is PooledList<Any>)
  }
}

/**
 * Tests [PooledList] implementation - a KTX LinkedList equivalent.
 * @author MJ
 */
class PooledListTest {
  @Test
  fun shouldCreateEmptyLists() {
    val list = PooledList(NodePool) // Note that gdxListOf is the preferred way of creating PooledList.
    assertEquals(0, list.size)
    assertTrue(list.isEmpty)
    assertFalse(list.isNotEmpty)
  }

  @Test
  fun shouldAddElements() {
    val list = PooledList(NodePool)
    assertTrue(list.isEmpty)
    list.add("1")
    assertEquals(1, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("1", list.last)
  }

  @Test
  fun shouldAddMultipleElements() {
    val list = PooledList(NodePool)
    assertTrue(list.isEmpty)
    list.add("1")
    assertEquals(1, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("1", list.last)
    list.add("2")
    list.add("3")
    assertEquals(3, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun shouldRemoveElements() {
    val list = PooledList(NodePool)
    assertTrue(list.isEmpty)
    list.add("1")
    list.add("2")
    list.add("3")
    assertEquals(3, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
    list.removeLast()
    assertEquals(2, list.size)
    assertEquals("1", list.first)
    assertEquals("2", list.last)
    list.removeFirst()
    assertEquals(1, list.size)
    assertEquals("2", list.first)
    assertEquals("2", list.last)
    list.removeLast()
    assertTrue(list.isEmpty)
    assertEquals(0, list.size)
  }

  @Test(expected = IllegalStateException::class)
  fun shouldThrowExceptionIfRemovingFirstElementFromEmptyList() {
    PooledList(NodePool).removeFirst()
  }

  @Test(expected = IllegalStateException::class)
  fun shouldThrowExceptionIfRemovingLastElementFromEmptyList() {
    PooledList(NodePool).removeLast()
  }

  @Test
  fun shouldAddAllElementsFromArrays() {
    val list = PooledList(NodePool)
    assertTrue(list.isEmpty)
    list.addAll(arrayOf("1", "2", "3"))
    assertEquals(3, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun shouldAddAllElementsFromIterables() {
    val list = PooledList(NodePool)
    assertTrue(list.isEmpty)
    list.addAll(sortedSetOf("1", "2", "3"))
    assertEquals(3, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun shouldAddElementsWithPlusOperator() {
    val list = PooledList(NodePool)
    assertTrue(list.isEmpty)
    list + "1" + "2"
    assertEquals(2, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("2", list.last)
  }

  @Test
  fun shouldAddAllElementsFromArraysWithPlusOperator() {
    val list = PooledList<String>(NodePool.pool())
    assertTrue(list.isEmpty)
    list + arrayOf("1", "2", "3")
    assertEquals(3, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun shouldAddAllElementsFromIterablesWithPlusOperator() {
    val list = PooledList<String>(NodePool.pool())
    assertTrue(list.isEmpty)
    list + sortedSetOf("1", "2", "3")
    assertEquals(3, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun shouldClearList() {
    val list = PooledList(NodePool)
    NodePool.clear()
    assertEquals(0, NodePool.free)
    assertTrue(list.isEmpty)
    list.add("1")
    list.add("2")
    list.add("3")
    assertEquals(3, list.size)
    list.clear()
    assertEquals(0, list.size)
    assertTrue(list.isEmpty)
    assertEquals(3, NodePool.free) // Nodes should be returned to the pool.
  }

  @Test
  fun shouldPurgeList() {
    val list = PooledList(NodePool)
    NodePool.clear()
    assertEquals(0, NodePool.free)
    assertTrue(list.isEmpty)
    list.add("1")
    list.add("2")
    list.add("3")
    assertEquals(3, list.size)
    list.purge()
    assertEquals(0, list.size)
    assertTrue(list.isEmpty)
    assertEquals(0, NodePool.free) // Nodes should not be returned to the pool.
  }

  @Test
  fun `should map elements into a new GdxList`() {
    val list = gdxListOf(1, 2, 3)
    val result = list.map { it * 2 }

    assertTrue(result is GdxList)
    assertEquals(3, result.size)
    assertEquals(2, result.first)
    assertEquals(6, result.last)
  }

  @Test
  fun `should filter elements into a new GdxList`() {
    val list = gdxListOf(1, 2, 3, 4, 5)
    val result = list.filter { it % 2 == 1 }

    assertTrue(result is GdxList)
    assertEquals(3, result.size)
    assertEquals(1, result.first)
    assertEquals(5, result.last)
  }

  @Test
  fun `should flatten elements into a new GdxList`() {
    val list = gdxListOf(GdxArray.with(1), listOf<Int>(), LinkedList(arrayListOf(2, 3)))
    val result = list.flatten()

    assertTrue(result is GdxList)
    assertEquals(3, result.size)
    assertEquals(3, result.size)
    assertEquals(1, result.first)
    assertEquals(3, result.last)
  }

  @Test
  fun `should map elements to lists and flatten them into a new GdxList`() {
    val list = gdxListOf(1, 2, 3)
    val result = list.flatMap { List(it) { "" } }

    assertTrue(result is GdxList)
    assertEquals(6, result.size)
  }

  @Test(expected = NoSuchElementException::class)
  fun shouldThrowExceptionIfFirstElementIsRequestedButListIsEmpty() {
    PooledList(NodePool).first
  }

  @Test(expected = NoSuchElementException::class)
  fun shouldThrowExceptionIfLastElementIsRequestedButListIsEmpty() {
    PooledList(NodePool).last
  }

  @Test
  fun shouldAddFirstElement() {
    val list = PooledList(NodePool)
    assertTrue(list.isEmpty)

    list.first = "1"
    assertEquals(1, list.size)
    assertEquals("1", list.first)

    list.first = "2"
    assertEquals(2, list.size)
    assertEquals("2", list.first)
    assertEquals("1", list.last)
  }

  @Test
  fun shouldAddLastElement() {
    val list = PooledList(NodePool)
    assertTrue(list.isEmpty)

    list.last = "1"
    assertEquals(1, list.size)
    assertEquals("1", list.last)

    list.last = "2"
    assertEquals(2, list.size)
    assertEquals("1", list.first)
    assertEquals("2", list.last)
  }

  @Test
  fun shouldCheckIfElementIsInTheListWithInOperator() {
    val list = PooledList(NodePool)
    assertFalse("1" in list) // Tests contains operator method.
    assertFalse("2" in list)
    list + "1"
    assertTrue("1" in list)
    assertFalse("2" in list)
    list + "2"
    assertTrue("1" in list)
    assertTrue("2" in list)
    list.removeFirst()
    assertFalse("1" in list)
    assertTrue("2" in list)
  }

  @Test
  fun shouldIterateOverList() {
    val list = PooledList<String>(NodePool.pool())
    list + arrayOf("1", "2", "3")
    val arrayList = arrayListOf<String>()
    list.forEach { arrayList.add(it) }
    assertEquals("1", arrayList[0])
    assertEquals("2", arrayList[1])
    assertEquals("3", arrayList[2])
  }

  @Test
  fun shouldIterateOverListBackwards() {
    val list = PooledList<String>(NodePool.pool())
    list + arrayOf("1", "2", "3")
    val arrayList = arrayListOf<String>()
    list.forEachReversed { arrayList.add(it) }
    assertEquals("3", arrayList[0])
    assertEquals("2", arrayList[1])
    assertEquals("1", arrayList[2])
  }

  @Test
  fun shouldIterateOverListWithNonCachedIterator() {
    val list = PooledList<String>(NodePool.pool())
    list + arrayOf("1", "2", "3")
    val arrayList = arrayListOf<String>()
    list.newIterator().forEach { arrayList.add(it) }
    assertEquals("1", arrayList[0])
    assertEquals("2", arrayList[1])
    assertEquals("3", arrayList[2])
  }

  @Test
  fun shouldIterateOverListWithIteratorAccess() {
    val list = PooledList<String>(NodePool.pool())
    list + arrayOf("1", "2", "3")
    val arrayList = arrayListOf<String>()
    list.iterate { element, iterator ->
      arrayList.add(element)
      if (element == "2") iterator.remove()
    }
    assertEquals("1", arrayList[0])
    assertEquals("2", arrayList[1])
    assertEquals("3", arrayList[2])
    assertTrue("1" in list)
    assertFalse("2" in list) // Removed by iterator.
    assertTrue("3" in list)
  }

  @Test(expected = IllegalStateException::class)
  fun shouldThrowExceptionIfRemovingElementsWithoutIteratingOverTheList() {
    PooledList(NodePool).remove()
  }

  @Test(expected = IllegalStateException::class)
  fun shouldThrowExceptionIfInsertingElementsBeforeCurrentValueWithoutIteratingOverTheList() {
    PooledList(NodePool).insertBefore("Test.")
  }

  @Test(expected = IllegalStateException::class)
  fun shouldThrowExceptionIfInsertingElementsAfterCurrentValueWithoutIteratingOverTheList() {
    PooledList(NodePool).insertAfter("Test.")
  }

  @Test
  fun shouldRemoveElementsDuringIteration() {
    val list = PooledList<String>(NodePool.pool())
    list + arrayOf("1", "2", "3")
    list.forEach { if (it == "2") list.remove() }
    assertTrue("1" in list)
    assertFalse("2" in list)
    assertTrue("3" in list)
    assertEquals(2, list.size)
  }

  @Test
  fun shouldInsertElementsBeforeCurrentValueDuringIteration() {
    val list = PooledList<String>(NodePool.pool())
    list + arrayOf("1", "2", "3")
    list.forEach { if (it == "1") list.insertBefore("0") }
    assertTrue("0" in list)
    assertTrue("1" in list)
    assertTrue("2" in list)
    assertTrue("3" in list)
    assertEquals(4, list.size)
    assertEquals("0", list.first)
  }

  @Test
  fun shouldInsertElementsAfterCurrentValueDuringIteration() {
    val list = PooledList<String>(NodePool.pool())
    list + arrayOf("1", "2", "3")
    list.forEach { if (it == "3") list.insertAfter("4") }
    assertTrue("1" in list)
    assertTrue("2" in list)
    assertTrue("3" in list)
    assertTrue("4" in list)
    assertEquals(4, list.size)
    assertEquals("4", list.last)
  }

  @Test
  fun shouldConvertToString() {
    assertEquals("[]", gdxListOf<String>().toString())
    assertEquals("[single]", gdxListOf("single").toString())
    assertEquals("[one, two, three]", gdxListOf("one", "two", "three").toString())
  }
}
