package ktx.actors

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton

/**
 * Allows to access and modify [Label] text with idiomatic Kotlin property syntax.
 */
var Label.txt: String
  get() = text.toString()
  set(value) = setText(value)

/**
 * Allows to access and modify [TextButton] text with idiomatic Kotlin property syntax.
 */
var TextButton.txt: String
  get() = text.toString()
  set(value) = setText(value)
