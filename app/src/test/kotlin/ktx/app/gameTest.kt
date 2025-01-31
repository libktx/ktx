package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.GdxRuntimeException
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

/**
 * Tests [KtxGame]: KTX equivalent of [com.badlogic.gdx.Game].
 */
class KtxGameTest {
  @Before
  fun `set up OpenGL`() {
    Gdx.gl20 = mock()
    Gdx.gl = Gdx.gl20
  }

  @Test
  fun `should display firstScreen without registration`() {
    val screen = mock<Screen>()
    val game = KtxGame(firstScreen = screen)
    Gdx.graphics =
      mock {
        on(it.width) doReturn 800
        on(it.height) doReturn 600
      }

    game.create()

    assertSame(screen, game.shownScreen)
    verify(screen).show()
    verify(screen).resize(800, 600)
    // addScreen must be called manually to keep firstScreen in context - should not contain initial Screen:
    assertFalse(game.containsScreen<Screen>())
  }

  @Test
  fun `should clear screen on render`() {
    val game = KtxGame(firstScreen = MockScreen())
    Gdx.graphics = mock()

    game.render()

    verify(Gdx.gl).glClearColor(0f, 0f, 0f, 1f)
    verify(Gdx.gl).glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
  }

  @Test
  fun `should not clear screen on render if screen clearing is turned off`() {
    val game = KtxGame(clearScreen = false, firstScreen = MockScreen())
    Gdx.graphics = mock()

    game.render()

    verify(Gdx.gl, never()).glClearColor(any(), any(), any(), any())
    verify(Gdx.gl, never()).glClear(any())
  }

  @Test
  fun `should delegate resize call to current screen`() {
    val screen = mock<Screen>()
    val game = KtxGame(screen)

    game.resize(100, 200)

    verify(screen).resize(100, 200)
  }

  @Test
  fun `should delegate pause call to current screen`() {
    val screen = mock<Screen>()
    val game = KtxGame(screen)

    game.pause()

    verify(screen).pause()
  }

  @Test
  fun `should delegate resume call to current screen`() {
    val screen = mock<Screen>()
    val game = KtxGame(screen)

    game.resume()

    verify(screen).resume()
  }

  @Test(expected = GdxRuntimeException::class)
  fun `should fail to provide non-registered Screen`() {
    val game = KtxGame<Screen>()

    game.getScreen<KtxScreen>()
  }

  @Test
  fun `should register Screen instance`() {
    val screen = mock<KtxScreen>()
    val game = KtxGame<Screen>()

    game.addScreen(screen)

    assertTrue(game.containsScreen<KtxScreen>())
    assertSame(screen, game.getScreen<KtxScreen>())
  }

  @Test(expected = GdxRuntimeException::class)
  fun `should fail to register Screen instance to same type multiple times`() {
    val game = KtxGame<Screen>()

    game.addScreen(mock<KtxScreen>())
    game.addScreen(mock<KtxScreen>())
  }

  @Test
  fun `should not throw exception when trying to remove unregistered Screen`() {
    val game = KtxGame<Screen>()

    val removed = game.removeScreen<KtxScreen>()

    assertNull(removed)
    assertFalse(game.containsScreen<KtxScreen>())
  }

  @Test
  fun `should remove Screen`() {
    val screen = mock<KtxScreen>()
    val game = KtxGame<Screen>()
    game.addScreen(screen)

    val removed = game.removeScreen<KtxScreen>()

    assertSame(screen, removed)
    assertFalse(game.containsScreen<KtxScreen>())
    verify(screen, never()).dispose() // Should not dispose of Screen upon removal.
  }

  @Test
  fun `should reassign Screen`() {
    val game = KtxGame<Screen>()
    val initialScreen = mock<KtxScreen>()
    val replacement = mock<KtxScreen>()
    game.addScreen(initialScreen)

    game.removeScreen<KtxScreen>()
    game.addScreen(replacement)

    assertTrue(game.containsScreen<KtxScreen>())
    assertSame(replacement, game.getScreen<KtxScreen>())
    verify(initialScreen, never()).dispose() // Should not dispose of previous KtxScreen upon removal.
  }

  @Test
  fun `should set current Screen`() {
    val firstScreen = mock<Screen>()
    val secondScreen = mock<KtxScreen>()
    val game = KtxGame(firstScreen)
    game.addScreen(secondScreen)
    Gdx.graphics =
      mock {
        on(it.width) doReturn 800
        on(it.height) doReturn 600
      }

    game.setScreen<KtxScreen>()

    assertSame(secondScreen, game.shownScreen)
    verify(firstScreen).hide()
    verify(secondScreen).show()
    verify(secondScreen).resize(800, 600)
  }

  @Test(expected = GdxRuntimeException::class)
  fun `should fail to set unregistered Screen`() {
    val game = KtxGame<Screen>()

    game.setScreen<MockScreen>()
  }

  @Test
  fun `should dispose of all registered Screen instances`() {
    val screen = mock<Screen>()
    val ktxScreen = mock<KtxScreen>()
    val game = KtxGame<Screen>()
    game.addScreen(screen)
    game.addScreen(ktxScreen)

    game.dispose()

    verify(screen).dispose()
    verify(ktxScreen).dispose()
  }

  @Test
  fun `should dispose of all registered Screen instances with error handling`() {
    Gdx.app = mock()
    val screen = mock<Screen>()
    val ktxScreen =
      mock<KtxScreen> {
        on(it.dispose()) doThrow GdxRuntimeException("Expected.")
      }
    val mockScreen =
      mock<MockScreen> {
        on(it.dispose()) doThrow GdxRuntimeException("Expected.")
      }
    val game = KtxGame<Screen>()
    game.addScreen(screen)
    game.addScreen(ktxScreen)
    game.addScreen(mockScreen)

    game.dispose()

    verify(screen).dispose()
    verify(ktxScreen).dispose()
    // Ensures exceptions were logged:
    verify(Gdx.app, times(2)).error(eq("KTX"), any(), argThat { this is GdxRuntimeException })
  }

  @After
  fun `clear static libGDX variables`() {
    Gdx.graphics = null
    Gdx.gl = null
    Gdx.gl20 = null
    Gdx.app = null
  }

  /** [Screen] implementation that tracks how many times it was rendered. */
  open class MockScreen : KtxScreen {
    private var rendered = false
    private var renderedTimes = 0

    override fun render(delta: Float) {
      rendered = true
      renderedTimes++
    }
  }
}

/**
 * Tests [Screen] utilities.
 */
class KtxScreenTest {
  @Test
  fun `should provide mock-up screen instance`() {
    val screen = emptyScreen()

    assertTrue(Screen::class.isInstance(screen))
  }

  @Suppress("unused", "ClassName")
  class `should implement KtxScreen with no methods overridden` : KtxScreen {
    // Guarantees all KtxScreen methods are optional to implement.
  }
}
