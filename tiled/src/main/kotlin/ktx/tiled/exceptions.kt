package ktx.tiled

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.utils.GdxRuntimeException

/**
 * Common type of exceptions thrown by the Tiled API extensions.
 */
open class TiledException(message: String, cause: Throwable? = null) : GdxRuntimeException(message, cause)

/**
 * [GdxRuntimeException] that is thrown when trying to access a non-existing property
 * of a [MapProperties] instance.
 */
class MissingPropertyException(message: String, cause: Throwable? = null) : TiledException(message, cause)

/**
 * [GdxRuntimeException] that is thrown when trying to access a non-existing [MapLayer]
 * of a [TiledMap] instance.
 */
class MissingLayerException(message: String, cause: Throwable? = null) : TiledException(message, cause)

/**
 * [GdxRuntimeException] that is thrown when trying to access a shape of a [MapObject]
 * that do not have any shape such as the [TextureMapObject].
 */
class MissingShapeException(message: String, cause: Throwable? = null) : TiledException(message, cause)
