package ktx.inject

import org.junit.Assert.*
import org.junit.Test
import java.util.Random

/**
 * Tests KTX dependency injection module.
 * @author MJ
 */
class DependencyInjectionTest {
  val context = Context()

  @Test
  fun shouldRegisterComponents() {
    context.bind { listOf<Any>() }
    assertTrue(List::class.java in context)
  }

  @Test
  fun shouldInjectComponents() {
    context.bind { mutableListOf<Any>() }
    assertTrue(MutableList::class.java in context)
    val list = context.inject<MutableList<String>>()
    assertNotNull(list)
    list.add("Test")
    assertEquals(1, list.size)
    assertEquals("Test", list[0])
  }

  @Test
  fun shouldBindSingletons() {
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
  fun shouldBindSingletonsToMultipleTypes() {
    assertFalse(context.contains<String>())
    assertFalse(context.contains<CharSequence>())
    context.bindSingleton(java.lang.String("Singleton"), String::class.java, CharSequence::class.java)
    assertTrue(context.contains<String>())
    assertTrue(context.contains<CharSequence>())
    assertSame(context.inject<String>(), context.provider<CharSequence>()())
    assertNotSame(context.inject<String>(), "Singleton")
  }

  @Test
  fun shouldInjectProviders() {
    context.bind { Random() }
    assertTrue(context.contains<Random>())
    val provider = context.provider<Random>()
    assertNotNull(provider)
    val random1 = provider()
    val random2 = provider()
    assertNotNull(random1)
    assertNotNull(random2)
    assertNotSame(random1, random2)
  }

  @Test
  fun shouldInjectBindProvidersToMultipleTypes() {
    assertFalse(context.contains<String>())
    assertFalse(context.contains<CharSequence>())
    context.bind(String::class.java, CharSequence::class.java) { java.lang.String("New") }
    assertTrue(context.contains<String>())
    assertTrue(context.contains<CharSequence>())
    assertNotSame(context.inject<String>(), context.provider<CharSequence>()())
  }

  @Test
  fun shouldInjectContext() {
    assertTrue(Context::class.java in context)
    assertTrue(context.contains<Context>())
    val injected = context.inject<Context>()
    assertNotNull(injected)
    assertSame(context, injected)
  }

  @Test
  fun shouldRemoveProvidersExceptForContextProvider() {
    context.bind { "Test" }
    assertTrue(context.contains<Context>())
    assertTrue(context.contains<String>())
    context.clear()
    assertTrue(context.contains<Context>())
    assertFalse(context.contains<String>())
  }

  @Test
  fun shouldFillStaticContext() {
    assertEquals(ContextContainer.defaultContext, inject<Context>())
    register {
      bind { Random() }
      bindSingleton(java.lang.String("Singleton"))
    }
    val random = inject<Random>()
    assertNotSame(random, inject<Random>())
    val randomProvider = provider<Random>()
    assertNotSame(random, randomProvider())

    val singleton = inject<String>()
    assertSame(singleton, inject<String>())
    assertNotSame("Singleton", singleton)
    val singletonProvider = provider<String>()
    assertSame(singleton, singletonProvider())
    assertNotSame("Singleton", singletonProvider())

    ContextContainer.defaultContext.clear()
    assertFalse(ContextContainer.defaultContext.contains<Random>())
    assertFalse(ContextContainer.defaultContext.contains<String>())
    assertTrue(ContextContainer.defaultContext.contains<Context>())
  }
}