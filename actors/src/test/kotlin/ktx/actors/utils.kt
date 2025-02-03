package ktx.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

/** @return [Stage] with mocked viewport and batch. */
internal fun getMockStage(
  viewportWidth: Float = 800f,
  viewportHeight: Float = 600f,
): Stage {
  Gdx.graphics = mock() // Referenced by Stage constructor.
  val viewport =
    mock<Viewport> {
      on(it.worldWidth) doReturn viewportWidth
      on(it.worldHeight) doReturn viewportHeight
    }
  return Stage(viewport, mock())
}
