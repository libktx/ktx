package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ObjectMap

/**
 * An equivalent of [com.badlogic.gdx.Game] delegating game events to a [Screen]. On contrary to `Game`, [KtxGame]
 * maintains a collection of screens mapped by their type and allows to change screens knowing only their type with
 * [setScreen]. Thanks to this, its easier to cache screens without maintaining a singleton with all [Screen] instances
 * manually. [ScreenType] generic type allows to users to use an extended specific base class (or interface) for all
 * screens, without locking into [Screen].
 *
 * @param firstScreen will be immediately used by the application. Note that it cannot use any resources initiated by
 * the libGDX (like the OpenGL context) in the constructor, as the screen will be created before the application is
 * launched. Defaults to an empty, mock-up screen implementation that should be replaced with the first [setScreen]
 * method call in [create]. Note: `firstScreen` still has to be explicitly registered with [addScreen] if you want it to
 * be accessible with [getScreen].
 * @param clearScreen if true (the default), [clearScreen] will be called before screen rendering.
 * @param ScreenType common base interface or class of all screens. Allows to use custom extended [Screen] API.
 * @see KtxScreen
 */
open class KtxGame<ScreenType : Screen>(
  firstScreen: ScreenType? = null,
  private val clearScreen: Boolean = true,
) : KtxApplicationAdapter {
  /** Holds references to all screens registered with [addScreen]. Allows to get a reference of the screen instance
   * knowing only its type. */
  protected val screens: ObjectMap<Class<out ScreenType>, ScreenType> = ObjectMap()

  /** Currently shown screen. Unless overridden with [setScreen], uses an empty mock-up implementation to work around
   * nullability and `lateinit` issues. [shownScreen] is a public property exposing this value as [ScreenType].
   * @see shownScreen */
  protected var currentScreen: Screen = firstScreen ?: emptyScreen()

  /** Provides direct access to current [Screen] instance handling game events. */
  open val shownScreen: ScreenType
    @Suppress("UNCHECKED_CAST")
    get() = currentScreen as ScreenType

  /**
   * By default, this method shows ([Screen.show]) and resizes ([Screen.resize]) the initial view. You do not have to call
   * super if you used [setScreen] in the overridden [create] method or the selected first view does not need to be
   * resized and showed before usage.
   */
  override fun create() {
    currentScreen.show()
    currentScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
  }

  override fun render() {
    if (clearScreen) {
      clearScreen(0f, 0f, 0f, 1f)
    }
    currentScreen.render(Gdx.graphics.deltaTime)
  }

  override fun resize(
    width: Int,
    height: Int,
  ) {
    currentScreen.resize(width, height)
  }

  override fun pause() {
    currentScreen.pause()
  }

  override fun resume() {
    currentScreen.resume()
  }

  /**
   * Registers an instance of [Screen].
   * @param Type concrete class of the [Screen] instance. The implementation assumes that screens are singletons and
   * only one implementation of each class will be registered.
   * @param screen instance of [Type]. After invocation of this method, [setScreen] can be used with the appropriate
   * class to change current screen to this object.
   * @throws GdxRuntimeException if a screen of selected type is already registered. Use [removeScreen] first to replace
   * the screen.
   * @see getScreen
   * @see setScreen
   * @see removeScreen
   */
  inline fun <reified Type : ScreenType> addScreen(screen: Type) = addScreen(Type::class.java, screen)

  /**
   * Registers an instance of [Screen]. Override this method to change the way screens are registered.
   * @param type concrete class of the [Screen] instance. The implementation assumes that screens are singletons and
   * only one implementation of each class will be registered.
   * @param screen instance of [Type]. After invocation of this method, [setScreen] can be used with the appropriate
   * class to change current screen to this object.
   * @see getScreen
   * @see setScreen
   * @see removeScreen
   */
  open fun <Type : ScreenType> addScreen(
    type: Class<Type>,
    screen: Type,
  ) {
    !screens.containsKey(type) || throw GdxRuntimeException("Screen already registered to type: $type.")
    screens.put(type, screen)
  }

  /**
   * Replaces current screen with the registered screen instance of the passed type.
   * @param Type concrete class of the screen implementation. The screen instance of this type must have been added
   * with [addScreen] before calling this method.
   * @see addScreen
   * @see shownScreen
   */
  inline fun <reified Type : ScreenType> setScreen() = setScreen(Type::class.java)

  /**
   * Replaces current screen with the registered screen instance of the passed type. Calls `hide` method of the
   * previous screen and `show` method of the current screen. Override this method to control screen transitions.
   * @param type concrete class of the screen implementation. The screen instance of this type must have been added with
   * [addScreen] before calling this method.
   * @see addScreen
   * @see shownScreen
   */
  open fun <Type : ScreenType> setScreen(type: Class<Type>) {
    currentScreen.hide()
    currentScreen = getScreen(type)
    currentScreen.show()
    currentScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
  }

  /**
   * Returns cached instance of [Screen] of the selected type.
   * @param Type concrete class of the screen implementation. The screen instance of this type must have been added with
   * [addScreen] before calling this method.
   * @return an instance of [Screen] extending passed [Type].
   * @throws GdxRuntimeException if instance of the selected [Type] was not registered with [addScreen].
   * @see addScreen
   */
  inline fun <reified Type : ScreenType> getScreen(): Type = getScreen(Type::class.java)

  /**
   * Returns cached instance of [Type] of the selected type.
   * @param type concrete class of the screen implementation. The screen instance of this type must have been added with
   * [addScreen] before calling this method.
   * @return an instance of [Type] extending passed [type].
   * @throws GdxRuntimeException if instance of the selected [type] was not registered with [addScreen].
   * @see addScreen
   */
  @Suppress("UNCHECKED_CAST")
  open fun <Type : ScreenType> getScreen(type: Class<Type>): Type =
    screens[type] as Type? ?: throw GdxRuntimeException("Missing screen instance of type: $type.")

  /**
   * Removes cached instance of [Screen] of the selected type. Note that this method does not dispose of the screen and
   * will not affect [shownScreen].
   * @param Type concrete class of the screen implementation.
   * @return removed instance of [Screen] extending passed [Type] if was registered. `null` otherwise.
   * @see addScreen
   */
  inline fun <reified Type : ScreenType> removeScreen(): Type? = removeScreen(Type::class.java)

  /**
   * Removes cached instance of [Screen] of the selected type. Note that this method does not dispose of the screen and
   * will not affect [shownScreen].
   * @param type concrete class of the screen implementation.
   * @return removed instance of [Screen] extending passed [type] if was registered. `null` otherwise.
   * @see addScreen
   */
  @Suppress("UNCHECKED_CAST")
  open fun <Type : ScreenType> removeScreen(type: Class<Type>): Type? = screens.remove(type) as Type?

  /**
   * Checks if screen of the given type is registered.
   * @param Type concrete class of a screen implementation.
   * @return true if a [Screen] is registered with the selected type, false otherwise.
   */
  inline fun <reified Type : ScreenType> containsScreen(): Boolean = containsScreen(Type::class.java)

  /**
   * Checks if screen of the given type is registered.
   * @param type concrete class of a screen implementation.
   * @return true if a [Screen] is registered with the selected type, false otherwise.
   */
  open fun <Type : ScreenType> containsScreen(type: Class<Type>): Boolean = screens.containsKey(type)

  /**
   * Disposes of all registered screens with [Screen.dispose]. Catches thrown errors and logs them with libGDX
   * application API by default. Override [onScreenDisposalError] method to change error handling behavior. Should be
   * called automatically by libGDX application lifecycle handler.
   */
  override fun dispose() {
    screens.values().forEach {
      try {
        it.dispose()
      } catch (exception: Throwable) {
        onScreenDisposalError(it, exception)
      }
    }
  }

  /**
   * Invoked during screens disposal on [dispose] if an error occurs.
   * @param screen thrown [exception] during disposal.
   * @param exception unexpected screen disposal exception.
   */
  protected open fun onScreenDisposalError(
    screen: ScreenType,
    exception: Throwable,
  ) {
    Gdx.app.error("KTX", "Unable to dispose of ${screen.javaClass} screen.", exception)
  }
}

/**
 * Provides empty implementations of all [Screen] methods, making them optional to override.
 *
 * Explicitly extends the [Disposable] interface, matching the [Screen.dispose] method,
 * which allows to leverage [Disposable] utilities.
 * @see KtxGame
 */
interface KtxScreen :
  Screen,
  Disposable {
  override fun show() = Unit

  override fun render(delta: Float) = Unit

  override fun resize(
    width: Int,
    height: Int,
  ) = Unit

  override fun pause() = Unit

  override fun resume() = Unit

  override fun hide() = Unit

  override fun dispose() = Unit
}

/**
 * Provides a no-op implementation of [Screen]. A workaround of [Screen] nullability issues.
 * @see KtxGame
 */
fun emptyScreen(): Screen = object : KtxScreen {}
