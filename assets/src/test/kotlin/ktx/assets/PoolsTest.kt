package ktx.assets

import com.badlogic.gdx.utils.Pool
import com.badlogic.gdx.utils.Pool.Poolable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests [Pool] extensions.
 */
class PoolsTest {
  @Test
  fun `should invoke pool as no parameter function to provide instances`() {
    // Given:
    val pool = MockPool()

    // When: Pool is called as a function.
    val instance = pool()

    // Then: Should work as "obtain":
    assertNotNull(instance)
    pool.free(instance)
    assertSame(instance, pool()) // Since the object was freed, pool should return the same instance.
  }

  @Test
  fun `should invoke pool as one parameter function to return instances`() {
    // Given:
    val pool = MockPool()
    val instance = pool.obtain()

    // When: Pool is called as a function with object parameter.
    pool(instance)

    // Then: Should work as "free".
    assertEquals(1, pool.free)
    assertSame(instance, pool.obtain()) // Since the object was freed, pool should return the same instance.
  }

  @Test
  fun `should create new pools with custom providers`() {
    // Given: A pool that always returns the same instance:
    val provided = "10"
    val pool = pool { provided }

    // When:
    val obtained = pool()

    // Then:
    assertSame(provided, obtained)
  }

  @Test
  fun `should honor max setting`() {
    // Given:
    val pool = pool(max = 5) { "Mock." }

    // When:
    for (index in 1..10) pool.free("Value.")

    // Then:
    assertEquals(5, pool.free)
  }

  @Test
  fun `should create new pools with a custom discard function`() {
    // Given: A pool that adds discarded objects to a list:
    val discarded = mutableListOf<String>()
    val pool = pool(max = 5, discard = { discarded.add(it) }) { "Mock." }

    // When:
    for (index in 1..10) pool.free("Value$index")

    // Then:
    assertEquals(listOf("Value6", "Value7", "Value8", "Value9", "Value10"), discarded)
  }

  @Test
  fun `should create new pools that reset discarded objects by default`() {
    // Given:
    val pool = pool(max = 1) { SamplePoolable() }
    val freed = pool()
    val discarded = pool()
    pool.free(freed)

    // When:
    pool.free(discarded)

    // Then:
    assertTrue(discarded.isReset)
  }

  /**
   * A simple data object implementing the [Poolable] interface.
   */
  private class SamplePoolable(
    var isReset: Boolean = false,
  ) : Poolable {
    override fun reset() {
      this.isReset = true
    }
  }

  /**
   * Provides new [Any] instances.
   */
  private class MockPool : Pool<Any>() {
    override fun newObject(): Any = Any()
  }
}
