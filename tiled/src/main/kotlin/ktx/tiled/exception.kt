package ktx.tiled

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.utils.GdxRuntimeException

/**
 * [GdxRuntimeException] that is thrown when trying to access a non-existing property of a [MapProperties] instance
 */
class MissingPropertyException(message: String) : GdxRuntimeException(message)

/**
 * [GdxRuntimeException] that is thrown when trying to access a non-existing [MapLayer] of a [TiledMap] instance
 */
class MissingLayerException(message: String) : GdxRuntimeException(message)

/**
 * [GdxRuntimeException] that is thrown when trying to access a shape of a [MapObject] that do not have any shape like [TextureMapObject]
 */
class MissingShapeException(message: String) : GdxRuntimeException(message)