package ktx.tiled

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import org.junit.Assert.*
import org.junit.Test

class TiledMapTest {
  private val tiledMap = TiledMap().apply {
    properties.apply {
      put("width", 16)
      put("height", 8)
      put("tilewidth", 32)
      put("tileheight", 32)
      put("backgroundcolor", "#ffffff")
      put("orientation", "orthogonal")
      put("hexsidelength", 0)
      put("staggeraxis", "Y")
      put("staggerindex", "Odd")
    }
    layers.add(MapLayer().apply {
      name = "layer-1"
      objects.apply {
        add(MapObject())
        add(MapObject())
        add(MapObject())
      }
    })
    layers.add(MapLayer().apply {
      name = "layer-2"
    })
  }

  @Test
  fun `should retrieve properties from TiledMap`() {
    assertEquals(16, tiledMap.property<Int>("width"))
  }

  @Test
  fun `should retrieve properties from TiledMap with default value`() {
    assertEquals(16, tiledMap.property("width", 0))
    assertEquals(-1, tiledMap.property("x", -1))
  }

  @Test
  fun `should retrieve properties from TiledMap without default value`() {
    assertNull(tiledMap.propertyOrNull("x"))
    assertEquals(16, tiledMap.propertyOrNull<Int>("width"))
  }

  @Test
  fun `should check if property from TiledMap exists`() {
    assertTrue(tiledMap.containsProperty("width"))
    assertFalse(tiledMap.containsProperty("x"))
  }

  @Test
  fun `should retrieve standard properties of TiledMap`() {
    assertEquals(16, tiledMap.width)
    assertEquals(8, tiledMap.height)
    assertEquals(32, tiledMap.tileWidth)
    assertEquals(32, tiledMap.tileHeight)
    assertEquals(16 * 32, tiledMap.totalWidth())
    assertEquals(8 * 32, tiledMap.totalHeight())
    assertEquals("#ffffff", tiledMap.backgroundColor)
    assertEquals("orthogonal", tiledMap.orientation)
    assertEquals(0, tiledMap.hexSideLength)
    assertEquals("Y", tiledMap.staggerAxis)
    assertEquals("Odd", tiledMap.staggerIndex)
  }

  @Test(expected = MissingPropertyException::class)
  fun `should not retrieve non-existing property from TiledMap`() {
    tiledMap.property<String>("non-existing")
  }

  @Test
  fun `should retrieve existing layer from TiledMap`() {
    assertEquals("layer-1", tiledMap.layer("layer-1").name)
  }

  @Test(expected = MissingLayerException::class)
  fun `should not retrieve non-existing layer from TiledMap`() {
    tiledMap.layer("non-existing")
  }

  @Test
  fun `should check if layer exists in TiledMap`() {
    assertTrue(tiledMap.contains("layer-1"))
    assertFalse(tiledMap.contains("non-existing"))

    assertTrue("layer-1" in tiledMap)
    assertFalse("non-existing" in tiledMap)
  }

  @Test
  fun `should execute action per object of a layer`() {
    var counter = 0

    tiledMap.forEachMapObject("layer-1") {
      it.isVisible = false
      counter++
    }

    assertEquals(3, counter)
    assertTrue(tiledMap.layers["layer-1"].objects.all { !it.isVisible })
  }

  @Test
  fun `should not execute any action for empty layer`() {
    tiledMap.forEachMapObject("layer-2") {
      fail()
    }
  }

  @Test
  fun `should not execute any action for non-existing layer`() {
    tiledMap.forEachMapObject("non-existing") {
      fail()
    }
  }
}
