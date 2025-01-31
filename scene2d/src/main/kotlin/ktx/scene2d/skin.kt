package ktx.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Style used by default by most widget constructors. Most factory methods default to this style if a custom one is not
 * chosen.
 */
const val defaultStyle = "default"

/**
 * Style used by default by constructors of some aligned widgets like SplitPane.
 */
const val defaultVerticalStyle = "default-vertical"

/**
 * Style used by default by constructors of some aligned widgets like SplitPane.
 */
const val defaultHorizontalStyle = "default-horizontal"

/**
 * Utility storage for global [Skin] object. The skin will be used by the widget factory methods by default if no custom
 * alternative skin instance is passed as an alternative.
 */
object Scene2DSkin {
  private var skin: Skin? = null
  private val listeners = GdxArray<(Skin) -> Unit>(4)

  /**
   * Used by the factory methods by default if no custom [Skin] is passed. Changing this value immediately invokes all
   * registered listeners. Throws [IllegalStateException] when accessed before overriding.
   */
  var defaultSkin: Skin
    get() =
      skin ?: throw IllegalStateException(
        "Default Scene2D Skin was accessed before initiation. In order to avoid this exception, " +
          "import ktx.scene2d.Scene2DSkin and override its defaultSkin property. For example: " +
          "Scene2DSkin.defaultSkin = Skin(); Scene2DSkin.defaultSkin = VisUI.getSkin()",
      )
    set(value) {
      skin = value
      for (listener in listeners) {
        listener(value)
      }
    }

  /**
   * @param listener will be invoked each time the [Skin] is reloaded.
   * @see removeListener
   */
  fun addListener(listener: (Skin) -> Unit) {
    listeners.add(listener)
  }

  /**
   * @param listener will no longer be invoked each time the default skin is changed.
   */
  fun removeListener(listener: (Skin) -> Unit) {
    listeners.removeValue(listener, true)
  }

  /**
   * Removes all skin reload listeners.
   */
  fun clearListeners() {
    listeners.clear()
  }
}
