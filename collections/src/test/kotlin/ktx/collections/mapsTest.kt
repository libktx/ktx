package ktx.collections

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectSet
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests utilities for LibGDX custom HashMap equivalent - ObjectMap.
 * @author MJ
 */
class MapsTest {
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
}