package ktx.assets

import com.badlogic.gdx.utils.Disposable
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import com.badlogic.gdx.utils.Array as GdxArray

/**
 * Tests [Disposable] utilities.
 * @author MJ
 */
class DisposablesTest {
  @Test
  fun shouldSafelyDisposeHeavyResources() {
    val disposable = MockDisposable()
    assertFalse(disposable.disposed)
    disposable.disposeSafely()
    assertTrue(disposable.disposed)
  }

  @Test
  fun shouldSilentlyIgnoreExceptionsDuringSafeDisposing() {
    val disposable = BrokenDisposable() // Always throws RuntimeException on dispose().
    disposable.disposeSafely() // Should not throw any exceptions.
    assertNotNull(disposable)
  }

  private fun getNullDisposable(): Disposable? = null
  @Test
  fun shouldSilentlyIgnoreNullsDuringSafeDisposing() {
    val disposable = getNullDisposable() // Always returns null.
    disposable.disposeSafely()
    assertNull(disposable)
  }

  @Test
  fun shouldDisposeWithCatchBlock() {
    val disposable = MockDisposable()
    assertFalse(disposable.disposed)
    disposable.dispose { ex -> fail(ex.message) } // Fails on error.
    assertTrue(disposable.disposed)
  }

  @Test
  fun shouldPassExceptionsToCatchBlock() {
    val disposable = BrokenDisposable()
    var caught = false
    disposable.dispose { caught = true }
    assertTrue(caught)
  }

  @Test
  fun shouldDisposeIterablesOfAssets() {
    val disposables = GdxArray.with(MockDisposable(), MockDisposable(), MockDisposable())
    disposables.forEach { assertFalse(it.disposed) }
    disposables.dispose()
    disposables.forEach { assertTrue(it.disposed) }
  }

  @Test
  fun shouldIgnoreNullsDuringIterablesDisposing() {
    val disposables = GdxArray.with(MockDisposable(), MockDisposable(), getNullDisposable())
    disposables.forEach { if (it is MockDisposable) assertFalse(it.disposed) }
    disposables.dispose()
    disposables.forEach { if (it is MockDisposable) assertTrue(it.disposed) }
  }

  @Test
  fun shouldSafelyDisposeIterablesOfAssets() {
    val disposables = GdxArray.with(BrokenDisposable(), MockDisposable(), getNullDisposable())
    disposables.forEach { if (it is MockDisposable) assertFalse(it.disposed) }
    disposables.disposeSafely()
    disposables.forEach { if (it is MockDisposable) assertTrue(it.disposed) }
  }

  @Test
  fun shouldDisposeIterablesOfAssetsWithCatchBlock() {
    val disposables = GdxArray.with(MockDisposable(), MockDisposable(), MockDisposable())
    disposables.forEach { assertFalse(it.disposed) }
    disposables.dispose { ex -> fail(ex.message) } // Fails on exceptions.
    disposables.forEach { assertTrue(it.disposed) }
  }

  @Test
  fun shouldPassExceptionOnDisposeOfIterablesOfAssetsWithCatchBlock() {
    val disposables = GdxArray.with(BrokenDisposable(), MockDisposable(), getNullDisposable())
    var exceptions = 0
    disposables.forEach { if (it is MockDisposable) assertFalse(it.disposed) }
    disposables.dispose { exceptions++ } // Increments exceptions counter on exception.
    disposables.forEach { if (it is MockDisposable) assertTrue(it.disposed) }
    assertEquals(1, exceptions) // BrokenDisposable should throw an exception.
  }

  @Test
  fun shouldDisposeArraysOfAssets() {
    val disposables = arrayOf(MockDisposable(), MockDisposable(), MockDisposable())
    disposables.forEach { assertFalse(it.disposed) }
    disposables.dispose()
    disposables.forEach { assertTrue(it.disposed) }
  }

  @Test
  fun shouldSafelyDisposeArraysOfAssets() {
    val disposables = arrayOf(MockDisposable(), BrokenDisposable(), MockDisposable())
    disposables.forEach { if (it is MockDisposable) assertFalse(it.disposed) }
    disposables.disposeSafely()
    disposables.forEach { if (it is MockDisposable) assertTrue(it.disposed) }
  }

  @Test
  fun shouldDisposeArraysOfAssetsWithCatchBlock() {
    val disposables = arrayOf(MockDisposable(), MockDisposable(), MockDisposable())
    disposables.forEach { assertFalse(it.disposed) }
    disposables.dispose { ex -> fail(ex.message) } // Fails on exceptions.
    disposables.forEach { assertTrue(it.disposed) }
  }

  @Test
  fun shouldPassExceptionOnDisposeOfArraysOfAssetsWithCatchBlock() {
    val disposables = GdxArray.with(BrokenDisposable(), MockDisposable(), BrokenDisposable())
    var exceptions = 0
    disposables.forEach { if (it is MockDisposable) assertFalse(it.disposed) }
    disposables.dispose { exceptions++ } // Increments exceptions counter on exception.
    disposables.forEach { if (it is MockDisposable) assertTrue(it.disposed) }
    assertEquals(2, exceptions) // BrokenDisposables should throw an exception.
  }

  @Test
  fun shouldIgnoreExceptionsWithANoOp() {
    val exception = RuntimeException("Should be unmodified.")
    val message = exception.message
    val cause = exception.cause
    exception.ignore()
    assertSame(message, exception.message)
    assertSame(cause, exception.cause)
  }

  /**
   * Allows to test [Disposable] utilities.
   * @author MJ
   */
  class MockDisposable : Disposable {
    var disposed = false
    override fun dispose() {
      disposed = true
    }
  }

  /**
   * Allows to test [Disposable] utilities.
   * @author MJ
   */
  class BrokenDisposable : Disposable {
    override fun dispose() = throw RuntimeException()
  }
}
