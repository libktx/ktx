package ktx.app

import com.badlogic.gdx.utils.GdxRuntimeException
import io.kotlintest.matchers.shouldThrow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ExceptionsTest {
  @Test
  fun `should throw error`() {
    shouldThrow<GdxRuntimeException> {
      gdxError()
    }
  }

  @Test
  fun `should throw error given a null message`() {
    val exception =
      shouldThrow<GdxRuntimeException> {
        gdxError(null)
      }

    assertEquals("null", exception.message)
  }

  @Test
  fun `should throw error given a string message`() {
    val exception =
      shouldThrow<GdxRuntimeException> {
        gdxError("Test")
      }

    assertEquals("Test", exception.message)
  }

  @Test
  fun `should throw error given an object message`() {
    val exception =
      shouldThrow<GdxRuntimeException> {
        gdxError(42)
      }

    assertEquals("42", exception.message)
  }

  @Test
  fun `should throw error given a cause`() {
    val cause = Exception()

    val exception =
      shouldThrow<GdxRuntimeException> {
        gdxError("Message", cause)
      }

    assertEquals("Message", exception.message)
    assertSame(cause, exception.cause)
  }
}
