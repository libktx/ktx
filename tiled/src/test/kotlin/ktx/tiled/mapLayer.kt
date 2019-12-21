package ktx.tiled

import com.badlogic.gdx.maps.MapLayer
import org.junit.Assert
import org.junit.Test

class MapLayerTest {
    private val mapLayer = MapLayer().apply {
        properties.put("active", true)
        properties.put("customProperty", 123)
    }

    @Test
    fun `retrieve properties from MapLayer with default value`() {
        Assert.assertEquals(true, mapLayer.property("active", false))
        Assert.assertEquals(123, mapLayer.property("customProperty", 0))
        Assert.assertEquals(-1f, mapLayer.property("x", -1f))
    }

    @Test
    fun `retrieve properties from MapLayer without default value`() {
        Assert.assertNull(mapLayer.propertyOrNull("x"))
        val customProperty: Int? = mapLayer.propertyOrNull("customProperty")
        Assert.assertNotNull(customProperty)
        Assert.assertEquals(123, customProperty)
    }

    @Test
    fun `check if property from MapLayer exists`() {
        Assert.assertTrue(mapLayer.containsProperty("active"))
        Assert.assertFalse(mapLayer.containsProperty("x"))
    }
}