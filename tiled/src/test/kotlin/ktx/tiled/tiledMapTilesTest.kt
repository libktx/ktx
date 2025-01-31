package ktx.tiled

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.utils.Array
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

abstract class TiledMapTileTest<T : TiledMapTile> {
  abstract val tile: T

  @Test
  fun `should retrieve properties from tile`() {
    assertEquals(true, tile.property<Boolean>("prop1"))
    assertEquals("text", tile.property<String>("prop2"))
  }

  @Test
  fun `should retrieve properties from tile with default value`() {
    assertEquals(true, tile.property("prop1", false))
    assertEquals("text", tile.property("prop2", ""))
    assertEquals(-1f, tile.property("non-existing", -1f))
  }

  @Test
  fun `should retrieve properties from tile without default value`() {
    assertNull(tile.propertyOrNull("non-existing"))
    assertEquals("text", tile.propertyOrNull("prop2"))
  }

  @Test
  fun `should check if property from tile exists`() {
    assertTrue(tile.containsProperty("prop1"))
    assertTrue(tile.containsProperty("prop2"))
    assertFalse(tile.containsProperty("non-existing"))
  }

  @Test(expected = MissingPropertyException::class)
  fun `retrieve non-existing property from Tile using exception`() {
    tile.property<String>("non-existing")
  }
}

class StaticTiledMapTileTest : TiledMapTileTest<StaticTiledMapTile>() {
  override val tile =
    StaticTiledMapTile(TextureRegion()).apply {
      properties.apply {
        put("prop1", true)
        put("prop2", "text")
      }
    }
}

class AnimatedTiledMapTileTest : TiledMapTileTest<AnimatedTiledMapTile>() {
  override val tile =
    AnimatedTiledMapTile(1f, Array<StaticTiledMapTile>()).apply {
      properties.apply {
        put("prop1", true)
        put("prop2", "text")
      }
    }
}
