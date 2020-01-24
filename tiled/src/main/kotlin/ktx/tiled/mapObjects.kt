package ktx.tiled

import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.*
import com.badlogic.gdx.math.*

/**
 * Extension method to directly access the [MapProperties] of a [MapObject]. If the property
 * is not defined then this method throws a [MissingPropertyException].
 * @param key property name.
 * @return value of the property.
 * @throws MissingPropertyException If the property is not defined.
 */
inline fun <reified T> MapObject.property(key: String): T = properties[key, T::class.java]
    ?: throw MissingPropertyException("Property $key does not exist for object $name")

/**
 * Extension method to directly access the [MapProperties] of a [MapObject]. The type is automatically
 * derived from the type of the given default value. If the property is not defined the [defaultValue]
 * will be returned.
 * @param key property name.
 * @param defaultValue default value in case the property is missing.
 * @return value of the property or defaultValue if property is missing.
 */
inline fun <reified T> MapObject.property(key: String, defaultValue: T): T = properties[key, defaultValue, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [MapObject]. If the property
 * is not defined then this method returns null.
 * @param key property name.
 * @return value of the property or null if the property is missing.
 */
inline fun <reified T> MapObject.propertyOrNull(key: String): T? = properties[key, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [MapObject] and its
 * [containsKey][MapProperties.containsKey] method.
 * @param key property name.
 * @return true if the property exists. Otherwise false.
 */
fun MapObject.containsProperty(key: String) = properties.containsKey(key)

/**
 * Extension property to retrieve the x-coordinate of the [MapObject].
 * @throws MissingPropertyException if property x does not exist.
 */
val MapObject.x: Float
  get() = property("x")

/**
 * Extension property to retrieve the y-coordinate of the [MapObject].
 * @throws MissingPropertyException if property y does not exist.
 */
val MapObject.y: Float
  get() = property("y")

/**
 * Extension property to retrieve the width of the [MapObject].
 * @throws MissingPropertyException if property width does not exist.
 */
val MapObject.width: Float
  get() = property("width")

/**
 * Extension property to retrieve the height of the [MapObject].
 * @throws MissingPropertyException if property height does not exist.
 */
val MapObject.height: Float
  get() = property("height")

/**
 * Extension property to retrieve the unique ID of the [MapObject].
 * @throws MissingPropertyException if property id does not exist.
 */
val MapObject.id: Int
  get() = property("id")

/**
 * Extension property to retrieve the rotation of the [MapObject]. Null if the property is unset.
 */
val MapObject.rotation: Float?
  get() = propertyOrNull("rotation")

/**
 * Extension property to retrieve the type of the [MapObject]. Null if the property is unset.
 */
val MapObject.type: String?
  get() = propertyOrNull("type")

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
 * Note that objects that do not have any shape like [TextureMapObject] will throw a [MissingShapeException]
 * @throws MissingShapeException If the object does not have any shape
 */
val MapObject.shape: Shape2D
  get() = when (this) {
    is CircleMapObject -> circle
    is EllipseMapObject -> ellipse
    is PolylineMapObject -> polyline
    is PolygonMapObject -> polygon
    is RectangleMapObject -> rectangle
    else -> throw MissingShapeException("MapObject of type ${this::class.java} does not have a shape.")
  }
