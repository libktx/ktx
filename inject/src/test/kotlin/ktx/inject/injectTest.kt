package ktx.inject

import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import java.util.Random

/**
 * Tests the KTX dependency injection module: [Context] implementation.
 */
class DependencyInjectionTest {
  val context = Context()

  @Test
  fun `should register components`() {
    context.bind { listOf<Any>() }

    assertTrue(List::class.java in context)
  }

  @Test
  fun `should inject components`() {
    context.bind { mutableListOf<Any>() }

    val list = context.inject<MutableList<String>>()

    assertTrue(MutableList::class.java in context)
    assertNotNull(list)
    list.add("Test")
    assertEquals(1, list.size)
    assertEquals("Test", list[0])
  }

  @Test
  fun `should bind singletons`() {
    val singleton = Random()

    context.bindSingleton(singleton)

    assertTrue(context.contains<Random>())
    val provided = context.inject<Random>()
    assertSame(singleton, provided)
    assertSame(context.inject<Random>(), context.inject<Random>())

    val provider = context.provider<Random>()
    assertSame(singleton, provider())
    assertSame(provider(), provider())
  }

  @Test
  fun `should bind singletons to multiple types`() {
    val singleton = java.lang.String("Singleton")

    context.bindSingleton(singleton, String::class.java, CharSequence::class.java)

    assertTrue(context.contains<String>())
    assertTrue(context.contains<CharSequence>())
    assertSame(singleton, context.inject<String>())
    assertSame(context.inject<String>(), context.provider<CharSequence>()())
    assertNotSame("Singleton", context.inject<String>())
  }

  @Test
  fun `should inject providers`() {
    context.bind { Random() }

    val provider = context.provider<Random>()

    assertTrue(context.contains<Random>())
    assertNotNull(provider)
    val random1 = provider()
    val random2 = provider()
    assertNotNull(random1)
    assertNotNull(random2)
    assertNotSame(random1, random2)
  }

  @Test
  fun `should bind providers to multiple types`() {
    context.bind(String::class.java, CharSequence::class.java) { java.lang.String("New") }

    assertTrue(context.contains<String>())
    assertTrue(context.contains<CharSequence>())
    assertNotSame(context.inject<String>(), context.provider<CharSequence>()())
  }

  @Test
  fun `should inject context`() {
    val injected = context.inject<Context>()

    assertNotNull(injected)
    assertTrue(Context::class.java in context)
    assertTrue(context.contains<Context>())
    assertSame(context, injected)
  }

  @Test
  fun `should remove providers on clear except for context provider`() {
    context.bind { "Test" }

    context.clear()

    assertTrue(context.contains<Context>())
    assertFalse(context.contains<String>())
  }

  @Test
  fun `should fill context with builder DSL`() {
    context.register {
      bind { Random() }
      bindSingleton("Test")
    }

    assertTrue(context.contains<Random>())
    assertTrue(context.contains<String>())
  }

  @Test(expected = InjectionException::class)
  fun `should throw exception upon injection of missing type`() {
    context.inject<String>()
  }

  @Test(expected = InjectionException::class)
  fun `should throw exception upon injection of provider of missing type`() {
    context.provider<String>()
  }

  @Test
  fun `should inject instances with invocation syntax`() {
    context.bind { Random() }

    val injected: Random = context()

    assertNotNull(injected)
  }

  @After
  fun `clear context`() {
    context.clear()
  }
}
