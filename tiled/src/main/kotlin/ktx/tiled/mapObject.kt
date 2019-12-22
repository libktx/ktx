package ktx.tiled

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.*
import com.badlogic.gdx.math.*

/**
 * Extension method to directly access the [MapProperties] of a [MapObject]. The type is automatically
 * derived from the type of the given default value. If the property is not defined the defaultValue will be returned.
 * @param key property name
 * @param defaultValue default value in case the property is missing
 * @return value of the property or defaultValue if property is missing
 */
inline fun <reified T> MapObject.property(key: String, defaultValue: T): T = this.properties[key, defaultValue, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [MapObject]. If the property
 * is not defined then this method returns null.
 * @param key property name
 * @return value of the property or null if the property is missing
 */
inline fun <reified T> MapObject.propertyOrNull(key: String): T? = this.properties[key, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [MapObject] and its [containsKey][MapProperties.containsKey] method
 * @param key property name
 * @return true if the property exists. Otherwise false
 */
fun MapObject.containsProperty(key: String) = properties.containsKey(key)

/**
 * Extension property to retrieve the x-coordinate of the [MapObject]. If the object does not have
 * a x-coordinate then 0 is returned
 */
val MapObject.x: Float
    get() = property("x", 0f)

/**
 * Extension property to retrieve the y-coordinate of the [MapObject]. If the object does not have
 * a y-coordinate then 0 is returned
 */
val MapObject.y: Float
    get() = property("y", 0f)

/**
 * Extension property to retrieve the width of the [MapObject]. If the object does not have
 * a width then 0 is returned
 */
val MapObject.width: Float
    get() = property("width", 0f)

/**
 * Extension property to retrieve the height of the [MapObject]. If the object does not have
 * a height then 0 is returned
 */
val MapObject.height: Float
    get() = property("height", 0f)

/**
 * Extension property to retrieve the unique ID of the [MapObject]. If the object does not have an
 * id then -1 is returned
 */
val MapObject.id: Int
    get() = property("id", -1)

/**
 * Extension method to retrieve the [Shape2D] instance of a [MapObject].
 * Depending on the type of the object a different shape will be returned:
 *
 * - [CircleMapObject] -> [Circle]
 * - [EllipseMapObject] -> [Ellipse]
 * - [PolylineMapObject] -> [Polyline]
 * - [PolygonMapObject] -> [Polygon]
 * - [RectangleMapObject] -> [Rectangle]
 *
 * Note that [TextureMapObject] is not supported by this method as it has no related shape.
 */
val MapObject.shape: Shape2D
    get() = when (this) {
        is CircleMapObject -> this.circle
        is EllipseMapObject -> this.ellipse
        is PolylineMapObject -> this.polyline
        is PolygonMapObject -> this.polygon
        is RectangleMapObject -> this.rectangle
        else -> throw UnsupportedOperationException("Shape extension function is not supported for MapObject of type ${this::class.java}")
    }
