package ktx.tiled

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.objects.CircleMapObject
import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Ellipse
import com.badlogic.gdx.math.Rectangle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MapObjectTest {
  private val mapObject =
    MapObject().apply {
      properties.apply {
        put("id", 13)
        put("x", 1)
        put("y", 0f)
        put("rotation", -2.33f)
        put("type", "SomeType")
        put("width", 20f)
        put("height", 30f)
        put("name", "Property")
        put("active", true)
      }
    }

  @Test
  fun `should retrieve properties from MapObject`() {
    assertEquals(1, mapObject.property<Int>("x"))
    assertEquals(20f, mapObject.property<Float>("width"))
    assertEquals("Property", mapObject.property<String>("name"))
    assertEquals(true, mapObject.property<Boolean>("active"))
  }

  @Test
  fun `should retrieve properties from MapObject with default value`() {
    assertEquals(1, mapObject.property("x", 0))
    assertEquals(0, mapObject.property("non-existing", 0))
    assertEquals(20f, mapObject.property("width", 0f))
    assertEquals("Property", mapObject.property("name", ""))
    assertEquals(true, mapObject.property("active", false))
  }

  @Test
  fun `should retrieve properties from MapObject without default value`() {
    assertNull(mapObject.propertyOrNull("non-existing"))
    assertEquals(1, mapObject.propertyOrNull<Int>("x"))
  }

  @Test
  fun `should check if property from MapObject exists`() {
    assertTrue(mapObject.containsProperty("x"))
    assertFalse(mapObject.containsProperty("non-existing"))
  }

  @Test
  fun `should retrieve standard properties of MapObject`() {
    assertEquals(13, mapObject.id)
    assertEquals(1f, mapObject.x)
    assertEquals(0f, mapObject.y)
    assertEquals(20f, mapObject.width)
    assertEquals(30f, mapObject.height)
    assertEquals(-2.33f, mapObject.rotation)
    assertEquals("SomeType", mapObject.type)
  }

  @Test(expected = MissingPropertyException::class)
  fun `should not retrieve non-existing property from MapObject`() {
    mapObject.property<String>("non-existing")
  }

  @Test
  fun `should retrieve shape from MapObject with Circle type`() {
    val circleObject = CircleMapObject()

    assertEquals(Circle(0f, 0f, 1f), circleObject.shape)
  }

  @Test
  fun `should retrieve shape from MapObject with Ellipse type`() {
    val ellipseObject = EllipseMapObject()

    assertEquals(Ellipse(0f, 0f, 1f, 1f), ellipseObject.shape)
  }

  @Test
  fun `should retrieve shape from MapObject with Polyline type`() {
    val polylineVertices = floatArrayOf(0f, 0f, 1f, 1f)
    val polylineObject = PolylineMapObject(polylineVertices)

    assertEquals(polylineObject.polyline, polylineObject.shape)
  }

  @Test
  fun `should retrieve shape from MapObject with Polygon type`() {
    val polygonVertices = floatArrayOf(0f, 0f, 1f, 1f, 2f, 0f)
    val polygonObject = PolygonMapObject(polygonVertices)

    assertEquals(polygonObject.polygon, polygonObject.shape)
  }

  @Test
  fun `should retrieve shape from MapObject with Rectangle type`() {
    val rectObject = RectangleMapObject()

    assertEquals(Rectangle(0f, 0f, 1f, 1f), rectObject.shape)
  }

  @Test(expected = MissingShapeException::class)
  fun `retrieve shape from unsupported MapObject`() {
    val textureObject = TextureMapObject()

    textureObject.shape
  }

  @Test
  fun `should return true when MapObjects is empty`() {
    val actual = MapObjects()

    assertTrue(actual.isEmpty())
  }

  @Test
  fun `should return false when MapObjects is not empty`() {
    val actual = MapObjects()
    actual.add(MapObject())

    assertFalse(actual.isEmpty())
  }

  @Test
  fun `should return true when MapObjects is not empty`() {
    val actual = MapObjects()
    actual.add(MapObject())

    assertTrue(actual.isNotEmpty())
  }

  @Test
  fun `should return false when MapObjects is empty`() {
    val actual = MapObjects()

    assertFalse(actual.isNotEmpty())
  }
}
