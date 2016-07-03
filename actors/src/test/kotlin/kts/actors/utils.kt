package kts.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.Viewport
import org.mockito.Mockito

/**
 * @return [Stage] with mocked viewport and batch
 */
internal fun getMockStage(): Stage {
  mockGraphics()
  return Stage(Mockito.mock(Viewport::class.java), Mockito.mock(Batch::class.java))
}

/**
 * Mocks [Gdx.graphics] instance.
 */
internal fun mockGraphics() {
  // Referenced by Stage constructor...
  Gdx.graphics = Mockito.mock(Graphics::class.java)
}
