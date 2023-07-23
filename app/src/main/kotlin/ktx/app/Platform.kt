package ktx.app

import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.GdxRuntimeException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Contains utilities for platform-specific code. Allows to easily determine current application platform
 * and its version. Can execute given actions only on specific platforms.
 */
object Platform {
  /**
   * The [ApplicationType] reported by the current libGDX application. Throws [GdxRuntimeException] when unable to
   * determine the platform.
   */
  val currentPlatform: ApplicationType
    get() = Gdx.app?.type ?: throw GdxRuntimeException("Gdx.app is not defined or is missing the application type.")

  /**
   * True if [ApplicationType.Applet] is the [currentPlatform].
   *
   * Deprecated. Since Applets are deprecated since Java 9 in 2017, developers are encouraged to use other technologies
   * to target the web platforms such as the WebGL backend.
   */
  @Deprecated(
    message = "Java Applets are deprecated since Java 9 in 2017.",
    replaceWith = ReplaceWith("isWeb"),
  )
  val isApplet: Boolean
    get() = currentPlatform === ApplicationType.Applet

  /**
   * True if [ApplicationType.Android] is the [currentPlatform].
   */
  val isAndroid: Boolean
    get() = currentPlatform === ApplicationType.Android

  /**
   * True if [ApplicationType.Desktop] with a graphical application is the [currentPlatform].
   */
  val isDesktop: Boolean
    get() = currentPlatform === ApplicationType.Desktop

  /**
   * True if [ApplicationType.HeadlessDesktop] without a graphical application is the [currentPlatform].
   */
  val isHeadless: Boolean
    get() = currentPlatform === ApplicationType.HeadlessDesktop

  /**
   * True if [ApplicationType.iOS] is the [currentPlatform].
   */
  val isiOS: Boolean
    get() = currentPlatform === ApplicationType.iOS

  /**
   * True if [ApplicationType.Android] or [ApplicationType.iOS] are the [currentPlatform].
   */
  val isMobile: Boolean
    get() = isAndroid || isiOS

  /**
   * True if [ApplicationType.WebGL] is the [currentPlatform]. To determine if the application is running in an Applet,
   * use [isApplet] instead.
   */
  val isWeb: Boolean
    get() = currentPlatform == ApplicationType.WebGL

  /**
   * Android API version on Android, major OS version on iOS, 0 on most other platforms, or -1 if unable to read.
   */
  val version: Int
    get() = Gdx.app?.version ?: -1

  /**
   * Executes [action] if the [currentPlatform] is [ApplicationType.Applet]. Returns [action] result or null.
   * @see isApplet
   */
  @Deprecated(
    message = "Java Applets are deprecated since Java 9 in 2017.",
    replaceWith = ReplaceWith("runOnWeb"),
  )
  @Suppress("DEPRECATION")
  @OptIn(ExperimentalContracts::class)
  inline fun <T> runOnApplet(action: () -> T?): T? {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    return if (isApplet) action() else null
  }

  /**
   * Executes [action] if the [currentPlatform] is [ApplicationType.Android]. Returns [action] result or null.
   * @see isAndroid
   */
  @OptIn(ExperimentalContracts::class)
  inline fun <T> runOnAndroid(action: () -> T?): T? {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    return if (isAndroid) action() else null
  }

  /**
   * Executes [action] if the [currentPlatform] is [ApplicationType.Desktop]. Returns [action] result or null.
   * @see isDesktop
   */
  @OptIn(ExperimentalContracts::class)
  inline fun <T> runOnDesktop(action: () -> T?): T? {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    return if (isDesktop) action() else null
  }

  /**
   * Executes [action] if the [currentPlatform] is [ApplicationType.HeadlessDesktop]. Returns [action] result or null.
   * @see isHeadless
   */
  @OptIn(ExperimentalContracts::class)
  inline fun <T> runOnHeadless(action: () -> T?): T? {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    return if (isHeadless) action() else null
  }

  /**
   * Executes [action] if the [currentPlatform] is [ApplicationType.iOS]. Returns [action] result or null.
   * @see isiOS
   */
  @OptIn(ExperimentalContracts::class)
  inline fun <T> runOniOS(action: () -> T?): T? {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    return if (isiOS) action() else null
  }

  /**
   * Executes [action] if the [currentPlatform] is [ApplicationType.Android] or [ApplicationType.iOS].
   * Returns [action] result or null.
   * @see isMobile
   */
  @OptIn(ExperimentalContracts::class)
  inline fun <T> runOnMobile(action: () -> T?): T? {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    return if (isMobile) action() else null
  }

  /**
   * Executes [action] if the [currentPlatform] is [ApplicationType.WebGL]. Returns [action] result or null.
   * Not that the [action] will not be executed in an Applet - use [runOnApplet] instead.
   * @see isWeb
   */
  @OptIn(ExperimentalContracts::class)
  inline fun <T> runOnWeb(action: () -> T?): T? {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    return if (isWeb) action() else null
  }

  /**
   * Executes [action] is the current platform [version] (such as Android API version or iOS major OS version)
   * is equal to or higher than [minVersion] and equal to or lower than [maxVersion]. If a [platform] is given,
   * the [currentPlatform] must also be the same in order to execute the [action].
   *
   * All parameters are optional; if a parameter is not given, the associated condition does not have to be met
   * to execute [action]. For example, if [minVersion] is given, but [maxVersion] is not, the current application
   * [version] has to be the same as or above [minVersion], but there is no upper bound. Similarly, if a [platform]
   * is not given, the [action] will be executed on any platform that meets the [version] criteria.
   *
   * Returns [action] result if it was executed or null otherwise.
   *
   * @see version
   */
  @OptIn(ExperimentalContracts::class)
  inline fun <T> runOnVersion(
    minVersion: Int? = null,
    maxVersion: Int? = null,
    platform: ApplicationType? = null,
    action: () -> T?,
  ): T? {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    val matchesMinVersion = minVersion === null || minVersion <= version
    val matchesMaxVersion = maxVersion === null || version <= maxVersion
    val matchesPlatform = platform === null || currentPlatform === platform
    return if (matchesMinVersion && matchesMaxVersion && matchesPlatform) action() else null
  }
}
