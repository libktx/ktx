package ktx.tiled

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.utils.Array
import org.junit.Assert
import org.junit.Test

class TiledMapTileTest {
    private val staticTile = StaticTiledMapTile(TextureRegion()).apply {
        properties.put("staticProp1", true)
        properties.put("staticProp2", 123)
    }
    private val animatedTile = AnimatedTiledMapTile(0f, Array<StaticTiledMapTile>()).apply {
        properties.put("aniProp1", 1f)
        properties.put("aniProp2", "SomeText")
    }

    @Test
    fun `retrieve properties from Tile with default value`() {
        Assert.assertEquals(true, staticTile.property("staticProp1", false))
        Assert.assertEquals(123, staticTile.property("staticProp2", 0))
        Assert.assertEquals(-1f, animatedTile.property("non-existing", -1f))
    }

    @Test
    fun `retrieve properties from Tile without default value`() {
        Assert.assertNull(animatedTile.propertyOrNull("non-existing"))
        val customProperty: Int? = staticTile.propertyOrNull("staticProp2")
        Assert.assertNotNull(customProperty)
        Assert.assertEquals(123, customProperty)
    }

    @Test
    fun `check if property from Tile exists`() {
        Assert.assertTrue(staticTile.containsProperty("staticProp1"))
        Assert.assertFalse(staticTile.containsProperty("non-existing"))
        Assert.assertTrue(animatedTile.containsProperty("aniProp2"))
        Assert.assertFalse(animatedTile.containsProperty("non-existing"))
    }

    @Test(expected = MissingPropertyException::class)
    fun `retrieve non-existing property from Tile using exception`() {
        animatedTile.property<String>("non-existing")
    }
}
