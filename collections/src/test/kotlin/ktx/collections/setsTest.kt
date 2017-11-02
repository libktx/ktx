package ktx.collections

import com.badlogic.gdx.utils.ObjectSet
import org.junit.Assert.*
import org.junit.Test
import java.util.LinkedList

/**
 * Tests utilities for LibGDX custom HashSet equivalent - [ObjectSet].
 */
class SetsTest {
  @Test
  fun shouldCreateSet() {
    val set = gdxSetOf<Any>()

    assertNotNull(set)
    assertEquals(0, set.size)
  }

  @Test
  fun shouldCreateSetsWithCustomInitialCapacity() {
    val set = gdxSetOf<Any>(initialCapacity = 32)

    assertNotNull(set)
    assertEquals(0, set.size)
  }

  @Test
  fun shouldCreateSetsWithCustomLoadFactor() {
    val set = gdxSetOf<Any>(loadFactor = 0.4f)

    assertNotNull(set)
    assertEquals(0, set.size)
  }

  @Test
  fun shouldCreateSetsWithCustomElements() {
    val set = gdxSetOf("1", "2", "3")
    assertEquals(3, set.size)
    assertTrue("1" in set)
    assertTrue("2" in set)
    assertTrue("3" in set)
  }

  @Test
  fun shouldReportSize() {
    val set: ObjectSet<String>? = ObjectSet.with("1", "2", "3")
    assertEquals(3, set.size())
    assertEquals(set!!.size, set.size())
    val nullSet: ObjectSet<Any>? = null
    assertEquals(0, nullSet.size())
  }

  @Test
  fun shouldReportEmptyStatus() {
    val set: ObjectSet<String>? = ObjectSet.with("1", "2", "3")
    assertFalse(set.isEmpty())
    val emptySet = ObjectSet<Any>()
    assertTrue(emptySet.isEmpty())
    val nullSet: ObjectSet<Any>? = null
    assertTrue(nullSet.isEmpty())
  }

  @Test
  fun shouldReportNonEmptyStatus() {
    val set: ObjectSet<String>? = ObjectSet.with("1", "2", "3")
    assertTrue(set.isNotEmpty())
    val emptySet = ObjectSet<Any>()
    assertFalse(emptySet.isNotEmpty())
    val nullSet: ObjectSet<Any>? = null
    assertFalse(nullSet.isNotEmpty())
  }

  @Test
  fun shouldAddAllValuesFromCustomIterable() {
    val set = ObjectSet<String>()
    assertEquals(0, set.size)
    assertFalse("1" in set)
    assertFalse("2" in set)
    assertFalse("3" in set)

    set.addAll(listOf("1", "2", "3"))
    assertEquals(3, set.size)
    assertTrue("1" in set)
    assertTrue("2" in set)
    assertTrue("3" in set)
  }

  @Test
  fun shouldRemoveAllValuesFromCustomIterable() {
    val set = ObjectSet.with("1", "2", "3")
    assertEquals(3, set.size)
    assertTrue("1" in set)
    assertTrue("2" in set)
    assertTrue("3" in set)

    set.removeAll(listOf("1", "2", "3"))
    assertEquals(0, set.size)
    assertFalse("1" in set)
    assertFalse("2" in set)
    assertFalse("3" in set)
  }

  @Test
  fun shouldRemoveAllValuesFromArray() {
    val set = ObjectSet.with("1", "2", "3")
    assertEquals(3, set.size)
    assertTrue("1" in set)
    assertTrue("2" in set)
    assertTrue("3" in set)

    set.removeAll(arrayOf("1", "2", "3"))
    assertEquals(0, set.size)
    assertFalse("1" in set)
    assertFalse("2" in set)
    assertFalse("3" in set)
  }

  @Test
  fun shouldAddValuesWithPlusOperator() {
    val set = ObjectSet<String>()
    assertEquals(0, set.size)
    set + "1"
    assertEquals(1, set.size)
    assertTrue("1" in set)
    set + "2" + "3"
    assertEquals(3, set.size)
    assertTrue("2" in set)
    assertTrue("3" in set)
  }

  @Test
  fun shouldRemoveValuesWithMinusOperator() {
    val set = ObjectSet.with("1", "2", "3")
    assertEquals(3, set.size)
    set - "1"
    assertEquals(2, set.size)
    assertFalse("1" in set)
    set - "2" - "3"
    assertEquals(0, set.size)
    assertFalse("2" in set)
    assertFalse("3" in set)
  }

  @Test
  fun shouldChainOperators() {
    val set = ObjectSet.with("1", "2", "3")
    set + "5" - "2" + "7"
    assertEquals(ObjectSet.with("1", "3", "5", "7"), set)
  }

  @Test
  fun shouldAddElementsFromIterablesWithPlusOperator() {
    val set = ObjectSet<String>()
    assertEquals(0, set.size)

    set + ObjectSet.with("1", "2", "3")
    assertEquals(3, set.size)
    assertTrue("1" in set)
    assertTrue("2" in set)
    assertTrue("3" in set)
  }

  @Test
  fun shouldAddElementsFromArraysWithPlusOperator() {
    val set = ObjectSet<String>()
    assertEquals(0, set.size)

    set + arrayOf("1", "2", "3")
    assertEquals(3, set.size)
    assertTrue("1" in set)
    assertTrue("2" in set)
    assertTrue("3" in set)
  }

  @Test
  fun shouldRemovedElementsFromIterablesWithMinusOperator() {
    val set = ObjectSet.with("1", "2", "3")
    assertEquals(3, set.size)
    assertTrue("1" in set)
    assertTrue("2" in set)
    assertTrue("3" in set)

    set - ObjectSet.with("1", "2", "3")
    assertEquals(0, set.size)
    assertFalse("1" in set)
    assertFalse("2" in set)
    assertFalse("3" in set)
  }

  @Test
  fun shouldRemovedElementsFromArraysWithMinusOperator() {
    val set = ObjectSet.with("1", "2", "3")
    assertEquals(3, set.size)
    assertTrue("1" in set)
    assertTrue("2" in set)
    assertTrue("3" in set)

    set - arrayOf("1", "2", "3")
    assertEquals(0, set.size)
    assertFalse("1" in set)
    assertFalse("2" in set)
    assertFalse("3" in set)
  }

  @Test
  fun shouldChainCollectionOperators() {
    val set = ObjectSet.with("1", "2", "3", "4") +
        ObjectSet.with("3", "5") -
        arrayOf("2", "4", "6") +
        arrayOf("5", "7")
    assertEquals(ObjectSet.with("1", "3", "5", "7"), set)
  }

  @Test
  fun shouldAllowToIterateWithIteratorReference() {
    val set = ObjectSet.with("1", "2", "3")
    assertEquals(3, set.size)
    set.iterate { value, iterator -> if (value == "2") iterator.remove() }
    assertEquals(2, set.size)
    assertFalse("2" in set)
  }

  @Test
  fun `should map elements into a new GdxSet`() {
    val set = GdxSet.with(1, 2, 3)
    val result = set.map { it * 2 }

    assertTrue(2 in result)
    assertTrue(4 in result)
    assertTrue(6 in result)
  }

  @Test
  fun `should filter elements into a new GdxSet`() {
    val set = GdxSet.with(1, 2, 3, 4, 5)
    val result = set.filter { it % 2 == 1 }

    assertEquals(3, result.size)
    assertTrue(1 in result)
    assertTrue(3 in result)
    assertTrue(5 in result)
  }

  @Test
  fun `should flatten elements into a new GdxSet`() {
    val set = GdxSet.with(GdxArray.with(1, 2), listOf<Int>(), LinkedList(arrayListOf(2, 3)))
    val result = set.flatten()

    assertEquals(3, result.size)
    assertTrue(1 in result)
    assertTrue(2 in result)
    assertTrue(3 in result)
  }

  @Test
  fun `should map elements to lists and flatten them into a new GdxSet`() {
    val set = GdxSet.with(1, 2, 3)
    val result = set.flatMap { count -> List(count) { it } }

    assertEquals(3, result.size)
    assertTrue(0 in result)
    assertTrue(1 in result)
    assertTrue(2 in result)
  }

  @Test
  fun shouldConvertSetToArray() {
    val setAsArray = ObjectSet.with("1", "2", "3").toGdxArray()
    assertEquals(3, setAsArray.size)
    assertTrue("1" in setAsArray)
    assertTrue("2" in setAsArray)
    assertTrue("3" in setAsArray)
  }

  @Test
  fun shouldConvertIterablesToSets() {
    val listAsSet = listOf("1", "2", "3").toGdxSet()
    assertEquals(3, listAsSet.size)
    assertTrue("1" in listAsSet)
    assertTrue("2" in listAsSet)
    assertTrue("3" in listAsSet)
  }

  @Test
  fun shouldConvertArraysToSets() {
    val arrayAsSet = arrayOf("1", "2", "3").toGdxSet()
    assertEquals(3, arrayAsSet.size)
    assertTrue("1" in arrayAsSet)
    assertTrue("2" in arrayAsSet)
    assertTrue("3" in arrayAsSet)
  }

  @Test
  fun shouldConvertIntArraysToIntSets() {
    val arrayAsSet = intArrayOf(1, 2, 3).toGdxSet()
    assertEquals(3, arrayAsSet.size)
    assertTrue(1 in arrayAsSet)
    assertTrue(2 in arrayAsSet)
    assertTrue(3 in arrayAsSet)
  }

  @Test
  fun `should provide alias for compatibility with other LibGDX collections`() {
    @Suppress("USELESS_IS_CHECK")
    assertTrue(GdxSet<Any>() is ObjectSet<Any>)
  }
}
