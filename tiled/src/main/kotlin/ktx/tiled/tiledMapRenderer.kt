package ktx.tiled

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer
import com.badlogic.gdx.maps.tiled.renderers.beginInternal
import com.badlogic.gdx.maps.tiled.renderers.endInternal
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Rectangle

/**
 * Automatically calls [BatchTiledMapRenderer.beginRender] and [BatchTiledMapRenderer.endRender].
 * @param camera A camera to set on the renderer before [BatchTiledMapRenderer.beginRender].
 * @param block inlined. Executed after [BatchTiledMapRenderer.beginRender] and before [BatchTiledMapRenderer.endRender].
 */
inline fun <T : BatchTiledMapRenderer> T.use(
  camera: OrthographicCamera,
  block: (T) -> Unit,
) {
  this.setView(camera)
  this.use(block)
}

/**
 * Automatically calls [BatchTiledMapRenderer.beginRender] and [BatchTiledMapRenderer.endRender].
 * @param projection A projection matrix to set on the renderer before [BatchTiledMapRenderer.beginRender].
 * @param x map render boundary x value.
 * @param y map render boundary y value.
 * @param width map render boundary width value.
 * @param height map render boundary height value.
 * @param block inlined. Executed after [BatchTiledMapRenderer.beginRender] and before [BatchTiledMapRenderer.endRender].
 */
inline fun <T : BatchTiledMapRenderer> T.use(
  projection: Matrix4,
  x: Float,
  y: Float,
  width: Float,
  height: Float,
  block: (T) -> Unit,
) {
  this.setView(projection, x, y, width, height)
  this.use(block)
}

/**
 * Automatically calls [BatchTiledMapRenderer.beginRender] and [BatchTiledMapRenderer.endRender].
 * @param projection A projection matrix to set on the renderer before [BatchTiledMapRenderer.beginRender].
 * @param mapBoundary map render boundary.
 * @param block inlined. Executed after [BatchTiledMapRenderer.beginRender] and before [BatchTiledMapRenderer.endRender].
 */
inline fun <T : BatchTiledMapRenderer> T.use(
  projection: Matrix4,
  mapBoundary: Rectangle,
  block: (T) -> Unit,
) = this.use(projection, mapBoundary.x, mapBoundary.y, mapBoundary.width, mapBoundary.height, block)

/**
 * Automatically calls [BatchTiledMapRenderer.beginRender] and [BatchTiledMapRenderer.endRender].
 * @param block inlined. Executed after [BatchTiledMapRenderer.beginRender] and before [BatchTiledMapRenderer.endRender].
 */
inline fun <T : BatchTiledMapRenderer> T.use(block: (T) -> Unit) {
  this.beginInternal()

  block(this)

  this.endInternal()
}
