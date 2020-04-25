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
  fun `should create GdxSet`() {
    val set = gdxSetOf<Any>()

    assertNotNull(set)
    assertEquals(0, set.size)
  }

  @Test
  fun `should create GdxSet with custom initial capacity`() {
    val set = gdxSetOf<Any>(initialCapacity = 32)

    assertNotNull(set)
    assertEquals(0, set.size)
  }

  @Test
  fun `should create GdxSet with custom load factor`() {
    val set = gdxSetOf<Any>(loadFactor = 0.4f)

    assertNotNull(set)
    assertEquals(0, set.size)
  }

  @Test
  fun `should create GdxSet with custom elements`() {
    val set = gdxSetOf("1", "2", "3")

    assertEquals(3, set.size)
    assertTrue("1" in set)
    assertTrue("2" in set)
    assertTrue("3" in set)
  }

  @Test
  fun `should report size`() {
    val set: ObjectSet<String>? = ObjectSet.with("1", "2", "3")

    assertEquals(3, set.size())
    assertEquals(set!!.size, set.size())
    val nullSet: ObjectSet<Any>? = null
    assertEquals(0, nullSet.size())
  }

  @Test
  fun `should report empty status`() {
    assertFalse(ObjectSet.with("1", "2", "3").isEmpty())
    assertTrue(ObjectSet<Any>().isEmpty())
    assertTrue((null as ObjectSet<Any>?).isEmpty())
  }

  @Test
  fun `should report non empty status`() {
    assertTrue(ObjectSet.with("1", "2", "3").isNotEmpty())
    assertFalse(ObjectSet<Any>().isNotEmpty())
    assertFalse((null as ObjectSet<Any>?).isNotEmpty())
  }

  @Test
  fun `should add all values from custom iterable`() {
    val set = ObjectSet<String>()

    set.addAll(listOf("1", "2", "3"))

    assertEquals(gdxSetOf("1", "2", "3"), set)
  }

  @Test
  fun `should remove all values from custom iterable`() {
    val set = ObjectSet.with("1", "2", "3")

    set.removeAll(listOf("1", "2", "3"))

    assertEquals(gdxSetOf<String>(), set)
  }

  @Test
  fun `should remove all values from Array`() {
    val set = ObjectSet.with("1", "2", "3")

    set.removeAll(arrayOf("1", "2", "3"))

    assertEquals(gdxSetOf<String>(), set)
  }

  @Test
  fun `should add values with += operator`() {
    val set = ObjectSet.with("1", "2")

    set += "3"

    assertEquals(gdxSetOf("1", "2", "3"), set)
  }

  @Test
  fun `should add values with + operator`() {
    val set = ObjectSet<String>()

    val result = set + "1"

    assertEquals(gdxSetOf("1"), result)
    assertEquals(gdxSetOf<String>(), set)

    val chained = result + "2" + "3"

    assertEquals(gdxSetOf("1", "2", "3"), chained)
    assertEquals(gdxSetOf("1"), result)
  }

  @Test
  fun `should remove values with -= operator`() {
    val set = ObjectSet.with("1", "2", "3")

    set -= "1"

    assertEquals(gdxSetOf("2", "3"), set)

    set -= "4"

    assertEquals(gdxSetOf("2", "3"), set)
  }

  @Test
  fun `should remove values with - operator`() {
    val set = ObjectSet.with("1", "2", "3")

    val result = set - "1"

    assertEquals(gdxSetOf("2", "3"), result)
    assertEquals(gdxSetOf("1", "2", "3"), set)

    val chained = result - "2" - "3"

    assertEquals(gdxSetOf<String>(), chained)
    assertEquals(gdxSetOf("2", "3"), result)
  }

  @Test
  fun `should chain operators`() {
    val set = ObjectSet.with("1", "2", "3")

    val result = set + "5" - "2" + "7"

    assertEquals(gdxSetOf("1", "3", "5", "7"), result)
    assertEquals(gdxSetOf("1", "2", "3"), set)
  }

  @Test
  fun `should add elements from iterables with += operator`() {
    val set = ObjectSet.with("1", "2")

    set += ObjectSet.with("2", "3")

    assertEquals(ObjectSet.with("1", "2", "3"), set)
  }

  @Test
  fun `should add elements from iterables with + operator`() {
    val set = ObjectSet.with("1", "2")

    val result = set + ObjectSet.with("2", "3")

    assertEquals(ObjectSet.with("1", "2", "3"), result)
    assertEquals(ObjectSet.with("1", "2"), set)
  }

  @Test
  fun `should add elements from Arrays with += operator`() {
    val set = ObjectSet.with("1", "2")

    set += arrayOf("2", "3")

    assertEquals(ObjectSet.with("1", "2", "3"), set)
  }

  @Test
  fun `should add elements from Arrays with + operator`() {
    val set = ObjectSet.with("1", "2")

    val result = set + arrayOf("2", "3")

    assertEquals(ObjectSet.with("1", "2", "3"), result)
    assertEquals(ObjectSet.with("1", "2"), set)
  }

  @Test
  fun `should remove elements from iterables with -= operator`() {
    val set = ObjectSet.with("1", "2", "3")

    set -= ObjectSet.with("2", "3", "4")

    assertEquals(gdxSetOf("1"), set)
  }

  @Test
  fun `should remove elements from Arrays with -= operator`() {
    val set = ObjectSet.with("1", "2", "3")

    set -= arrayOf("2", "3", "4")

    assertEquals(gdxSetOf("1"), set)
  }

  @Test
  fun `should remove elements from iterables with - operator`() {
    val set = ObjectSet.with("1", "2", "3")

    val result = set - ObjectSet.with("2", "3", "4")

    assertEquals(gdxSetOf("1"), result)
    assertEquals(gdxSetOf("1", "2", "3"), set)
  }

  @Test
  fun `should remove elements from Arrays with - operator`() {
    val set = ObjectSet.with("1", "2", "3")

    val result = set - arrayOf("2", "3", "4")

    assertEquals(gdxSetOf("1"), result)
    assertEquals(gdxSetOf("1", "2", "3"), set)
  }

  @Test
  fun `should chain collection operators`() {
    val set = ObjectSet.with("1", "2", "3", "4") +
      ObjectSet.with("3", "5") -
      arrayOf("2", "4", "6") +
      arrayOf("5", "7")

    assertEquals(ObjectSet.with("1", "3", "5", "7"), set)
  }

  @Test
  fun `should allow to iterate with iterator reference`() {
    val set = ObjectSet.with("1", "2", "3")

    set.iterate { value, iterator -> if (value == "2") iterator.remove() }

    assertEquals(2, set.size)
    assertFalse("2" in set)
  }

  @Test
  fun `should map elements into a new GdxSet`() {
    val set = GdxSet.with(1, 2, 3)

    val result = set.map { it * 2 }

    assertEquals(gdxSetOf(4, 2, 6), result)
  }

  @Test
  fun `should filter elements into a new GdxSet`() {
    val set = GdxSet.with(1, 2, 3, 4, 5)

    val result = set.filter { it % 2 == 1 }

    assertEquals(gdxSetOf(3, 1, 5), result)
  }

  @Test
  fun `should flatten elements into a new GdxSet`() {
    val set = GdxSet.with(GdxArray.with(1, 2), listOf<Int>(), LinkedList(arrayListOf(2, 3)))

    val result = set.flatten()

    assertEquals(gdxSetOf(1, 2, 3), result)
  }

  @Test
  fun `should map elements to lists and flatten them into a new GdxSet`() {
    val set = GdxSet.with(1, 2, 3)

    val result = set.flatMap { count -> List(count) { it } }

    assertEquals(gdxSetOf(0, 1, 2), result)
  }

  @Test
  fun `should convert Set to Array`() {
    val setAsArray = ObjectSet.with("1", "2", "3").toGdxArray()

    assertEquals(3, setAsArray.size)
    assertTrue("1" in setAsArray)
    assertTrue("2" in setAsArray)
    assertTrue("3" in setAsArray)
  }

  @Test
  fun `should convert iterables to Sets`() {
    val listAsSet = listOf("1", "2", "3").toGdxSet()

    assertEquals(3, listAsSet.size)
    assertTrue("1" in listAsSet)
    assertTrue("2" in listAsSet)
    assertTrue("3" in listAsSet)
  }

  @Test
  fun `should convert Arrays to Sets`() {
    val arrayAsSet = arrayOf("1", "2", "3").toGdxSet()

    assertEquals(3, arrayAsSet.size)
    assertTrue("1" in arrayAsSet)
    assertTrue("2" in arrayAsSet)
    assertTrue("3" in arrayAsSet)
  }

  @Test
  fun `should convert IntArrays to IntSets`() {
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
