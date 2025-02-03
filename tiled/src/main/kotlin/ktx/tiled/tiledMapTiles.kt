package ktx.tiled

import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.tiled.TiledMapTile

/**
 * Extension method to directly access the [MapProperties] of a [TiledMapTile]. If the property
 * is not defined then this method throws a [MissingPropertyException].
 * @param key property name.
 * @return value of the property.
 * @throws MissingPropertyException If the property is not defined.
 */
inline fun <reified T> TiledMapTile.property(key: String): T =
  properties[key, T::class.java]
    ?: throw MissingPropertyException("Property $key does not exist for tile $id")

/**
 * Extension method to directly access the [MapProperties] of a [TiledMapTile]. The type is automatically
 * derived from the type of the given default value. If the property is not defined the [defaultValue]
 * will be returned.
 * @param key property name.
 * @param defaultValue default value in case the property is missing.
 * @return value of the property or defaultValue if property is missing.
 */
inline fun <reified T> TiledMapTile.property(
  key: String,
  defaultValue: T,
): T = properties[key, defaultValue, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [TiledMapTile]. If the property
 * is not defined then this method returns null.
 * @param key property name.
 * @return value of the property or null if the property is missing.
 */
inline fun <reified T> TiledMapTile.propertyOrNull(key: String): T? = properties[key, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [TiledMapTile] and its
 * [containsKey][MapProperties.containsKey] method.
 * @param key property name.
 * @return true if the property exists. Otherwise false.
 */
fun TiledMapTile.containsProperty(key: String) = properties.containsKey(key)
