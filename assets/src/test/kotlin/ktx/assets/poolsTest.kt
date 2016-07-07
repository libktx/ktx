package ktx.assets

import com.badlogic.gdx.utils.Pool
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests [Pool] extensions.
 * @author MJ
 */
class PoolsTest {
  @Test
  fun shouldInvokePoolAsNoParameterFunctionToProvideInstances() {
    val pool = MockPool()
    val instance = pool() // Should work as "obtain".
    assertNotNull(instance)
    pool.free(instance)
    assertSame(instance, pool()) // Since the object was freed, pool should return the same instance.
  }

  @Test
  fun shouldInvokePoolAsOneParameterFunctionToReturnInstances() {
    val pool = MockPool()
    val instance = pool.obtain()
    assertNotNull(instance)
    pool(instance) // Should work as "free".
    assertEquals(1, pool.free)
    assertSame(instance, pool.obtain()) // Since the object was freed, pool should return the same instance.
  }

  @Test
  fun shouldCreateNewPoolsWithProviders() {
    val pool = pool { "10" } // Always returns "10" string.
    val obtained = pool()
    assertEquals("10", obtained)
  }

  @Test
  fun shouldHonorMaxSetting() {
    val pool = pool(max = 5) { "Mock." }
    for (index in 1..10) pool.free("Value.")
    assertEquals(5, pool.free)
  }

  /**
   * Provides new object instances.
   * @author MJ
   */
  private class MockPool : Pool<Any>() {
    override fun newObject(): Any = Any()
  }
}
