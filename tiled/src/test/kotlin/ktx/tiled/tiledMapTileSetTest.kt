package ktx.tiled

import com.badlogic.gdx.maps.tiled.TiledMapTileSet
import org.junit.Assert
import org.junit.Test

class TiledMapTileSetTest {
    private val tileset = TiledMapTileSet().apply {
        properties.put("tilesetProp1", true)
        properties.put("tilesetProp2", 123)
    }

    @Test
    fun `retrieve properties from TileSet with default value`() {
        Assert.assertEquals(true, tileset.property("tilesetProp1", false))
        Assert.assertEquals(123, tileset.property("tilesetProp2", 0))
        Assert.assertEquals(-1f, tileset.property("non-existing", -1f))
    }

    @Test
    fun `retrieve properties from TileSet without default value`() {
        Assert.assertNull(tileset.propertyOrNull("non-existing"))
        val customProperty: Int? = tileset.propertyOrNull("tilesetProp2")
        Assert.assertNotNull(customProperty)
        Assert.assertEquals(123, customProperty)
    }

    @Test
    fun `check if property from TileSet exists`() {
        Assert.assertTrue(tileset.containsProperty("tilesetProp1"))
        Assert.assertFalse(tileset.containsProperty("non-existing"))
    }

    @Test(expected = MissingPropertyException::class)
    fun `retrieve non-existing property from TileSet using exception`() {
        tileset.property<String>("non-existing")
    }
}
