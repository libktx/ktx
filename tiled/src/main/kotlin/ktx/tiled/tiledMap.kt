package ktx.tiled

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.tiled.TiledMap

private val DEFAULT_MAP_LAYER = MapLayer().apply { name = "ktx-default-map-layer" }

/**
 * Extension method to directly access the [MapProperties] of a [TiledMap]. The type is automatically
 * derived from the type of the given default value. If the property is not defined the defaultValue will be returned.
 * @param key property name
 * @param defaultValue default value in case the property is missing
 * @return value of the property or defaultValue if property is missing
 */
inline fun <reified T> TiledMap.property(key: String, defaultValue: T): T = this.properties[key, defaultValue, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [TiledMap]. If the property
 * is not defined then this method returns null.
 * @param key property name
 * @return value of the property or null if the property is missing
 */
inline fun <reified T> TiledMap.propertyOrNull(key: String): T? = this.properties[key, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [TiledMap] and its [containsKey][MapProperties.containsKey] method
 * @param key property name
 * @return true if the property exists. Otherwise false
 */
fun TiledMap.containsProperty(key: String) = properties.containsKey(key)

/**
 * Extension property to retrieve the width of the [TiledMap]. If the map does not have
 * a **width** property then 0 is returned
 */
val TiledMap.width: Int
    get() = property("width", 0)

/**
 * Extension property to retrieve the height of the [TiledMap]. If the map does not have
 * a **height** property then 0 is returned
 */
val TiledMap.height: Int
    get() = property("height", 0)

/**
 * Extension property to retrieve the tile width of each tile of the [TiledMap]. If the map does not have
 * a **tilewidth** property then 0 is returned
 */
val TiledMap.tileWidth: Int
    get() = property("tilewidth", 0)

/**
 * Extension property to retrieve the tile height of each tile of the [TiledMap]. If the map does not have
 * a **tileheight** property then 0 is returned
 */
val TiledMap.tileHeight: Int
    get() = property("tileheight", 0)

/**
 * Extension method to retrieve the total width of the [TiledMap]. It is the result of the
 * width multiplied by the tile width of the map.
 *
 * @see [width]
 * @see [tileWidth]
 *
 * @return total width in pixels
 */
fun TiledMap.totalWidth() = width * tileWidth

/**
 * Extension method to retrieve the total height of the [TiledMap]. It is the result of the
 * height multiplied by the tile height of the map.
 *
 * @see [height]
 * @see [tileHeight]
 *
 * @return total height in pixels
 */
fun TiledMap.totalHeight() = height * tileHeight

/**
 * Extension method to retrieve a non-null [MapLayer] of the [TiledMap]. In case the layer
 * cannot be found, a default empty layer is returned with name **ktx-default-map-layer**.
 * @param layerName name of [MapLayer]
 * @return [MapLayer] of given name or an empty default map layer, if the specified layer does not exist
 */
fun TiledMap.layer(layerName: String) = layers[layerName] ?: DEFAULT_MAP_LAYER

/**
 * Extension method to easily execute an action per [MapObject] of a given [MapLayer].
 * @param layerName name of [MapLayer]
 * @param action action to execute per [MapObject] of the [MapLayer]
 */
fun TiledMap.forEachMapObject(layerName: String, action: (MapObject) -> Unit) {
    layer(layerName).objects.forEach { action(it) }
}
