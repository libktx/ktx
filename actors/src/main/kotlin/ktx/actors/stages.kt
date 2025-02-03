package ktx.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Scaling.stretch
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.badlogic.gdx.utils.viewport.Viewport

/**
 * Allows to leverage named and default parameters to initiate a custom [Stage]. [batch] will be used to render
 * the [Stage], while [viewport]'s camera will affect how the [Stage] is rendered. If any of the parameters are
 * not given, the used default values match [Stage] no-arg constructor.
 */
fun stage(
  batch: Batch = SpriteBatch(),
  viewport: Viewport = getDefaultViewport(),
) = Stage(viewport, batch)

/**
 * Returns an instance of [Viewport] compatible with the [Stage] default constructor.
 */
private fun getDefaultViewport() =
  ScalingViewport(
    stretch,
    Gdx.graphics.width.toFloat(),
    Gdx.graphics.height.toFloat(),
    OrthographicCamera(),
  )
