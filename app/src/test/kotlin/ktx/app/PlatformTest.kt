package ktx.app

import com.badlogic.gdx.Application
import com.badlogic.gdx.Application.ApplicationType
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.GdxRuntimeException
import io.kotlintest.matchers.shouldThrow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

/**
 * Tests [Platform] utilities.
 */
@Suppress("DEPRECATION") // For Applet tests.
class PlatformTest {
  private fun fakeApp(
    platform: ApplicationType? = null,
    version: Int = 0,
  ): Application =
    mock {
      on { it.type } doReturn platform
      on { it.version } doReturn version
    }

  @After
  fun `clean libGDX application`() {
    Gdx.app = null
  }

  @Test
  fun `should return current platform if application is defined`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val platform = Platform.currentPlatform

    // Then:
    assertSame(ApplicationType.Desktop, platform)
  }

  @Test
  fun `should throw exception if application is undefined`() {
    // Given:
    Gdx.app = null

    // Expect:
    shouldThrow<GdxRuntimeException> {
      Platform.currentPlatform
    }
  }

  @Test
  fun `should throw exception if application type is undefined`() {
    // Given:
    Gdx.app = fakeApp(platform = null)

    // Expect:
    shouldThrow<GdxRuntimeException> {
      Platform.currentPlatform
    }
  }

  @Test
  fun `should check if the current platform is Applet`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Applet)

    // When:
    val isApplet = Platform.isApplet

    // Then:
    assertTrue(isApplet)
  }

  @Test
  fun `should check if the current platform is not Applet`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.WebGL)

    // When:
    val isApplet = Platform.isApplet

    // Then:
    assertFalse(isApplet)
  }

  @Test
  fun `should execute action if the current platform is Applet`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Applet)

    // When:
    val executed = Platform.runOnApplet { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute action if the current platform is not Applet`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.WebGL)

    // When:
    val executed = Platform.runOnApplet { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should check if the current platform is Android`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Android)

    // When:
    val isAndroid = Platform.isAndroid

    // Then:
    assertTrue(isAndroid)
  }

  @Test
  fun `should check if the current platform is not Android`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.iOS)

    // When:
    val isAndroid = Platform.isAndroid

    // Then:
    assertFalse(isAndroid)
  }

  @Test
  fun `should execute action if the current platform is Android`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Android)

    // When:
    val executed = Platform.runOnAndroid { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute action if the current platform is not Android`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.iOS)

    // When:
    val executed = Platform.runOnAndroid { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should check if the current platform is Desktop`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val isDesktop = Platform.isDesktop

    // Then:
    assertTrue(isDesktop)
  }

  @Test
  fun `should check if the current platform is not Desktop`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.HeadlessDesktop)

    // When:
    val isDesktop = Platform.isDesktop

    // Then:
    assertFalse(isDesktop)
  }

  @Test
  fun `should execute action if the current platform is Desktop`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val executed = Platform.runOnDesktop { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute action if the current platform is not Desktop`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.HeadlessDesktop)

    // When:
    val executed = Platform.runOnDesktop { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should check if the current platform is HeadlessDesktop`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.HeadlessDesktop)

    // When:
    val isHeadless = Platform.isHeadless

    // Then:
    assertTrue(isHeadless)
  }

  @Test
  fun `should check if the current platform is not HeadlessDesktop`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val isHeadless = Platform.isHeadless

    // Then:
    assertFalse(isHeadless)
  }

  @Test
  fun `should execute action if the current platform is HeadlessDesktop`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.HeadlessDesktop)

    // When:
    val executed = Platform.runOnHeadless { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute action if the current platform is not HeadlessDesktop`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val executed = Platform.runOnHeadless { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should check if the current platform is iOS`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.iOS)

    // When:
    val isiOS = Platform.isiOS

    // Then:
    assertTrue(isiOS)
  }

  @Test
  fun `should check if the current platform is not iOS`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Android)

    // When:
    val isiOS = Platform.isiOS

    // Then:
    assertFalse(isiOS)
  }

  @Test
  fun `should execute action if the current platform is iOS`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.iOS)

    // When:
    val executed = Platform.runOniOS { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute action if the current platform is not iOS`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Android)

    // When:
    val executed = Platform.runOniOS { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should check if the current platform is mobile on Android`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Android)

    // When:
    val isMobile = Platform.isMobile

    // Then:
    assertTrue(isMobile)
  }

  @Test
  fun `should check if the current platform is mobile on iOS`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.iOS)

    // When:
    val isMobile = Platform.isMobile

    // Then:
    assertTrue(isMobile)
  }

  @Test
  fun `should check if the current platform is not mobile`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val isMobile = Platform.isMobile

    // Then:
    assertFalse(isMobile)
  }

  @Test
  fun `should execute action if the current platform is mobile on iOS`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.iOS)

    // When:
    val executed = Platform.runOnMobile { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should execute action if the current platform is mobile on Android`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Android)

    // When:
    val executed = Platform.runOnMobile { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute action if the current platform is not mobile`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val executed = Platform.runOnMobile { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should check if the current platform is WebGL`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.WebGL)

    // When:
    val isWebGL = Platform.isWeb

    // Then:
    assertTrue(isWebGL)
  }

  @Test
  fun `should check if the current platform is not WebGL`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Applet)

    // When:
    val isWebGL = Platform.isWeb

    // Then:
    assertFalse(isWebGL)
  }

  @Test
  fun `should execute action if the current platform is WebGL`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.WebGL)

    // When:
    val executed = Platform.runOnWeb { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute action if the current platform is not WebGL`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Applet)

    // When:
    val executed = Platform.runOnWeb { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should return platform version`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Android, version = 20)

    // When:
    val version = Platform.version

    // Then:
    assertEquals(20, version)
  }

  @Test
  fun `should return default version on platforms without API version`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val version = Platform.version

    // Then:
    assertEquals(0, version)
  }

  @Test
  fun `should return -1 if application is not defined`() {
    // Given:
    Gdx.app = null

    // When:
    val version = Platform.version

    // Then:
    assertEquals(-1, version)
  }

  @Test
  fun `should execute if version is above the minimum given value`() {
    // Given:
    Gdx.app = fakeApp(version = 10)

    // When:
    val executed = Platform.runOnVersion(minVersion = 5) { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should execute if version is equal to the minimum given value`() {
    // Given:
    Gdx.app = fakeApp(version = 10)

    // When:
    val executed = Platform.runOnVersion(minVersion = 10) { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute if version is below the minimum given value`() {
    // Given:
    Gdx.app = fakeApp(version = 10)

    // When:
    val executed = Platform.runOnVersion(minVersion = 15) { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should execute if version is below the maximum given value`() {
    // Given:
    Gdx.app = fakeApp(version = 10)

    // When:
    val executed = Platform.runOnVersion(maxVersion = 15) { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should execute if version is equal to the maximum given value`() {
    // Given:
    Gdx.app = fakeApp(version = 10)

    // When:
    val executed = Platform.runOnVersion(maxVersion = 10) { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute if version is above the maximum given value`() {
    // Given:
    Gdx.app = fakeApp(version = 10)

    // When:
    val executed = Platform.runOnVersion(maxVersion = 5) { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should execute if platform is not specified`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val executed = Platform.runOnVersion(platform = null) { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should execute if platform is equal to the given value`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val executed = Platform.runOnVersion(platform = ApplicationType.Desktop) { true } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute if platform is not equal to the given value`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Desktop)

    // When:
    val executed = Platform.runOnVersion(platform = ApplicationType.Android) { true } ?: false

    // Then:
    assertFalse(executed)
  }

  @Test
  fun `should execute if version requirements are met`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Android, version = 25)

    // When:
    val executed =
      Platform.runOnVersion(minVersion = 20, maxVersion = 30, platform = ApplicationType.Android) {
        true
      } ?: false

    // Then:
    assertTrue(executed)
  }

  @Test
  fun `should not execute if version requirements are not met`() {
    // Given:
    Gdx.app = fakeApp(platform = ApplicationType.Android, version = 25)

    // When:
    val executed =
      Platform.runOnVersion(minVersion = 8, maxVersion = 12, platform = ApplicationType.iOS) {
        true
      } ?: false

    // Then:
    assertFalse(executed)
  }
}
