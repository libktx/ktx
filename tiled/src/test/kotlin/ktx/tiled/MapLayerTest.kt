package ktx.tiled

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapLayers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapLayerTest {
  private val mapLayer =
    MapLayer().apply {
      properties.apply {
        put("active", true)
        put("customProperty", 123)
      }
    }

  @Test
  fun `should retrieve properties from MapLayer`() {
    assertEquals(true, mapLayer.property<Boolean>("active"))
    assertEquals(123, mapLayer.property<Int>("customProperty"))
  }

  @Test
  fun `should retrieve properties from MapLayer with default value`() {
    assertEquals(true, mapLayer.property("active", false))
    assertEquals(123, mapLayer.property("customProperty", 0))
    assertEquals(-1f, mapLayer.property("x", -1f))
  }

  @Test
  fun `should retrieve properties from MapLayer without default value`() {
    assertNull(mapLayer.propertyOrNull("x"))
    assertEquals(123, mapLayer.propertyOrNull<Int>("customProperty"))
  }

  @Test
  fun `should check if property from MapLayer exists`() {
    assertTrue(mapLayer.containsProperty("active"))
    assertFalse(mapLayer.containsProperty("x"))
  }

  @Test(expected = MissingPropertyException::class)
  fun `should not retrieve non-existing property from MapLayer`() {
    mapLayer.property<String>("non-existing")
  }

  @Test
  fun `should return true when MapLayers is empty`() {
    val actual = MapLayers()

    assertTrue(actual.isEmpty())
  }

  @Test
  fun `should return false when MapLayers is not empty`() {
    val actual = MapLayers()
    actual.add(MapLayer())

    assertFalse(actual.isEmpty())
  }

  @Test
  fun `should return true when MapLayers becomes empty`() {
    val actual = MapLayers()
    actual.add(MapLayer())
    actual.remove(0)

    assertTrue(actual.isEmpty())
  }

  @Test
  fun `should return true when MapLayers is not empty`() {
    val actual = MapLayers()
    actual.add(MapLayer())

    assertTrue(actual.isNotEmpty())
  }

  @Test
  fun `should return false when MapLayers is empty`() {
    val actual = MapLayers()

    assertFalse(actual.isNotEmpty())
  }
}
