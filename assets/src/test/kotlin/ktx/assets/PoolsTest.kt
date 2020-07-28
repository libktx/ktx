package ktx.assets

import com.badlogic.gdx.utils.Pool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Tests [Pool] extensions.
 */
class PoolsTest {
  @Test
  fun `should invoke pool as no parameter function to provide instances`() {
    val pool = MockPool()

    val instance = pool() // Should work as "obtain".

    assertNotNull(instance)
    pool.free(instance)
    assertSame(instance, pool()) // Since the object was freed, pool should return the same instance.
  }

  @Test
  fun `should invoke pool as one parameter function to return instances`() {
    val pool = MockPool()
    val instance = pool.obtain()

    pool(instance) // Should work as "free".

    assertEquals(1, pool.free)
    assertSame(instance, pool.obtain()) // Since the object was freed, pool should return the same instance.
  }

  @Test
  fun `should create new pools with custom providers`() {
    val provided = "10"
    val pool = pool { provided } // Always returns `provided` string.

    val obtained = pool()

    assertSame(provided, obtained)
  }

  @Test
  fun `should honor max setting`() {
    val pool = pool(max = 5) { "Mock." }

    for (index in 1..10) pool.free("Value.")

    assertEquals(5, pool.free)
  }

  /**
   * Provides new [Any] instances.
   */
  private class MockPool : Pool<Any>() {
    override fun newObject(): Any = Any()
  }
}
