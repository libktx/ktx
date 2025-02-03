package ktx.graphics

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.math.Vector2

/**
 * Calculates center position of the chosen [text] written with this [BitmapFont] on an object
 * with given [width] and [height] drawn at [x] and [y] coordinates.
 *
 * The passed [text] rendered with the selected [BitmapFont] at the returned coordinates should
 * be in the middle of the object.
 *
 * Note that [x] and [y] are essentially an optional offset added to the calculated position.
 */
fun BitmapFont.center(
  text: String,
  width: Float,
  height: Float,
  x: Float = 0f,
  y: Float = 0f,
): Vector2 {
  val layout = GlyphLayout(this, text)
  return Vector2(x + (width - layout.width) / 2f, y + (height + layout.height) / 2f)
}
