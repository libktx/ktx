package ktx.collections

import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import org.junit.Assert.*
import org.junit.Test
import java.util.LinkedList

/**
 * Tests utilities for LibGDX custom HashMap equivalent - [ObjectMap].
 */
class MapsTest {
  @Test
  fun `should create GdxMap`() {
    val map = gdxMapOf<Any, Any>()

    assertNotNull(map)
    assertEquals(0, map.size)
  }

  @Test
  fun `should create GdxMap with custom initial capacity`() {
    val map = gdxMapOf<Any, Any>(initialCapacity = 32)

    assertNotNull(map)
    assertEquals(0, map.size)
  }

  @Test
  fun `should create GdxMap with custom load factor`() {
    val map = gdxMapOf<Any, Any>(loadFactor = 0.4f)

    assertNotNull(map)
    assertEquals(0, map.size)
  }

  @Test
  fun `should create GdxMap from key to value pairs`() {
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
  fun `should report size`() {
    val map = gdxMapOf(1 to "1", 2 to "2", 3 to "3")

    assertEquals(3, map.size())
    assertEquals(map.size, map.size())
  }

  @Test
  fun `should report size on null Map`() {
    val nullMap: ObjectMap<Any, Any>? = null

    assertEquals(0, nullMap.size())
  }

  @Test
  fun `should report empty status`() {
    assertFalse(gdxMapOf(1 to "1", 2 to "2", 3 to "3").isEmpty())
    assertTrue(ObjectMap<Any, Any>().isEmpty())
    assertTrue((null as ObjectMap<Any, Any>?).isEmpty())
  }

  @Test
  fun `should report non empty status`() {
    assertTrue(gdxMapOf(1 to "1", 2 to "2", 3 to "3").isNotEmpty())
    assertFalse(ObjectMap<Any, Any>().isNotEmpty())
    assertFalse((null as ObjectMap<Any, Any>?).isNotEmpty())
  }

  @Test
  fun `should put values in Map with square brackets`() {
    val map = ObjectMap<String, String>()

    map["key"] = "value"

    assertTrue("key" in map)
    assertTrue(map.containsValue("value", false))
  }

  @Test
  fun `should allow to iterate with iterator reference`() {
    val map = gdxMapOf(1 to "1", 2 to "2", 3 to "3")

    map.iterate { _, value, iterator -> if (value == "1") iterator.remove() }

    assertEquals(2, map.size)
    assertFalse(1 in map)
    assertFalse(map.containsValue("1", false))
  }

  @Test
  fun `should check if Map contains key with in operator`() {
    val map = ObjectMap<String, String>()

    map.put("key", "value")

    assertTrue("key" in map)
    assertTrue("absent" !in map)
  }

  @Test
  fun `should convert GdxMap keys to Set`() {
    val set = gdxMapOf(1 to "1", 2 to "2", 3 to "3").toGdxSet() // .keys().toGdxSet() and .values().toGdxSet() also work.

    assertNotNull(set)
    assertEquals(3, set.size)
    assertTrue(1 in set)
    assertTrue(2 in set)
    assertTrue(3 in set)
  }

  @Test
  fun `should convert Set to GdxMap`() {
    val setAsMap = ObjectSet.with("1", "2", "3").toGdxMap { it.toInt() }

    assertEquals(gdxMapOf(1 to "1", 2 to "2", 3 to "3"), setAsMap)
  }

  @Test
  fun `should convert GdxArray to GdxMap`() {
    val arrayAsMap = Array.with("1", "2", "3").toGdxMap { it.toInt() }

    assertEquals(gdxMapOf(1 to "1", 2 to "2", 3 to "3"), arrayAsMap)
  }

  @Test
  fun `should convert Array to GdxMap`() {
    val arrayAsMap = arrayOf("1", "2", "3").toGdxMap { it.toInt() }

    assertEquals(gdxMapOf(1 to "1", 2 to "2", 3 to "3"), arrayAsMap)
  }

  @Test
  fun `should convert Iterables to GdxMap`() {
    val map = listOf("1", "2", "3").toGdxMap(
        keyProvider = { it.toInt() },
        valueProvider = { it + it }
    )

    assertEquals(gdxMapOf(1 to "11", 2 to "22", 3 to "33"), map)
  }

  @Test
  fun `should convert customized Arrays to GdxMap`() {
    val map = arrayOf("1", "2", "3").toGdxMap(
        keyProvider = { it.toInt() },
        valueProvider = { it + it }
    )

    assertEquals(gdxMapOf(1 to "11", 2 to "22", 3 to "33"), map)
  }

  @Test
  fun `should create IdentityMaps`() {
    val map = gdxIdentityMapOf<Any, Any>()

    assertNotNull(map)
    assertEquals(0, map.size)
  }

  @Test
  fun `should create IdentityMaps from key to value pairs`() {
    val key = "key"
    val otherKey = "other"
    val map = gdxIdentityMapOf(key to "value", otherKey to "another")

    assertEquals(2, map.size)
    assertTrue(key in map)
    assertTrue(otherKey in map)
    assertTrue(map.containsValue("value", false))
    assertTrue(map.containsValue("another", false))
    assertEquals("value", map[key])
    assertEquals("another", map[otherKey])
  }

  @Test
  fun `should check if IdentityMap contains key with in operator`() {
    val map = IdentityMap<String, String>()
    val key = "key"

    map.put(key, "value")

    assertTrue(key in map)
    assertTrue("absent" !in map)
  }

  @Test
  fun `should put values in IdentityMap with square brackets`() {
    val map = IdentityMap<String, String>()
    val key = "key"

    map[key] = "value"

    assertTrue(key in map)
    assertTrue(map.containsValue("value", false))
  }

  @Test
  fun `should allow to iterate IdentityMap with iterator reference`() {
    val keyToRemove = 1
    val map = gdxIdentityMapOf(keyToRemove to "1", 2 to "2", 3 to "3")

    map.iterate { _, value, iterator -> if (value == "1") iterator.remove() }

    assertEquals(2, map.size)
    assertFalse(keyToRemove in map)
    assertFalse(map.containsValue("1", false))
  }

  @Test
  fun `should support optimized primitive IntIntMap`() {
    val map = gdxIntIntMap()

    map[1] = 2 // Tests array bracket operator.

    assertEquals(2, map[1])
    assertEquals(2, map[1, -1]) // Second parameter is the default value returned if key is missing: -1.
    assertEquals(1, map.size)
    assertTrue(1 in map) // Tests contains extension (in operator).
    assertTrue(2 !in map) // This operator (in) tests if keys are in the map and should return false even if the parameter
    // matches a valid value stored in the map.
  }

  @Test
  fun `should support optimized primitive IntFloatMap`() {
    val map = gdxIntFloatMap()

    map[1] = 2f // Tests array bracket operator.

    assertEquals(2f, map[1], 0.00001f)
    assertEquals(2f, map[1, -1f], 0.00001f) // Second parameter is the default value returned if key is missing: -1f.
    assertEquals(1, map.size)
    assertTrue(1 in map) // Tests contains extension (in operator).
    assertTrue(2 !in map)
  }

  @Test
  fun `should support optimized IntMap with primitive int keys`() {
    val map = gdxIntMap<String>()

    map[1] = "2" // Tests array bracket operator.

    assertEquals("2", map[1])
    assertEquals(1, map.size)
    assertTrue(1 in map) // Tests contains extension (in operator).
    assertTrue(2 !in map)
  }

  @Test
  fun `should provide alias for compatibility with other LibGDX collections`() {
    @Suppress("USELESS_IS_CHECK")
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

    assertEquals(gdxMapOf("One" to 2, "Two" to 4, "Three" to 6), result)
  }

  @Test
  fun `should filter elements into a new GdxMap`() {
    val map = gdxMapOf("One" to 1, "Two" to 2, "Three" to 3, "Four" to 4, "Five" to 5)

    val result = map.filter { it.value % 2 == 1 }

    assertEquals(gdxMapOf("One" to 1, "Three" to 3, "Five" to 5), result)
  }

  @Test
  fun `should flatten elements into a new GdxArray`() {
    val map = gdxMapOf(1 to GdxArray.with(1), 2 to listOf<Int>(), 3 to LinkedList(arrayListOf(2, 3)))

    val result = map.flatten()

    assertEquals(3, result.size)
    assertEquals(GdxArray.with(1, 2, 3), result)
  }

  @Test
  fun `should map elements to lists and flatten them into a new GdxArray`() {
    val map = gdxMapOf("One" to 1, "Two" to 2, "Three" to 3)

    val result = map.flatMap { (_, value) -> List(value!!) { value } }
    result.sort()

    assertEquals(GdxArray.with(1, 2, 2, 3, 3, 3), result)
  }

}
