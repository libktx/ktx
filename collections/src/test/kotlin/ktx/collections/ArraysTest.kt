package ktx.collections

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.LinkedList

/**
 * Tests utilities for libGDX custom ArrayList equivalent - Array.
 */
class ArraysTest {
  @Test
  fun `should create Array`() {
    val array = gdxArrayOf<Any>()

    assertNotNull(array)
    assertEquals(0, array.size)
  }

  @Test
  fun `should create Array with custom initial capacity`() {
    assertEquals(32, gdxArrayOf<Any>(initialCapacity = 32).items.size)
    assertEquals(128, gdxArrayOf<Any>(initialCapacity = 128).items.size)
  }

  @Test
  fun `should create Arrays with custom ordered setting`() {
    assertFalse(gdxArrayOf<Any>(ordered = false).ordered)
    assertTrue(gdxArrayOf<Any>(ordered = true).ordered)
  }

  @Test
  fun `should create Array with custom elements`() {
    val array = gdxArrayOf("1", "2", "3")

    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)
  }

  @Test
  fun `should report size of Array`() {
    val array = GdxArray.with("1", "2", "3")

    assertEquals(3, array.size())
    assertEquals(array.size, array.size())
  }

  @Test
  fun `should return 0 as null Array size`() {
    val nullArray: GdxArray<Any>? = null

    assertEquals(0, nullArray.size())
  }

  @Test
  fun `should report size of IntArray`() {
    val array = GdxIntArray.with(1, 2, 3)

    assertEquals(3, array.size())
    assertEquals(array.size, array.size())
  }

  @Test
  fun `should return 0 as null IntArray size`() {
    val nullArray: GdxIntArray? = null

    assertEquals(0, nullArray.size())
  }

  @Test
  fun `should report size of FloatArray`() {
    val array = GdxFloatArray.with(1f, 2f, 3f)

    assertEquals(3, array.size())
    assertEquals(array.size, array.size())
  }

  @Test
  fun `should return 0 as null FloatArray size`() {
    val nullArray: GdxFloatArray? = null

    assertEquals(0, nullArray.size())
  }

  @Test
  fun `should report size of BooleanArray`() {
    val array = GdxBooleanArray.with(true, false, true)

    assertEquals(3, array.size())
    assertEquals(array.size, array.size())
  }

  @Test
  fun `should return 0 as null BooleanArray size`() {
    val nullArray: GdxBooleanArray? = null

    assertEquals(0, nullArray.size())
  }

  @Test
  fun `should report empty status`() {
    assertFalse(GdxArray.with("1", "2", "3").isEmpty())
    assertTrue(GdxArray<Any>().isEmpty())
    assertTrue((null as GdxArray<Any>?).isEmpty())
  }

  @Test
  fun `should verify empty status contract`() {
    fun getArray(): GdxArray<Any>? = GdxArray.with("1")

    val array = getArray()
    if (!array.isEmpty()) {
      assertTrue(array.size == 1)
    }
  }

  @Test
  fun `should report non empty status`() {
    assertTrue(GdxArray.with("1", "2", "3").isNotEmpty())
    assertFalse(GdxArray<Any>().isNotEmpty())
    assertFalse((null as GdxArray<Any>?).isNotEmpty())
  }

  @Test
  fun `should verify non empty status contract`() {
    fun getArray(): GdxArray<Any>? = GdxArray.with("1")

    val array = getArray()
    if (array.isNotEmpty()) {
      assertTrue(array.size == 1)
    }
  }

  @Test
  fun `should return last valid index of Array`() {
    val array = GdxArray.with("1", "2", "3")

    assertEquals(2, array.lastIndex)
  }

  @Test
  fun `should return last valid index of empty Array`() {
    val emptyArray = GdxArray<Any>()

    assertEquals(-1, emptyArray.lastIndex)
  }

  @Test
  fun `should return negative last index for null Array`() {
    val nullArray: GdxArray<Any>? = null

    assertEquals(-1, nullArray.lastIndex)
  }

  @Test
  fun `should return last valid index of IntArray`() {
    val array = GdxIntArray.with(1, 2, 3)

    assertEquals(2, array.lastIndex)
  }

  @Test
  fun `should return last valid index of empty IntArray`() {
    val emptyArray = GdxIntArray()

    assertEquals(-1, emptyArray.lastIndex)
  }

  @Test
  fun `should return negative last index for null IntArray`() {
    val nullArray: GdxIntArray? = null

    assertEquals(-1, nullArray.lastIndex)
  }

  @Test
  fun `should return last valid index of FloatArray`() {
    val array = GdxFloatArray.with(1f, 2f, 3f)

    assertEquals(2, array.lastIndex)
  }

  @Test
  fun `should return last valid index of empty FloatArray`() {
    val emptyArray = GdxFloatArray()

    assertEquals(-1, emptyArray.lastIndex)
  }

  @Test
  fun `should return negative last index for null FloatArray`() {
    val nullArray: GdxFloatArray? = null

    assertEquals(-1, nullArray.lastIndex)
  }

  @Test
  fun `should return last valid index of BooleanArray`() {
    val array = GdxBooleanArray.with(true, false, true)

    assertEquals(2, array.lastIndex)
  }

  @Test
  fun `should return last valid index of empty BooleanArray`() {
    val emptyArray = GdxBooleanArray()

    assertEquals(-1, emptyArray.lastIndex)
  }

  @Test
  fun `should return negative last index for null BooleanArray`() {
    val nullArray: GdxBooleanArray? = null

    assertEquals(-1, nullArray.lastIndex)
  }

  @Test
  @Suppress("ReplaceGetOrSet")
  fun `should return alternative if element is null`() {
    val array = GdxArray.with("0", null, "2")

    assertEquals("0", array.get(0, "3"))
    assertEquals("3", array[1, "3"]) // This method is also available through square bracket operator.
    assertEquals("2", array.get(2, "3"))
    assertEquals("3", array[3, "3"])
  }

  @Test
  fun `should add all values from Iterable`() {
    val array = GdxArray<String>()

    array.addAll(listOf("1", "2", "3"))

    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)
  }

  @Test
  fun `should remove all values from Iterable`() {
    val array = GdxArray.with("1", "2", "3")

    array.removeAll(listOf("1", "2", "3"))

    assertEquals(0, array.size)
    assertFalse("1" in array)
    assertFalse("2" in array)
    assertFalse("3" in array)
  }

  @Test
  fun `should remove all values from native Array`() {
    val array = GdxArray.with("1", "2", "3")

    array.removeAll(arrayOf("1", "2", "3"))

    assertEquals(0, array.size)
    assertFalse("1" in array)
    assertFalse("2" in array)
    assertFalse("3" in array)
  }

  @Test
  fun `should add elements with += operator`() {
    val array = GdxArray.with("1")

    array += "2"

    assertEquals(gdxArrayOf("1", "2"), array)
  }

  @Test
  fun `should add Iterable with += operator`() {
    val array = GdxArray.with("1")

    array += listOf("2", "3") as Iterable<String>

    assertEquals(gdxArrayOf("1", "2", "3"), array)
  }

  @Test
  fun `should add Collection with += operator`() {
    val array = GdxArray.with("1")

    array += listOf("2", "3")

    assertEquals(gdxArrayOf("1", "2", "3"), array)
  }

  @Test
  fun `should add GdxArray with += operator`() {
    val array = GdxArray.with("1")

    array += GdxArray.with("2", "3")

    assertEquals(gdxArrayOf("1", "2", "3"), array)
  }

  @Test
  fun `should add arrays and elements with + operator`() {
    val array = GdxArray<String>()

    val result = array + "1"

    assertEquals(gdxArrayOf("1"), result)
    assertEquals(gdxArrayOf<String>(), array)

    val chained = result + "2" + "3"

    assertEquals(gdxArrayOf("1", "2", "3"), chained)
    assertEquals(gdxArrayOf("1"), result)
  }

  @Test
  fun `should add arrays and Iterables with + operator`() {
    val array = GdxArray.with("0")

    val result = array + listOf("1", "2", "3")

    assertEquals(gdxArrayOf("0", "1", "2", "3"), result)
    assertEquals(gdxArrayOf("0"), array)
  }

  @Test
  fun `should add arrays and native Arrays with + operator`() {
    val array = GdxArray.with("0")

    val result = array + arrayOf("1", "2", "3")

    assertEquals(gdxArrayOf("0", "1", "2", "3"), result)
    assertEquals(gdxArrayOf("0"), array)
  }

  @Test
  fun `should remove values with - operator`() {
    var array = GdxArray.with("1", "2", "3", "4", "5", "6")

    var result = array - "1"

    assertEquals(gdxArrayOf("2", "3", "4", "5", "6"), result)
    assertEquals(gdxArrayOf("1", "2", "3", "4", "5", "6"), array)

    array = result
    result = array - listOf("2", "3")

    assertEquals(gdxArrayOf("4", "5", "6"), result)
    assertEquals(gdxArrayOf("2", "3", "4", "5", "6"), array)

    array = result
    result = array - arrayOf("4", "5")

    assertEquals(gdxArrayOf("6"), result)
    assertEquals(gdxArrayOf("4", "5", "6"), array)
  }

  @Test
  fun `should remove values with -= operator`() {
    val array = GdxArray.with("1", "2", "3", "4", "5", "6")

    array -= "1"

    assertEquals(gdxArrayOf("2", "3", "4", "5", "6"), array)

    array -= listOf("2", "3")

    assertEquals(gdxArrayOf("4", "5", "6"), array)

    array -= arrayOf("4", "5")

    assertEquals(gdxArrayOf("6"), array)
  }

  @Test
  fun `should chain operators`() {
    val array = GdxArray.with("1", "2", "3", "4")

    val result = array + "5" - "2" + GdxArray.with("7") - GdxArray.with("4", "6")

    assertEquals(GdxArray.with("1", "3", "5", "7"), result)
  }

  @Test
  fun `should find elements with in operator`() {
    val array = GdxArray.with("1")
    val identityCheck = false // Will compare with equals(Object).

    assertTrue(array.contains("1", identityCheck)) // Standard libGDX API.
    assertTrue("1" in array)
    assertTrue(array.contains("1")) // Operator method alias.

    array.removeValue("1", identityCheck)

    assertFalse(array.contains("1", identityCheck)) // Standard libGDX API.
    assertFalse("1" in array)
    assertFalse(array.contains("1"))
  }

  @Test
  fun `should allow to iterate Array with iterator reference`() {
    val array = GdxArray.with("1", "2", "3")

    array.iterate { value, iterator -> if (value == "2") iterator.remove() }

    assertEquals(2, array.size)
    assertFalse("2" in array)
  }

  @Test
  fun `should sort elements in descending natural order`() {
    val array = GdxArray.with(1, 2, 3)

    array.sortDescending()

    assertEquals(GdxArray.with(3, 2, 1), array)
  }

  @Test
  fun `should sort elements by property`() {
    val array = GdxArray.with("Twenty-one", "Eleven", "One")

    array.sortBy { it.length }

    assertEquals(GdxArray.with("One", "Eleven", "Twenty-one"), array)
  }

  @Test
  fun `should sort elements by property in descending order`() {
    val array = GdxArray.with("One", "Eleven", "Twenty-one")

    array.sortByDescending { it.length }

    assertEquals(GdxArray.with("Twenty-one", "Eleven", "One"), array)
  }

  @Test
  fun `should remove elements from existing GdxArray`() {
    val array = GdxArray.with(1, 2, 3, 4, 5)
    val noneRemovedResult = array.removeAll { it > 10 }
    assert(!noneRemovedResult)
    assertEquals(GdxArray.with(1, 2, 3, 4, 5), array)

    val evensRemovedResult = array.removeAll { it % 2 == 0 }
    assert(evensRemovedResult)
    assertEquals(GdxArray.with(1, 3, 5), array)

    val allRemovedResult = array.removeAll { it is Number }
    assert(allRemovedResult)
    assertEquals(GdxArray<Int>(), array)

    val emptyRemoveResult = array.removeAll { it > 0 }
    assert(!emptyRemoveResult)
    assertEquals(GdxArray<Int>(), array)
  }

  @Test
  fun `should free removed elements`() {
    val array = GdxArray.with(Vector2(), Vector2(1f, 1f), Vector2(2f, 2f))
    val pool =
      object : Pool<Vector2>() {
        override fun newObject() = Vector2()
      }
    array.removeAll(pool) { it.len() > 0.5f }
    assertEquals(pool.peak, 2)
  }

  @Test
  fun `should transfer elements matching predicate`() {
    val array = GdxArray.with(0, 1, 2, 3, 4)
    val target = GdxArray.with<Int>()

    array.transfer(toArray = target) {
      it % 2 == 0
    }

    assertEquals(GdxArray.with(1, 3), array)
    assertEquals(GdxArray.with(0, 2, 4), target)
  }

  @Test
  fun `should transfer all elements`() {
    val array = GdxArray.with(0, 1, 2, 3, 4)
    val target = GdxArray.with<Int>()

    array.transfer(toArray = target) {
      true
    }

    assertEquals(GdxArray.with<Int>(), array)
    assertEquals(GdxArray.with(0, 1, 2, 3, 4), target)
  }

  @Test
  fun `should transfer no elements`() {
    val array = GdxArray.with(0, 1, 2, 3, 4)
    val target = GdxArray.with<Int>()

    array.transfer(toArray = target) {
      false
    }

    assertEquals(GdxArray.with(0, 1, 2, 3, 4), array)
    assertEquals(GdxArray.with<Int>(), target)
  }

  @Test
  fun `should not transfer any elements from empty array`() {
    val array = GdxArray.with<Int>()
    val target = GdxArray.with<Int>()

    array.transfer(toArray = target) {
      it % 2 == 0
    }

    assertEquals(GdxArray.with<Int>(), array)
    assertEquals(GdxArray.with<Int>(), target)
  }

  @Test
  fun `should transfer to wider typed array`() {
    val array = GdxArray.with("ABC", "AB", "ABC")
    val target = GdxArray<CharSequence>()

    array.transfer(target) {
      it.length < 3
    }

    assertEquals(GdxArray.with("ABC", "ABC"), array)
    assertEquals(GdxArray.with("AB" as CharSequence), target)
  }

  @Test
  fun `should retain elements from existing GdxArray`() {
    val array = GdxArray.with(1, 2, 3, 4, 5)
    val allRetainedResult = array.retainAll { it < 6 }
    assert(!allRetainedResult)
    assertEquals(GdxArray.with(1, 2, 3, 4, 5), array)

    val oddsRetainedResult = array.retainAll { it % 2 == 1 }
    assert(oddsRetainedResult)
    assertEquals(GdxArray.with(1, 3, 5), array)

    val noneRetainedResult = array.retainAll { it < 0 }
    assert(noneRetainedResult)
    assertEquals(GdxArray<Int>(), array)

    val emptyRetainResult = array.retainAll { it > 0 }
    assert(!emptyRetainResult)
    assertEquals(GdxArray<Int>(), array)
  }

  @Test
  fun `should free unretained elements`() {
    val array = GdxArray.with(Vector2(), Vector2(1f, 1f), Vector2(2f, 2f))
    val pool =
      object : Pool<Vector2>() {
        override fun newObject() = Vector2()
      }
    array.retainAll(pool) { it.len() < 0.5f }
    assertEquals(pool.peak, 2)
  }

  @Test
  fun `should map elements into a new GdxArray`() {
    val array = GdxArray.with(1, 2, 3)

    val result = array.map { it * 2 }

    assertEquals(GdxArray.with(2, 4, 6), result)
  }

  @Test
  fun `should filter elements into a new GdxArray`() {
    val array = GdxArray.with(1, 2, 3, 4, 5)

    val result = array.filter { it % 2 == 1 }

    assertEquals(GdxArray.with(1, 3, 5), result)
  }

  @Test
  fun `should flatten elements into a new GdxArray`() {
    val array = GdxArray.with(GdxArray.with(1), listOf<Int>(), LinkedList(arrayListOf(2, 3)))

    val result = array.flatten()

    assertEquals(GdxArray.with(1, 2, 3), result)
  }

  @Test
  fun `should map elements to lists and flatten them into a new GdxArray`() {
    val array = GdxArray.with(1, 2, 3)

    val result = array.flatMap { counter -> List(counter) { counter } }

    assertEquals(GdxArray.with(1, 2, 2, 3, 3, 3), result)
  }

  @Test
  fun `should convert Array to ObjectSet`() {
    val array = GdxArray.with("1", "2", "3")

    val set = array.toGdxSet()

    assertEquals(gdxSetOf("1", "2", "3"), set)
  }

  @Test
  fun `should convert Iterables to Arrays`() {
    val listAsArray = listOf("1", "2", "3").toGdxArray()

    assertEquals(gdxArrayOf("1", "2", "3"), listAsArray)
  }

  @Test
  fun `should convert native Arrays to GdxArrays`() {
    val array = arrayOf("1", "2", "3").toGdxArray()

    assertEquals(gdxArrayOf("1", "2", "3"), array)
  }

  @Test
  fun `should convert native IntArrays to GdxIntArrays`() {
    val intArray = intArrayOf(1, 2, 3).toGdxArray()

    assertEquals(GdxIntArray.with(1, 2, 3), intArray)
  }

  @Test
  fun `should convert native FloatArrays to GdxFloatArrays`() {
    val floatArray = floatArrayOf(1f, 2f, 3f).toGdxArray()

    assertEquals(3, floatArray.size)
    assertTrue(1f in floatArray)
    assertTrue(2f in floatArray)
    assertTrue(3f in floatArray)
    assertEquals(1f, floatArray[0], 0.001f)
    assertEquals(2f, floatArray[1], 0.001f)
    assertEquals(3f, floatArray[2], 0.001f)
  }

  @Test
  fun `should convert native BooleanArrays to GdxBooleanArrays`() {
    val booleanArray = booleanArrayOf(true, false, true).toGdxArray()

    assertEquals(3, booleanArray.size)
    assertEquals(true, booleanArray[0])
    assertEquals(false, booleanArray[1])
    assertEquals(true, booleanArray[2])
  }

  @Test
  @Suppress("USELESS_IS_CHECK") // Description of the API.
  fun `should provide aliases for collections with conflicting names`() {
    assertTrue(GdxArray<Any>() is com.badlogic.gdx.utils.Array<Any>)
    assertTrue(GdxIntArray() is com.badlogic.gdx.utils.IntArray)
    assertTrue(GdxFloatArray() is com.badlogic.gdx.utils.FloatArray)
    assertTrue(GdxBooleanArray() is com.badlogic.gdx.utils.BooleanArray)
    assertTrue(GdxCharArray() is com.badlogic.gdx.utils.CharArray)
    assertTrue(GdxLongArray() is com.badlogic.gdx.utils.LongArray)
    assertTrue(GdxShortArray() is com.badlogic.gdx.utils.ShortArray)
  }

  @Test
  fun `should create gdx arrays from vararg elements`() {
    assertEquals(GdxBooleanArray.with(true, false), gdxBooleanArrayOf(true, false))
    assertEquals(GdxByteArray.with(0, 1), gdxByteArrayOf(0, 1))
    assertEquals(GdxCharArray.with('0', '1'), gdxCharArrayOf('0', '1'))
    assertEquals(GdxShortArray.with(0, 1), gdxShortArrayOf(0, 1))
    assertEquals(GdxIntArray.with(0, 1), gdxIntArrayOf(0, 1))
    assertEquals(GdxLongArray.with(0L, 1L), gdxLongArrayOf(0L, 1L))
    assertEquals(GdxFloatArray.with(0.1F, 0.2F), gdxFloatArrayOf(0.1F, 0.2F))
  }
}
