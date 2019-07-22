package ktx.assets

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.*
import org.junit.Test
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests [Disposable] utilities.
 */
class DisposablesTest {
  @Test
  fun `should safely dispose heavy resources`() {
    val disposable = mock<Disposable>()

    disposable.disposeSafely()

    verify(disposable).dispose()
  }

  @Test
  fun `should silently ignore exceptions during safe disposing`() {
    val disposable = mock<Disposable> {
      on(it.dispose()) doThrow GdxRuntimeException("Expected.")
    }

    disposable.disposeSafely() // Should not throw any exceptions.

    verify(disposable).dispose()
  }

  @Test
  fun `should Silently Ignore Nulls During Safe Disposing`() {
    val disposable: Disposable? = null

    disposable.disposeSafely()

    assertNull(disposable)
  }

  @Test
  fun `should Dispose With Catch Block`() {
    val disposable = mock<Disposable>()

    disposable.dispose { ex -> fail(ex.message) } // Fails on error.

    verify(disposable).dispose()
  }

  @Test
  fun `should pass exceptions to catch block`() {
    val exception = GdxRuntimeException("Expected.")
    var thrown: Exception? = null
    val disposable = mock<Disposable> {
      on(it.dispose()) doThrow exception
    }

    disposable.dispose { thrown = it }

    assertSame(exception, thrown)
    verify(disposable).dispose()
  }

  @Test
  fun `should dispose iterables of assets`() {
    val disposables = GdxArray.with(mock<Disposable>(), mock(), mock())

    disposables.dispose()

    disposables.forEach { verify(it).dispose() }
  }

  @Test
  fun `should ignore nulls during iterables disposing`() {
    val disposables = GdxArray.with(mock<Disposable>(), mock(), null)

    disposables.dispose()

    disposables.forEach { it?.let { verify(it).dispose() } }
  }

  @Test
  fun shouldSafelyDisposeIterablesOfAssets() {
    val disposables = GdxArray.with(mock<Disposable>(), null, mock {
      on(it.dispose()) doThrow GdxRuntimeException("Expected.")
    })

    disposables.disposeSafely()

    disposables.forEach { it?.let { verify(it).dispose() } }
  }

  @Test
  fun `should dispose iterables of assets with catch block`() {
    val disposables = GdxArray.with(mock<Disposable>(), mock(), mock())

    disposables.dispose { fail(it.message) } // Fails on unexpected exception.

    disposables.forEach { verify(it).dispose() }
  }

  @Test
  fun `should pass exception on dispose of iterables of assets with catch block`() {
    val exception = GdxRuntimeException("Expected.")
    val disposables = GdxArray.with(mock<Disposable>(), null, mock {
      on(it.dispose()) doThrow exception
    })

    disposables.dispose { assertSame(exception, it) }

    disposables.forEach { it?.let { verify(it).dispose() } }
  }

  @Test
  fun `should dispose arrays of assets`() {
    val disposables = arrayOf(mock<Disposable>(), mock(), mock())

    disposables.dispose()

    disposables.forEach { verify(it).dispose() }
  }

  @Test
  fun `should safely dispose arrays of assets`() {
    val disposables = arrayOf(mock<Disposable>(), mock(), mock {
      on(it.dispose()) doThrow GdxRuntimeException("Expected.")
    })

    disposables.disposeSafely()

    disposables.forEach { verify(it).dispose() }
  }

  @Test
  fun `should dispose arrays of assets with catch block`() {
    val disposables = arrayOf(mock<Disposable>(), mock(), mock())

    disposables.dispose { fail(it.message) } // Fails on unexpected exception.

    disposables.forEach { verify(it).dispose() }
  }

  @Test
  fun `should pass exception on dispose of arrays of assets with catch block`() {
    val exception = GdxRuntimeException("Expected.")
    val disposables = arrayOf(mock<Disposable>(), mock(), mock {
      on(it.dispose()) doThrow exception
    })

    disposables.dispose { assertSame(exception, it) }

    disposables.forEach { verify(it).dispose() }
  }

  @Test
  fun `should ignore exceptions with a no-op`() {
    val exception = mock<Exception>()

    exception.ignore()

    verifyZeroInteractions(exception)
  }
}
