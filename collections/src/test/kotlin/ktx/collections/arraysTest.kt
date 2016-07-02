package ktx.collections

import org.junit.Assert.*
import org.junit.Test
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests utilities for LibGDX custom ArrayList equivalent - Array.
 * @author MJ
 */
class ArraysTest {
  @Test
  fun shouldCreateArray() {
    assertNotNull(gdxArrayOf<Any>())
  }

  @Test
  fun shouldCreateArrayWithCustomInitialCapacity() {
    assertEquals(32, gdxArrayOf<Any>(initialCapacity = 32).items.size)
    assertEquals(128, gdxArrayOf<Any>(initialCapacity = 128).items.size)
  }

  @Test
  fun shouldCreateArraysWithCustomOrderedSetting() {
    assertFalse(gdxArrayOf<Any>(ordered = false).ordered)
    assertTrue(gdxArrayOf<Any>(ordered = true).ordered)
  }

  @Test
  fun shouldCreateArraysWithCustomElements() {
    val array = gdxArrayOf("1", "2", "3")
    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)
  }

  @Test
  fun shouldReportSize() {
    val array: GdxArray<String>? = GdxArray.with("1", "2", "3")
    assertEquals(3, array.size())
    assertEquals(array!!.size, array.size())
    val nullArray: GdxArray<Any>? = null
    assertEquals(0, nullArray.size())
  }

  @Test
  fun shouldReportEmptyStatus() {
    val array: GdxArray<String>? = GdxArray.with("1", "2", "3")
    assertFalse(array.isEmpty())
    val emptyArray = GdxArray<Any>()
    assertTrue(emptyArray.isEmpty())
    val nullArray: GdxArray<Any>? = null
    assertTrue(nullArray.isEmpty())
  }

  @Test
  fun shouldReportNonEmptyStatus() {
    val array: GdxArray<String>? = GdxArray.with("1", "2", "3")
    assertTrue(array.isNotEmpty())
    val emptyArray = GdxArray<Any>()
    assertFalse(emptyArray.isNotEmpty())
    val nullArray: GdxArray<Any>? = null
    assertFalse(nullArray.isNotEmpty())
  }

  @Test
  fun shouldReturnLastElement() {
    assertEquals("3", GdxArray.with("1", "2", "3").getLast())
  }

  @Test
  fun shouldReturnNullAsLastElementIfEmpty() {
    assertNull(GdxArray<Any>().getLast())
  }

  @Test
  fun shouldRemoveLastElement() {
    val array = GdxArray.with("1", "2", "3")
    assertEquals(3, array.size)
    val last = array.removeLast()
    assertEquals("3", last)
    assertEquals(2, array.size)
  }

  @Test
  fun shouldReturnAlternativeIfElementIsNull() {
    val array = GdxArray.with("0", null, "2")
    assertEquals("0", array.getOrElse(0, "3"))
    assertEquals("3", array.getOrElse(1, "3"))
    assertEquals("2", array.getOrElse(2, "3"))
    assertEquals("3", array.getOrElse(3, "3"))
  }

  @Test
  fun shouldAddAllValuesFromCustomIterable() {
    val array = GdxArray<String>()
    assertEquals(0, array.size)
    assertFalse("1" in array)
    assertFalse("2" in array)
    assertFalse("3" in array)

    array.addAll(listOf("1", "2", "3"))
    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)
  }


  @Test
  fun shouldRemoveAllValuesFromCustomIterable() {
    val array = GdxArray.with("1", "2", "3")
    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)

    array.removeAll(listOf("1", "2", "3"))
    assertEquals(0, array.size)
    assertFalse("1" in array)
    assertFalse("2" in array)
    assertFalse("3" in array)
  }

  @Test
  fun shouldRemoveAllValuesFromNativeArray() {
    val array = GdxArray.with("1", "2", "3")
    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)

    array.removeAll(arrayOf("1", "2", "3"))
    assertEquals(0, array.size)
    assertFalse("1" in array)
    assertFalse("2" in array)
    assertFalse("3" in array)
  }

  @Test
  fun shouldAddValuesWithPlusOperator() {
    val array = GdxArray<String>()
    assertEquals(0, array.size)

    array + "1"
    assertEquals(1, array.size)
    assertTrue("1" in array)
    assertEquals("1", array[0])

    array + "2" + "3"
    assertEquals(3, array.size)
    assertTrue("2" in array)
    assertTrue("3" in array)
    assertEquals("2", array[1])
    assertEquals("3", array[2])
  }

  @Test
  fun shouldAddIterablesWithPlusOperator() {
    val array = GdxArray<String>()
    assertEquals(0, array.size)

    array + listOf("1", "2", "3")
    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)
    assertEquals("1", array[0])
    assertEquals("2", array[1])
    assertEquals("3", array[2])
  }

  @Test
  fun shouldAddArraysWithPlusOperator() {
    val array = GdxArray<String>()
    assertEquals(0, array.size)

    array + arrayOf("1", "2", "3")
    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)
    assertEquals("1", array[0])
    assertEquals("2", array[1])
    assertEquals("3", array[2])
  }

  @Test
  fun shouldRemoveValuesWithMinusOperator() {
    val array = GdxArray.with("1", "2", "3", "4", "5", "6")
    assertEquals(6, array.size)
    array - "1"
    assertEquals(5, array.size)
    assertFalse("1" in array)
    array - "2" - "3"
    assertEquals(3, array.size)
    assertFalse("2" in array)
    assertFalse("3" in array)
    array - listOf("4", "5")
    assertEquals(1, array.size)
    assertFalse("4" in array)
    assertFalse("5" in array)
    array - arrayOf("6", "7")
    assertEquals(0, array.size)
    assertFalse("6" in array)
    assertFalse("7" in array)
  }

  @Test
  fun shouldChainOperators() {
    val array = GdxArray.with("1", "2", "3", "4")
    array + "5" - "2" + GdxArray.with("7") - GdxArray.with("4", "6")
    assertEquals(GdxArray.with("1", "3", "5", "7"), array)
  }

  @Test
  fun shouldAllowToIterateWithIteratorReference() {
    val array = GdxArray.with("1", "2", "3")
    assertEquals(3, array.size)
    array.iterate { value, iterator -> if (value == "2") iterator.remove() }
    assertEquals(2, array.size)
    assertFalse("2" in array)
  }

  @Test
  fun shouldConvertArrayToSet() {
    val array = GdxArray.with("1", "2", "3").toGdxSet()
    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)
  }

  @Test
  fun shouldConvertIterablesToArrays() {
    val listAsArray = listOf("1", "2", "3").toGdxArray()
    assertEquals(3, listAsArray.size)
    assertTrue("1" in listAsArray)
    assertTrue("2" in listAsArray)
    assertTrue("3" in listAsArray)
    assertEquals("1", listAsArray[0])
    assertEquals("2", listAsArray[1])
    assertEquals("3", listAsArray[2])
  }

  @Test
  fun shouldConvertNativeArraysToGdxArrays() {
    val array = arrayOf("1", "2", "3").toGdxArray()
    assertEquals(3, array.size)
    assertTrue("1" in array)
    assertTrue("2" in array)
    assertTrue("3" in array)
    assertEquals("1", array[0])
    assertEquals("2", array[1])
    assertEquals("3", array[2])
  }

  @Test
  fun shouldConvertNativeIntArraysToGdxIntArrays() {
    val intArray = intArrayOf(1, 2, 3).toGdxArray()
    assertEquals(3, intArray.size)
    assertTrue(1 in intArray)
    assertTrue(2 in intArray)
    assertTrue(3 in intArray)
    assertEquals(1, intArray[0])
    assertEquals(2, intArray[1])
    assertEquals(3, intArray[2])
  }

  @Test
  fun shouldConvertNativeFloatArraysToGdxFloatArrays() {
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
  fun shouldConvertNativeBooleanArraysToGdxBooleanArrays() {
    val booleanArray = booleanArrayOf(true, false, true).toGdxArray()
    assertEquals(3, booleanArray.size)
    assertEquals(true, booleanArray[0])
    assertEquals(false, booleanArray[1])
    assertEquals(true, booleanArray[2])
  }
}
