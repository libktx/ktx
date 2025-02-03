package ktx.assets

import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
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
    val disposable =
      mock<Disposable> {
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
    val disposable =
      mock<Disposable> {
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
    val disposables =
      GdxArray.with(
        mock<Disposable>(),
        null,
        mock {
          on(it.dispose()) doThrow GdxRuntimeException("Expected.")
        },
      )

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
    val disposables =
      GdxArray.with(
        mock<Disposable>(),
        null,
        mock {
          on(it.dispose()) doThrow exception
        },
      )

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
    val disposables =
      arrayOf(
        mock<Disposable>(),
        mock(),
        mock {
          on(it.dispose()) doThrow GdxRuntimeException("Expected.")
        },
      )

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
    val disposables =
      arrayOf(
        mock<Disposable>(),
        mock(),
        mock {
          on(it.dispose()) doThrow exception
        },
      )

    disposables.dispose { assertSame(exception, it) }

    disposables.forEach { verify(it).dispose() }
  }

  @Test
  fun `should ignore exceptions with a no-op`() {
    val exception = mock<Exception>()

    exception.ignore()

    verifyNoInteractions(exception)
  }

  @Test
  fun `should register disposables`() {
    class Parent : DisposableRegistry by DisposableContainer() {
      val disposableA = mock<Disposable>()

      init {
        val initialSuccess = register(disposableA)
        assertTrue(initialSuccess)
      }

      val disposableB = mock<Disposable>().alsoRegister()
    }

    val instance = Parent()
    val children = listOf(instance.disposableA, instance.disposableB)
    val registered = instance.registeredDisposables
    assertTrue(registered.containsAll(children))
    assertEquals(children.size, registered.size)

    val repeatedSuccess = instance.register(instance.disposableA)
    assertFalse(repeatedSuccess)
  }

  @Test
  fun `should deregister disposables`() {
    class Parent : DisposableRegistry by DisposableContainer() {
      val disposableA = mock<Disposable>().alsoRegister()
      val disposableB = mock<Disposable>().alsoRegister()

      fun deregisterB() = disposableB.alsoDeregister()
    }

    val instance = Parent()
    val initialSuccess = instance.deregister(instance.disposableA)
    assertTrue(initialSuccess)
    instance.deregisterB()
    assertTrue(instance.registeredDisposables.isEmpty())
    instance.dispose()
    verify(instance.disposableA, never()).dispose()
    verify(instance.disposableB, never()).dispose()
    val repeatedSuccess = instance.deregister(instance.disposableA)
    assertFalse(repeatedSuccess)
  }

  @Test
  fun `should deregister all disposables`() {
    class Parent : DisposableRegistry by DisposableContainer() {
      val disposableA = mock<Disposable>().alsoRegister()
      val disposableB = mock<Disposable>().alsoRegister()
    }

    val instance = Parent()
    val initialSuccess = instance.deregisterAll()
    assertTrue(initialSuccess)
    assertTrue(instance.registeredDisposables.isEmpty())
    instance.dispose()
    verify(instance.disposableA, never()).dispose()
    verify(instance.disposableB, never()).dispose()
    val repeatedSuccess = instance.deregisterAll()
    assertFalse(repeatedSuccess)
  }

  @Test
  fun `should dispose registered disposables`() {
    class Parent : DisposableRegistry by DisposableContainer() {
      val disposableA = mock<Disposable>().alsoRegister()
      val disposableB = mock<Disposable>().alsoRegister()
    }

    val instance = Parent()
    instance.dispose()
    verify(instance.disposableA).dispose()
    verify(instance.disposableB).dispose()
  }

  @Test
  fun `should safely dispose registered disposables`() {
    class Parent : DisposableRegistry by DisposableContainer() {
      val disposable =
        mock<Disposable> {
          on(it.dispose()) doThrow GdxRuntimeException("Expected.")
        }.alsoRegister()
    }

    val instance = Parent()
    instance.disposeSafely() // Should not throw any exceptions.
    verify(instance.disposable).dispose()
  }

  @Test
  fun `should register disposables by identity`() {
    class Asset : Disposable {
      var disposed = false

      override fun dispose() {
        disposed = true
      }

      override fun equals(other: Any?): Boolean = true

      override fun hashCode(): Int = 1
    }

    class Parent : DisposableRegistry by DisposableContainer() {
      val disposableA = Asset().alsoRegister()
      val disposableB = Asset().alsoRegister()
      val disposableC = Asset().alsoRegister()
    }

    val instance = Parent()
    instance.dispose()
    val children = with(instance) { listOf(disposableA, disposableB, disposableC) }
    val registered = instance.registeredDisposables
    assertTrue(children.all(Asset::disposed))
    assertTrue(children.all { child -> registered.find { it === child } != null })
    assertEquals(children.size, registered.size)
  }
}
