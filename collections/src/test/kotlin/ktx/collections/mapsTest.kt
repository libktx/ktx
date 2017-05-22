package ktx.collections

import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * Tests utilities for LibGDX custom HashMap equivalent - [ObjectMap].
 * @author MJ
 */
class MapsTest {
  @Test
  fun shouldCreateMaps() {
    val map = gdxMapOf<Any, Any>()
    assertNotNull(map)
    assertTrue(map is ObjectMap)
    assertEquals(0, map.size)
  }

  @Test
  fun shouldCreateMapsWithCustomInitialCapacity() {
    val map = gdxMapOf<Any, Any>(initialCapacity = 32)
    assertNotNull(map)
    assertTrue(map is ObjectMap)
    assertEquals(0, map.size)
  }

  @Test
  fun shouldCreateMapsWithCustomLoadFactor() {
    val map = gdxMapOf<Any, Any>(loadFactor = 0.4f)
    assertNotNull(map)
    assertTrue(map is ObjectMap)
    assertEquals(0, map.size)
  }

  @Test
  fun shouldCreateMapsFromKeyToValuePairs() {
    val map = gdxMapOf("key" to "value", "other" to "another")
    assertEquals(2, map.size)
    assertTrue("key" in map)
    assertTrue("other" in map)
    assertTrue(map.containsValue("value", false))
    assertTrue(map.containsValue("another", false))
    assertEquals("value", map["key"])
    assertEquals("another", map["other"])
  }

  @Test
  fun shouldReportSize() {
    val map = gdxMapOf(1 to "1", 2 to "2", 3 to "3")
    assertEquals(3, map.size())
    assertEquals(map.size, map.size())
    val nullMap: ObjectMap<Any, Any>? = null
    assertEquals(0, nullMap.size())
  }

  @Test
  fun shouldReportEmptyStatus() {
    val map = gdxMapOf(1 to "1", 2 to "2", 3 to "3")
    assertFalse(map.isEmpty())
    val emptyMap = ObjectMap<Any, Any>()
    assertTrue(emptyMap.isEmpty())
    val nullMap: ObjectMap<Any, Any>? = null
    assertTrue(nullMap.isEmpty())
  }

  @Test
  fun shouldReportNonEmptyStatus() {
    val map = gdxMapOf(1 to "1", 2 to "2", 3 to "3")
    assertTrue(map.isNotEmpty())
    val emptyMap = ObjectMap<Any, Any>()
    assertFalse(emptyMap.isNotEmpty())
    val nullMap: ObjectMap<Any, Any>? = null
    assertFalse(nullMap.isNotEmpty())
  }

  @Test
  fun shouldPutValuesInMapWithSquareBrackets() {
    val map = ObjectMap<String, String>()
    assertFalse("key" in map)
    assertFalse(map.containsValue("value", false))
    map["key"] = "value"
    assertTrue("key" in map)
    assertTrue(map.containsValue("value", false))
  }

  @Test
  fun shouldAllowToIterateWithIteratorReference() {
    val map = gdxMapOf(1 to "1", 2 to "2", 3 to "3")
    assertEquals(3, map.size)
    map.iterate { _, value, iterator -> if (value == "1") iterator.remove() }
    assertEquals(2, map.size)
    assertFalse(1 in map)
    assertFalse(map.containsValue("1", false))
  }

  @Test
  fun shouldCheckIfMapContainsKeyWithInOperator() {
    val map = ObjectMap<String, String>()
    map.put("key", "value")
    assertTrue("key" in map)
    assertTrue("absent" !in map)
  }

  @Test
  fun shouldConvertMapKeysToSet() {
    val set = gdxMapOf(1 to "1", 2 to "2", 3 to "3").toGdxSet() // .keys().toGdxSet() and .values().toGdxSet() also work.
    assertNotNull(set)
    assertEquals(3, set.size)
    assertTrue(1 in set)
    assertTrue(2 in set)
    assertTrue(3 in set)
  }

  @Test
  fun shouldConvertSetToMap() {
    val setAsMap = ObjectSet.with("1", "2", "3").toGdxMap { it.toInt() }
    assertEquals(3, setAsMap.size)
    assertTrue(setAsMap.containsValue("1", false))
    assertTrue(setAsMap.containsValue("2", false))
    assertTrue(setAsMap.containsValue("3", false))
    assertTrue(setAsMap.containsKey(1))
    assertTrue(setAsMap.containsKey(2))
    assertTrue(setAsMap.containsKey(3))
    assertEquals("1", setAsMap[1])
    assertEquals("2", setAsMap[2])
    assertEquals("3", setAsMap[3])
  }

  @Test
  fun shouldConvertGdxArrayToMap() {
    val arrayAsMap = Array.with("1", "2", "3").toGdxMap { it.toInt() }
    assertEquals(3, arrayAsMap.size)
    assertTrue(arrayAsMap.containsValue("1", false))
    assertTrue(arrayAsMap.containsValue("2", false))
    assertTrue(arrayAsMap.containsValue("3", false))
    assertTrue(arrayAsMap.containsKey(1))
    assertTrue(arrayAsMap.containsKey(2))
    assertTrue(arrayAsMap.containsKey(3))
    assertEquals("1", arrayAsMap[1])
    assertEquals("2", arrayAsMap[2])
    assertEquals("3", arrayAsMap[3])
  }

  @Test
  fun shouldConvertArrayToMap() {
    val arrayAsMap = arrayOf("1", "2", "3").toGdxMap { it.toInt() }
    assertEquals(3, arrayAsMap.size)
    assertTrue(arrayAsMap.containsValue("1", false))
    assertTrue(arrayAsMap.containsValue("2", false))
    assertTrue(arrayAsMap.containsValue("3", false))
    assertTrue(arrayAsMap.containsKey(1))
    assertTrue(arrayAsMap.containsKey(2))
    assertTrue(arrayAsMap.containsKey(3))
    assertEquals("1", arrayAsMap[1])
    assertEquals("2", arrayAsMap[2])
    assertEquals("3", arrayAsMap[3])
  }

  @Test
  fun shouldConvertIterablesToMap() {
    val map = listOf("1", "2", "3").toGdxMap(
        keyProvider = { it.toInt() },
        valueProvider = { it + it }
    )
    assertEquals(3, map.size)
    assertTrue(map.containsValue("11", false))
    assertTrue(map.containsValue("22", false))
    assertTrue(map.containsValue("33", false))
    assertTrue(map.containsKey(1))
    assertTrue(map.containsKey(2))
    assertTrue(map.containsKey(3))
    assertEquals("11", map[1])
    assertEquals("22", map[2])
    assertEquals("33", map[3])
  }

  @Test
  fun shouldConvertCustomizedArraysToMap() {
    val map = arrayOf("1", "2", "3").toGdxMap(
        keyProvider = { it.toInt() },
        valueProvider = { it + it }
    )
    assertEquals(3, map.size)
    assertTrue(map.containsValue("11", false))
    assertTrue(map.containsValue("22", false))
    assertTrue(map.containsValue("33", false))
    assertTrue(map.containsKey(1))
    assertTrue(map.containsKey(2))
    assertTrue(map.containsKey(3))
    assertEquals("11", map[1])
    assertEquals("22", map[2])
    assertEquals("33", map[3])
  }

  @Test
  fun shouldCreateIdentityMaps() {
    val map = gdxIdentityMapOf<Any, Any>()
    assertNotNull(map)
    assertTrue(map is IdentityMap)
    assertEquals(0, map.size)
  }

  @Test
  fun shouldCreateIdentityMapsFromKeyToValuePairs() {
    val key = "key"
    val otherKey = "other"
    val map = gdxMapOf(key to "value", otherKey to "another")
    assertEquals(2, map.size)
    assertTrue(key in map)
    assertTrue(otherKey in map)
    assertTrue(map.containsValue("value", false))
    assertTrue(map.containsValue("another", false))
    assertEquals("value", map[key])
    assertEquals("another", map[otherKey])
  }

  @Test
  fun shouldCheckIfIdentityMapContainsKeyWithInOperator() {
    val map = IdentityMap<String, String>()
    val key = "key"
    assertFalse(key in map)
    map.put(key, "value")
    assertTrue(key in map)
    assertTrue("absent" !in map)
  }

  @Test
  fun shouldPutValuesInIdentityMapWithSquareBrackets() {
    val map = IdentityMap<String, String>()
    val key = "key"
    assertFalse(key in map)
    assertFalse(map.containsValue("value", false))
    map[key] = "value"
    assertTrue(key in map)
    assertTrue(map.containsValue("value", false))
  }

  @Test
  fun shouldAllowToIterateIdentityMapWithIteratorReference() {
    val keyToRemove = 1
    val map = gdxIdentityMapOf(keyToRemove to "1", 2 to "2", 3 to "3")
    assertEquals(3, map.size)
    map.iterate { _, value, iterator -> if (value == "1") iterator.remove() }
    assertEquals(2, map.size)
    assertFalse(keyToRemove in map)
    assertFalse(map.containsValue("1", false))
  }

  @Test
  fun shouldSupportOptimizedPrimitiveIntIntMap() {
    val map = gdxIntIntMap()
    assertNotNull(map)
    assertTrue(map is IntIntMap)
    assertEquals(0, map.size)
    map[1] = 2 // Tests array bracket operator.
    assertEquals(2, map[1])
    assertEquals(2, map[1, -1]) // Second parameter is the default value returned if key is missing: -1.
    assertEquals(1, map.size)
    assertTrue(1 in map) // Tests contains extension (in operator).
    assertTrue(2 !in map) // This operator (in) tests if keys are in the map and should return false even if the parameter
    // matches a valid value stored in the map.
  }

  @Test
  fun shouldSupportOptimizedPrimitiveIntFloatMap() {
    val map = gdxIntFloatMap()
    assertNotNull(map)
    assertTrue(map is IntFloatMap)
    assertEquals(0, map.size)
    map[1] = 2f // Tests array bracket operator.
    assertEquals(2f, map[1], 0.00001f)
    assertEquals(2f, map[1, -1f], 0.00001f) // Second parameter is the default value returned if key is missing: -1f.
    assertEquals(1, map.size)
    assertTrue(1 in map) // Tests contains extension (in operator).
    assertTrue(2 !in map)
  }

  @Test
  fun shouldSupportOptimizedIntMapWithPrimitiveIntKeys() {
    val map = gdxIntMap<String>()
    assertNotNull(map)
    assertTrue(map is IntMap)
    assertEquals(0, map.size)
    map[1] = "2" // Tests array bracket operator.
    assertEquals("2", map[1])
    assertEquals(1, map.size)
    assertTrue(1 in map) // Tests contains extension (in operator).
    assertTrue(2 !in map)
  }

  @Test
  fun `should provide alias for compatibility with other LibGDX collections`() {
    assertTrue(GdxMap<Any, Any>() is ObjectMap<Any, Any>)
  }

  @Test
  fun `should destruct ObjectMap#Entry into key and value`() {
    val entry = ObjectMap.Entry<String, String>()
    entry.key = "Key"
    entry.value = "Value"

    val (key, value) = entry

    assertEquals("Key", key)
    assertEquals("Value", value)
  }

  @Test
  fun `should destruct IdentityMap#Entry into key and value`() {
    val entry = IdentityMap.Entry<String, String>()
    entry.key = "Key"
    entry.value = "Value"

    val (key, value) = entry

    assertEquals("Key", key)
    assertEquals("Value", value)
  }

  @Test
  fun `should destruct IntMap#Entry into key and value`() {
    val entry = IntMap.Entry<String>()
    entry.key = 10
    entry.value = "Value"

    val (key, value) = entry

    assertEquals(10, key)
    assertEquals("Value", value)
  }

  @Test
  fun `should destruct LongMap#Entry into key and value`() {
    val entry = LongMap.Entry<String>()
    entry.key = 10L
    entry.value = "Value"

    val (key, value) = entry

    assertEquals(10L, key)
    assertEquals("Value", value)
  }

  @Test
  fun `should destruct IntIntMap#Entry into key and value`() {
    val entry = IntIntMap.Entry()
    entry.key = 10
    entry.value = 20

    val (key, value) = entry

    assertEquals(10, key)
    assertEquals(20, value)
  }

  @Test
  fun `should destruct IntFloatMap#Entry into key and value`() {
    val entry = IntFloatMap.Entry()
    entry.key = 10
    entry.value = 20f

    val (key, value) = entry

    assertEquals(10, key)
    assertEquals(20f, value)
  }

  @Test
  fun `should destruct ObjectIntMap#Entry into key and value`() {
    val entry = ObjectIntMap.Entry<String>()
    entry.key = "Key"
    entry.value = 10

    val (key, value) = entry

    assertEquals("Key", key)
    assertEquals(10, value)
  }

  @Test
  fun `should map elements into a new GdxMap`() {
    val map = gdxMapOf("One" to 1, "Two" to 2, "Three" to 3)
    val result = map.map { it.value * 2 }

    assertTrue(result is GdxMap)
    assertEquals(gdxMapOf("One" to 2, "Two" to 4, "Three" to 6), result)
  }

  @Test
  fun `should filter elements into a new GdxMap`() {
    val map = gdxMapOf("One" to 1, "Two" to 2, "Three" to 3, "Four" to 4, "Five" to 5)
    val result = map.filter { it.value % 2 == 1 }

    assertTrue(result is GdxMap)
    assertEquals(gdxMapOf("One" to 1, "Three" to 3, "Five" to 5), result)
  }

  @Test
  fun `should flatten elements into a new GdxArray`() {
    val map = gdxMapOf(1 to GdxArray.with(1), 2 to listOf<Int>(), 3 to LinkedList(arrayListOf(2, 3)))
    val result = map.flatten()

    assertTrue(result is GdxArray)
    assertEquals(3, result.size)
    assertEquals(GdxArray.with(1, 2, 3), result)
  }

  @Test
  fun `should map elements to lists and flatten them into a new GdxArray`() {
    val map = gdxMapOf("One" to 1, "Two" to 2, "Three" to 3)
    val result = map.flatMap { e -> List(e.value) { e.value }  }
    result.sort()

    assertTrue(result is GdxArray)
    assertEquals(GdxArray.with(1, 2, 2, 3, 3, 3), result)
  }

}
