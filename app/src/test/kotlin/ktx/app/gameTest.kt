package ktx.app

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.GdxRuntimeException
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests [KtxGame]: KTX equivalent of [com.badlogic.gdx.Game].
 */
class KtxGameTest {
  @Before
  fun `set up OpenGL`() {
    Gdx.gl20 = mock<GL20>()
    Gdx.gl = Gdx.gl20
  }

  @Test
  fun `should not render if delta time is lower than fixed time step`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1 / 120f)
    val screen = MockScreen()
    val game = KtxGame(fixedTimeStep = 1 / 30f, firstScreen = screen)

    game.render()

    assertFalse(screen.rendered)
  }

  @Test
  fun `should render if delta time is equal to fixed time step`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1 / 30f)
    val screen = MockScreen()
    val game = KtxGame(fixedTimeStep = 1 / 30f, firstScreen = screen)

    game.render()

    assertTrue(screen.rendered)
    assertEquals(1, screen.renderedTimes)
  }

  @Test
  fun `should render if delta time is higher than fixed time step`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(2 / 30f)
    val screen = MockScreen()
    val game = KtxGame(fixedTimeStep = 1 / 30f, firstScreen = screen)

    game.render()

    assertTrue(screen.rendered)
    assertEquals(2, screen.renderedTimes)
  }

  @Test
  fun `should render if delta times are collectively equal to or higher than fixed time step`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1 / 50f)
    val screen = MockScreen()
    val game = KtxGame(fixedTimeStep = 1 / 30f, firstScreen = screen)

    game.render() // 0.02 - 0.0
    assertEquals(0, screen.renderedTimes)

    game.render() // 0.04 - 0.0(3)
    assertEquals(1, screen.renderedTimes)

    game.render() // 0.06 - 0.0(3)
    assertEquals(1, screen.renderedTimes)

    game.render() // 0.08 - 0.0(6)
    assertEquals(2, screen.renderedTimes)
  }

  @Test
  fun `should clear screen on render`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1 / 30f)
    val screen = MockScreen()
    val game = KtxGame(fixedTimeStep = 1 / 30f, firstScreen = screen)

    game.render()

    verify(Gdx.gl).glClearColor(0f, 0f, 0f, 1f)
    verify(Gdx.gl).glClear(GL20.GL_COLOR_BUFFER_BIT)
  }

  @Test
  fun `should display firstScreen without registration`() {
    val screen = mock<Screen>()
    val game = KtxGame(firstScreen = screen)
    Gdx.graphics = mock<Graphics> {
      on(it.width) doReturn 800
      on(it.height) doReturn 600
    }

    game.create()

    assertSame(screen, game.shownScreen)
    verify(screen).resize(800, 600)
    verify(screen).show()
    // addScreen must be called manually to keep firstScreen in context - should not contain initial Screen:
    assertFalse(game.containsScreen<Screen>())
  }

  @Test
  fun `should not render more times than max delta time limit allows`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1f)
    val screen = MockScreen()
    val game = KtxGame(fixedTimeStep = 1 / 60f, maxDeltaTime = 5f / 60f, firstScreen = screen)

    game.render()

    assertEquals(5, screen.renderedTimes)
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
    Gdx.graphics = mock<Graphics> {
      on(it.width) doReturn 800
      on(it.height) doReturn 600
    }

    game.setScreen<KtxScreen>()

    assertSame(secondScreen, game.shownScreen)
    verify(firstScreen).hide()
    verify(secondScreen).resize(800, 600)
    verify(secondScreen).show()
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
    Gdx.app = mock<Application>()
    val screen = mock<Screen>()
    val ktxScreen = mock<KtxScreen> {
      on(it.dispose()) doThrow GdxRuntimeException("Expected.")
    }
    val mockScreen = mock<MockScreen> {
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
  fun `clear static LibGDX variables`() {
    Gdx.graphics = null
    Gdx.gl = null
    Gdx.gl20 = null
    Gdx.app = null
  }

  /** [Screen] implementation that tracks how many times it was rendered. */
  open class MockScreen : KtxScreen {
    var lastDelta = -1f
    var rendered = false
    var renderedTimes = 0

    override fun render(delta: Float) {
      rendered = true
      lastDelta = delta
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

    assertTrue(screen is Screen)
  }

  @Suppress("unused")
  class `should implement KtxScreen with no methods overridden` : KtxScreen {
    // Guarantees all KtxScreen methods are optional to implement.
  }
}
