package ktx.tiled

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import org.junit.Assert.*
import org.junit.Test

class TiledMapTest {
  private val tiledMap = TiledMap().apply {
    properties.put("width", 16)
    properties.put("height", 8)
    properties.put("tilewidth", 32)
    properties.put("tileheight", 32)

    properties.put("backgroundcolor", "#ffffff")
    properties.put("orientation", "orthogonal")
    properties.put("hexsidelength", 0)
    properties.put("staggeraxis", "Y")
    properties.put("staggerindex", "Odd")

    layers.add(MapLayer().apply {
      name = "layer-1"
      objects.add(MapObject())
      objects.add(MapObject())
      objects.add(MapObject())
    })
    layers.add(MapLayer().apply { name = "layer-2" })
  }

  @Test
  fun `retrieve properties from TiledMap with default value`() {
    assertEquals(16, tiledMap.property("width", 0))
    assertEquals(-1, tiledMap.property("x", -1))
  }

  @Test
  fun `retrieve properties from TiledMap without default value`() {
    assertNull(tiledMap.propertyOrNull("x"))
    val width: Int? = tiledMap.propertyOrNull("width")
    assertNotNull(width)
    assertEquals(16, width)
  }

  @Test
  fun `check if property from TiledMap exists`() {
    assertTrue(tiledMap.containsProperty("width"))
    assertFalse(tiledMap.containsProperty("x"))
  }

  @Test
  fun `retrieve standard properties of TiledMap`() {
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
  fun `retrieve non-existing property from TiledMap using exception`() {
    tiledMap.property<String>("non-existing")
  }

  @Test
  fun `retrieve existing layer from TiledMap`() {
    assertEquals("layer-1", tiledMap.layer("layer-1").name)
  }

  @Test(expected = MissingLayerException::class)
  fun `retrieve non-existing layer from TiledMap using exception`() {
    tiledMap.layer("non-existing")
  }

  @Test
  fun `check if layer exists in TiledMap`() {
    assertTrue(tiledMap.contains("layer-1"))
    assertFalse("non-existing" in tiledMap)
  }

  @Test
  fun `execute action per object of a layer`() {
    // check that there are objects in layer -1
    assertEquals(3, tiledMap.layers["layer-1"].objects.count)
    // verify that they are all visible and set them to not visible
    var counter = 0
    tiledMap.forEachMapObject("layer-1") {
      assertTrue(it.isVisible)
      it.isVisible = false
      counter++
    }
    // verify again that they are now all invisible and revert them back to being visible
    assertEquals(3, counter)
    tiledMap.forEachMapObject("layer-1") {
      assertFalse(it.isVisible)
      it.isVisible = true
    }

    // also, test empty default layer which should do nothing
    counter = 0
    tiledMap.forEachMapObject("non-existing") { ++counter }
    assertEquals(0, counter)
  }
}
