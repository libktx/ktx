package ktx.collections

import org.junit.Assert.*
import org.junit.Test
import java.util.LinkedList
import java.util.NoSuchElementException

/**
 * Tests general [PooledList] utilities.
 */
class ListsTest {
  @Test
  fun `should create new empty lists`() {
    val list = gdxListOf<String>()

    assertNotNull(list)
    assertEquals(0, list.size)
    assertTrue(list.isEmpty)
  }

  @Test
  fun `should create new lists with elements`() {
    val list = gdxListOf("1", "2", "3")

    assertNotNull(list)
    assertEquals(3, list.size)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun `should create new lists from arrays`() {
    val list = arrayOf("1", "2", "3").toGdxList()

    assertEquals(gdxListOf("1", "2", "3"), list)
  }

  @Test
  fun `should create new lists from iterables`() {
    val list = sortedSetOf("1", "2", "3").toGdxList()

    assertEquals(gdxListOf("1", "2", "3"), list)
  }

  @Test
  fun `should provide alias for compatibility with other LibGDX collections`() {
    @Suppress("USELESS_IS_CHECK")
    assertTrue(GdxList(NodePool) is PooledList<Any>)
  }
}

/**
 * Tests [PooledList] implementation - a KTX LinkedList equivalent.
 */
class PooledListTest {
  @Test
  fun `should create new empty lists`() {
    val list = PooledList(NodePool) // Note that gdxListOf is the preferred way of creating PooledList.

    assertEquals(0, list.size)
    assertTrue(list.isEmpty)
    assertFalse(list.isNotEmpty)
  }

  @Test
  fun `should add elements to empty lists`() {
    val list = PooledList(NodePool)

    list.add("1")

    assertEquals(1, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("1", list.last)
  }

  @Test
  fun `should add multiple elements`() {
    val list = PooledList(NodePool)

    list.add("1")
    list.add("2")
    list.add("3")

    assertEquals(3, list.size)
    assertFalse(list.isEmpty)
    assertTrue(list.isNotEmpty)
    assertEquals("1", list.first)
    assertEquals("3", list.last)
  }

  @Test
  fun `should remove elements`() {
    val list = PooledList(NodePool)

    list.add("1")
    list.add("2")
    list.add("3")
    list.removeLast()

    assertEquals(gdxListOf("1", "2"), list)

    list.removeFirst()

    assertEquals(gdxListOf("2"), list)

    list.removeLast()

    assertEquals(gdxListOf<String>(), list)
  }

  @Test(expected = IllegalStateException::class)
  fun `should throw exception if removing first element from empty list`() {
    PooledList(NodePool).removeFirst()
  }

  @Test(expected = IllegalStateException::class)
  fun `should throw exception if removing last element from empty list`() {
    PooledList(NodePool).removeLast()
  }

  @Test
  fun `should add all elements from Arrays`() {
    val list = PooledList(NodePool)

    list.addAll(arrayOf("1", "2", "3"))

    assertEquals(gdxListOf("1", "2", "3"), list)
  }

  @Test
  fun `should add all elements from Iterables`() {
    val list = PooledList(NodePool)

    list.addAll(sortedSetOf("1", "2", "3"))

    assertEquals(gdxListOf("1", "2", "3"), list)
  }

  @Test
  fun `should add elements with + operator`() {
    val list = PooledList(NodePool)

    list + "1" + "2"

    assertEquals(gdxListOf("1", "2"), list)
  }

  @Test
  fun `should add all elements from Arrays with + operator`() {
    val list = PooledList<String>(NodePool.pool())

    list + arrayOf("1", "2", "3")

    assertEquals(gdxListOf("1", "2", "3"), list)
  }

  @Test
  fun `should add all elements from Iterables with + operator`() {
    val list = PooledList<String>(NodePool.pool())

    list + sortedSetOf("1", "2", "3")

    assertEquals(gdxListOf("1", "2", "3"), list)
  }

  @Test
  fun `should clear list return objects to the pool`() {
    val list = PooledList(NodePool)
    NodePool.clear()

    list.add("1")
    list.add("2")
    list.add("3")

    assertEquals(3, list.size)
    assertEquals(0, NodePool.free)

    list.clear()

    assertEquals(0, list.size)
    assertTrue(list.isEmpty)
    assertEquals(3, NodePool.free)
  }

  @Test
  fun `should purge list not return objects to the pool`() {
    val list = PooledList(NodePool)
    NodePool.clear()

    list.add("1")
    list.add("2")
    list.add("3")

    assertEquals(3, list.size)
    assertEquals(0, NodePool.free)

    list.purge()

    assertEquals(0, list.size)
    assertTrue(list.isEmpty)
    assertEquals(0, NodePool.free)
  }

  @Test
  fun `should map elements into a new GdxList`() {
    val list = gdxListOf(1, 2, 3)

    val result = list.map { it * 2 }

    assertEquals(gdxListOf(2, 4, 6), result)
  }

  @Test
  fun `should filter elements into a new GdxList`() {
    val list = gdxListOf(1, 2, 3, 4, 5)

    val result = list.filter { it % 2 == 1 }

    assertEquals(gdxListOf(1, 3, 5), result)
  }

  @Test
  fun `should flatten elements into a new GdxList`() {
    val list = gdxListOf(GdxArray.with(1), listOf<Int>(), LinkedList(arrayListOf(2, 3)))

    val result = list.flatten()

    assertEquals(gdxListOf(1, 2, 3), result)
  }

  @Test
  fun `should map elements to lists and flatten them into a new GdxList`() {
    val list = gdxListOf(1, 2, 3)

    val result = list.flatMap { List(it) { "" } }

    assertEquals(6, result.size)
  }

  @Test(expected = NoSuchElementException::class)
  fun `should throw exception if first element is requested from empty list`() {
    PooledList(NodePool).first
  }

  @Test(expected = NoSuchElementException::class)
  fun `should throw exception if last element is requested from empty list`() {
    PooledList(NodePool).last
  }

  @Test
  fun `should add first element lengthen the list`() {
    val list = PooledList(NodePool)

    list.first = "1"

    assertEquals(gdxListOf("1"), list)

    list.first = "2"

    assertEquals(gdxListOf("2", "1"), list)
  }

  @Test
  fun `should add last element lengthen the list`() {
    val list = PooledList(NodePool)

    list.last = "1"

    assertEquals(gdxListOf("1"), list)

    list.last = "2"

    assertEquals(gdxListOf("1", "2"), list)
  }

  @Test
  fun `should check if element is in the list with in operator`() {
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
  fun `should iterate over the list`() {
    val list = gdxListOf("1", "2", "3")
    val arrayList = arrayListOf<String>()

    list.forEach { arrayList.add(it) }

    assertEquals(arrayListOf("1", "2", "3"), arrayList)
  }

  @Test
  fun `should iterate over the list backwards`() {
    val list = gdxListOf("1", "2", "3")
    val arrayList = arrayListOf<String>()

    list.forEachReversed { arrayList.add(it) }

    assertEquals(arrayListOf("3", "2", "1"), arrayList)
  }

  @Test
  fun `should iterate over the list with non cached iterator`() {
    val list = gdxListOf("1", "2", "3")
    val arrayList = arrayListOf<String>()

    list.newIterator().forEach { arrayList.add(it) }

    assertEquals(arrayListOf("1", "2", "3"), arrayList)
  }

  @Test
  fun `should iterate over the list with iterator access`() {
    val list = gdxListOf("1", "2", "3")
    val arrayList = arrayListOf<String>()

    list.iterate { element, iterator ->
      arrayList.add(element)
      if (element == "2") iterator.remove()
    }

    assertEquals(arrayListOf("1", "2", "3"), arrayList)
    assertEquals(gdxListOf("1", "3"), list)
  }

  @Test(expected = IllegalStateException::class)
  fun `should throw exception if removing elements without iterating over the list`() {
    PooledList(NodePool).remove()
  }

  @Test(expected = IllegalStateException::class)
  fun `should throw exception if inserting elements before current value without iterating over the list`() {
    PooledList(NodePool).insertBefore("Test.")
  }

  @Test(expected = IllegalStateException::class)
  fun `should throw exception if inserting elements after current value without iterating over the list`() {
    PooledList(NodePool).insertAfter("Test.")
  }

  @Test
  fun `should remove element during iteration`() {
    val list = gdxListOf("1", "2", "3")

    list.forEach { if (it == "2") list.remove() }

    assertEquals(gdxListOf("1", "3"), list)
  }

  @Test
  fun `should insert element before current value during iteration`() {
    val list = gdxListOf("1", "2", "3")

    list.forEach { if (it == "1") list.insertBefore("0") }

    assertEquals(gdxListOf("0", "1", "2", "3"), list)
  }

  @Test
  fun `should insert element after current value during iteration`() {
    val list = gdxListOf("1", "2", "3")

    list.forEach { if (it == "3") list.insertAfter("4") }

    assertEquals(gdxListOf("1", "2", "3", "4"), list)
  }

  @Test
  fun `should convert to string`() {
    assertEquals("[]", gdxListOf<String>().toString())
    assertEquals("[single]", gdxListOf("single").toString())
    assertEquals("[one, two, three]", gdxListOf("one", "two", "three").toString())
  }

  @Test
  fun `should calculate distinct hash code for lists with same elements`() {
    val list = gdxListOf("a", "b", "c")
    val same = gdxListOf("a", "b", "c")
    val different = gdxListOf("b", "c", "a")

    assertEquals(same.hashCode(), list.hashCode())
    assertNotEquals(different.hashCode(), list.hashCode())
  }

  @Test
  fun `should properly implement equals`() {
    val list = gdxListOf("a", "b", "c")

    assertNotEquals(null, list)
    assertNotEquals("[a, b, c]", list)
    assertNotEquals(gdxListOf<String>(), list)
    assertNotEquals(GdxArray.with("a", "b", "c"), list) // No common interface.
    assertNotEquals(gdxListOf("a", "b"), list)
    assertNotEquals(gdxListOf("a", "b", "c", "d"), list)
    assertEquals(gdxListOf("a", "b", "c"), list)
  }
}
