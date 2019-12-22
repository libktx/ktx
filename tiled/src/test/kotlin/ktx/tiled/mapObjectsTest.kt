package ktx.tiled

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.*
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Ellipse
import com.badlogic.gdx.math.Rectangle
import org.junit.Assert.*
import org.junit.Test

class MapObjectTest {
    private val mapObject = MapObject().apply {
        properties.also {
            it.put("id", 13)
            it.put("x", 1)
            it.put("width", 1f)
            it.put("name", "Property")
            it.put("active", true)
        }
    }

    private val polylineVertices = floatArrayOf(0f, 0f, 1f, 1f)
    private val polygonVertices = floatArrayOf(0f, 0f, 1f, 1f, 2f, 0f)

    private val circleObject = CircleMapObject()
    private val ellipseObject = EllipseMapObject()
    private val polylineObject = PolylineMapObject(polylineVertices)
    private val polygonObject = PolygonMapObject(polygonVertices)
    private val rectObject = RectangleMapObject()
    private val textureObject = TextureMapObject()

    @Test
    fun `retrieve properties from MapObject with default value`() {
        assertEquals(1, mapObject.property("x", 0))
        assertEquals(0, mapObject.property("y", 0))
        assertEquals(1f, mapObject.property("width", 0f))
        assertEquals("Property", mapObject.property("name", ""))
        assertEquals(true, mapObject.property("active", false))
    }

    @Test
    fun `retrieve properties from MapObject without default value`() {
        assertNull(mapObject.propertyOrNull("y"))
        val x: Int? = mapObject.propertyOrNull("x")
        assertNotNull(x)
        assertEquals(1, x)
    }

    @Test
    fun `check if property from MapObject exists`() {
        assertTrue(mapObject.containsProperty("x"))
        assertFalse(mapObject.containsProperty("y"))
    }

    @Test
    fun `retrieve standard properties of MapObject`() {
        assertEquals(1f, mapObject.x)
        assertEquals(0f, mapObject.y)
        assertEquals(13, mapObject.id)
    }

    @Test
    fun `retrieve shape from MapObject`() {
        assertEquals(Circle(0f, 0f, 1f), circleObject.shape)
        assertEquals(Ellipse(0f, 0f, 1f, 1f), ellipseObject.shape)
        assertEquals(polylineObject.polyline, polylineObject.shape)
        assertEquals(polygonObject.polygon, polygonObject.shape)
        assertEquals(Rectangle(0f, 0f, 1f, 1f), rectObject.shape)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `retrieve shape from unsupported MapObject`() {
        textureObject.shape
    }
}
