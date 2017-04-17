package ktx.app

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.graphics.GL20
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests [KotlinApplication] - KTX equivalent of [com.badlogic.gdx.ApplicationAdapter].
 * @author MJ
 */
class KotlinApplicationTest {
  @Before
  fun setUpOpenGL() {
    Gdx.gl20 = mock<GL20>()
    Gdx.gl = Gdx.gl20
  }

  private fun mockGraphicsWithDeltaTime(delta: Float): Graphics =
      mock<Graphics> {
        on(it.deltaTime) doReturn delta
        on(it.rawDeltaTime) doReturn delta
      }

  @Test
  fun `should not render if delta time is lower than fixed time step`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1 / 120f)
    val app = MockKotlinApplication(fixedTimeStep = 1 / 30f)

    assertFalse(app.rendered)
    app.render()
    assertFalse(app.rendered)
  }

  @Test
  fun `should render if delta time is equal to fixed time step`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1 / 30f)
    val app = MockKotlinApplication(fixedTimeStep = 1 / 30f)

    app.render()

    assertTrue(app.rendered)
    assertEquals(1, app.renderedTimes)
  }

  @Test
  fun `should render if delta time is higher than fixed time step`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1 / 30f)
    val app = MockKotlinApplication(fixedTimeStep = 1 / 60f)

    app.render()

    assertTrue(app.rendered)
    assertEquals(2, app.renderedTimes)
  }

  @Test
  fun `should render if delta times are collectively equal to or higher than fixed time step`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1 / 50f)
    val app = MockKotlinApplication(fixedTimeStep = 1 / 30f)

    app.render() // 0.02 - 0.0
    assertEquals(0, app.renderedTimes)
    app.render() // 0.04 - 0.0(3)
    assertEquals(1, app.renderedTimes)
    app.render() // 0.06 - 0.0(3)
    assertEquals(1, app.renderedTimes)
    app.render() // 0.08 - 0.0(6)
    assertEquals(2, app.renderedTimes)
  }

  @Test
  fun `should clear screen on render`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1 / 30f)
    val app = MockKotlinApplication(fixedTimeStep = 1 / 30f)

    app.render()

    verify(Gdx.gl).glClearColor(0f, 0f, 0f, 1f)
    verify(Gdx.gl).glClear(GL20.GL_COLOR_BUFFER_BIT)
  }

  @Test
  fun `should not render more times than max delta time limit allows`() {
    Gdx.graphics = mockGraphicsWithDeltaTime(1f)
    val app = MockKotlinApplication(fixedTimeStep = 1 / 60f, maxDeltaTime = 5f / 60f)

    app.render()

    assertEquals(5, app.renderedTimes)
  }

  @After
  fun clearStatics() {
    Gdx.graphics = null
    Gdx.gl = null
    Gdx.gl20 = null
  }

  /**
   * Example implementation of [KotlinApplication]. Reports rendering data for tests.
   * @author MJ
   */
  class MockKotlinApplication(fixedTimeStep: Float = 1f / 60f, maxDeltaTime: Float = 1f) :
      KotlinApplication(fixedTimeStep, maxDeltaTime) {
    var lastDelta = -1f
    var rendered = false
    var renderedTimes = 0

    override fun create() {
    }

    override fun render(delta: Float) {
      rendered = true
      lastDelta = delta
      renderedTimes++
    }
  }

  @Suppress("unused")
  class `Should implement KtxApplicationAdapter with no methods overridden` : KtxApplicationAdapter {
    // Guarantees all KtxApplicationAdapter methods are optional to implement.
  }

  @Suppress("unused")
  class `Should implement KtxInputAdapter with no methods overridden` : KtxInputAdapter {
    // Guarantees all KtxInputAdapter methods are optional to implement.
  }
}
