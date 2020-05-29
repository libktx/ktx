package ktx.tiled

import com.badlogic.gdx.maps.MapProperties
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MapPropertiesTest {
  @Test
  fun `should check if a property exists`() {
    val mapProperties = MapProperties()
    mapProperties.put("key", "value")

    val result = "key" in mapProperties

    assertTrue(result)
  }

  @Test
  fun `should check if a property exists given missing key`() {
    val mapProperties = MapProperties()

    val result = "key" in mapProperties

    assertFalse(result)
  }

  @Test
  fun `should add property`() {
    val mapProperties = MapProperties()

    mapProperties["key"] = "value"

    assertEquals("value", mapProperties["key"])
  }

  @Test
  fun `should override property`() {
    val mapProperties = MapProperties()
    mapProperties.put("key", "old")

    mapProperties["key"] = "new"

    assertEquals("new", mapProperties["key"])
  }
}
