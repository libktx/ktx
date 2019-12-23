package ktx.tiled

import com.badlogic.gdx.maps.tiled.TiledMapTileSet
import org.junit.Assert.*
import org.junit.Test

class TiledMapTileSetTest {
  private val tileset = TiledMapTileSet().apply {
    properties.apply {
      put("tilesetProp1", true)
      put("tilesetProp2", 123)
    }
  }

  @Test
  fun `should retrieve properties from TileSet`() {
    assertEquals(true, tileset.property<Boolean>("tilesetProp1"))
    assertEquals(123, tileset.property<Int>("tilesetProp2"))
  }

  @Test
  fun `should retrieve properties from TileSet with default value`() {
    assertEquals(true, tileset.property("tilesetProp1", false))
    assertEquals(123, tileset.property("tilesetProp2", 0))
    assertEquals(-1f, tileset.property("non-existing", -1f))
  }

  @Test
  fun `should retrieve properties from TileSet without default value`() {
    assertNull(tileset.propertyOrNull("non-existing"))
    assertEquals(123, tileset.propertyOrNull<Int>("tilesetProp2"))
  }

  @Test
  fun `should check if property from TileSet exists`() {
    assertTrue(tileset.containsProperty("tilesetProp1"))
    assertFalse(tileset.containsProperty("non-existing"))
  }

  @Test(expected = MissingPropertyException::class)
  fun `should not retrieve non-existing property from TileSet`() {
    tileset.property<String>("non-existing")
  }
}
