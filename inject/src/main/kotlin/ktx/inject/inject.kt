package ktx.inject

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Disposable
import ktx.reflect.Reflection
import ktx.reflect.reflect
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

/**
 * Handles dependency injection mechanism. Allows binding selected classes with their providers.
 *
 * Note that [Context] instances are not considered fully thread-safe - while it is _usually_ safe to access a fully
 * built context from within multiple threads, you should override [createProvidersMap] method and return a thread-safe
 * [MutableMap] implementation to avoid concurrency bugs.
 */
open class Context : Disposable {
  @Suppress("LeakingThis")
  private val providers = createProvidersMap()

  init {
    // Context should be injectable:
    @Suppress("LeakingThis")
    bindSingleton(this)
  }

  /**
   * Override this method and provide a thread-safe map implementation to make the [Context] thread-safe.
   * @return a map storing all providers of the context. Used upon construction.
   */
  protected open fun createProvidersMap(): MutableMap<Class<*>, () -> Any> = mutableMapOf()

  /**
   * Provides instance of the selected type. Utility method allowing to call context as a function.
   * @return instance of the class with the selected type if a provider is present in the context.
   * @see inject
   */
  inline operator fun <reified Type : Any> invoke(): Type = getProvider(Type::class.java)()

  /**
   * Provides instance of the selected type.
   * @return instance of the class with the selected type if a provider is present in the context.
   * @throws InjectionException if no provider is registered for the selected type.
   */
  inline fun <reified Type : Any> inject(): Type = getProvider(Type::class.java)()

  /**
   * Extracts provider of instances of the selected type.
   * @return instance of a provider of objects with the selected type.
   * @throws InjectionException if no provider is registered for the selected type.
   */
  inline fun <reified Type : Any> provider(): () -> Type = getProvider(Type::class.java)

  /**
   * Extracts provider of instances of the selected type. This method is internally used by inlined injection methods.
   * @param forClass type of objects provided by the selected provider.
   * @return provider instance bind to the selected class.
   * @throws InjectionException if no provider is registered for the selected type.
   * @see provider
   * @see inject
   */
  @Suppress("UNCHECKED_CAST")
  open fun <Type> getProvider(forClass: Class<Type>): () -> Type {
    val provider = providers[forClass]
    return if (provider == null) {
      throw InjectionException("No provider registered for class: $forClass")
    } else {
      provider as () -> Type
    }
  }

  /**
   * Adds the selected provider to the [Context]. This method is internally used by inlined binding methods.
   * @param forClass type of objects provided by the registered provider.
   * @param provider will be bind to the selected class.
   * @throws InjectionException if provider is already defined.
   * @see bind
   * @see bindSingleton
   */
  open fun setProvider(
    forClass: Class<*>,
    provider: () -> Any,
  ) {
    forClass !in providers || throw InjectionException("Provider already defined for class: $forClass")
    providers[forClass] = provider
  }

  /**
   * Removes provider of instances of the selected type. This method is internally used by inlined removal methods.
   * @param ofClass type of objects provided by the selected provider.
   * @return removed provider instance bind to the selected class if any was registered or null.
   * @see remove
   */
  @Suppress("UNCHECKED_CAST")
  open fun <Type> removeProvider(ofClass: Class<Type>): (() -> Type)? = providers.remove(ofClass) as (() -> Type)?

  /**
   * Removes singleton or provider of instances of the selected type registered in the [Context].
   * @return removed provider instance bind to the selected class if any was registered or null.
   * @see bind
   * @see bindSingleton
   */
  inline fun <reified Type : Any> remove(): (() -> Type)? = removeProvider(Type::class.java)

  /**
   * @param type class of the provided components.
   * @return true if there is a provider registered for the selected type.
   */
  operator fun contains(type: Class<*>): Boolean = type in providers

  /**
   * @param type Kotlin class of the provided components.
   * @return true if there is a provider registered for the selected type.
   */
  operator fun contains(type: KClass<*>): Boolean = type.java in providers

  /** @return true if there is a provider registered for the selected type. */
  inline fun <reified Type : Any> contains(): Boolean = Type::class.java in this

  /**
   * Allows to bind a provider producing instances of the selected type.
   * @param provider will be bind with the selected type. If no type argument is passed, it will be bind to the same
   *    exact class as the object it provides.
   * @throws InjectionException if provider for the selected type is already defined.
   */
  inline fun <reified Type : Any> bind(noinline provider: () -> Type) = setProvider(Type::class.java, provider)

  /**
   * Allows to bind a singleton to the chosen class.
   * @param singleton will be converted to a provider that always returns the same instance. If no type argument is
   *    passed, it will be bind to its own class.
   * @throws InjectionException if provider for the selected type is already defined.
   */
  inline fun <reified Type : Any> bindSingleton(singleton: Type) = bind(SingletonProvider(singleton))

  /**
   * Allows to bind a singleton to the chosen class.
   * @param provider inlined. Immediately invoked a single time. Its result will be registered as a singleton.
   * @throws InjectionException if provider for the selected type is already defined.
   */
  inline fun <reified Type : Any> bindSingleton(provider: () -> Type) = bind(SingletonProvider(provider()))

  /**
   * Automatically registers a provider for [Type] that will use reflection to create a new instance each
   * time it is requested. All required constructor parameters will be extracted from [Context]. Note that
   * the provider might throw [InjectionException] if any of the dependencies are missing, or
   * [com.badlogic.gdx.utils.reflect.ReflectionException] when unable to construct an instance.
   * @param Type reified type of the provided instances. This class must have a single constructor.
   */
  @Reflection
  inline fun <reified Type : Any> bind() = bind<Type> { newInstanceOf() }

  /**
   * Automatically creates and registers an instance of [Type] with reflection. All required constructor parameters
   * will be extracted from [Context] and must be present before calling this method.
   * @param Type reified type of the provided instance. This class must have a single constructor.
   * @return the constructed [Type] instance with injected dependencies.
   * @throws InjectionException if any of the dependencies are missing.
   * @throws com.badlogic.gdx.utils.reflect.ReflectionException when unable to construct an instance.
   */
  @Reflection
  inline fun <reified Type : Any> bindSingleton(): Type = newInstanceOf<Type>().apply(::bindSingleton)

  /**
   * Constructs an instance of [Type] using reflection. All required constructor parameters will be extracted from
   * [Context] and must be present before calling this method.
   * @param Type reified type of the constructed instance. This class must have a single constructor.
   * @return a new instance of [Type] with injected dependencies.
   * @throws InjectionException if any of the dependencies are missing.
   * @throws com.badlogic.gdx.utils.reflect.ReflectionException when unable to construct an instance.
   */
  @Reflection
  inline fun <reified Type : Any> newInstanceOf(): Type {
    val constructor = reflect<Type>().constructor
    val parameters = constructor.parameterTypes.map { getProvider(it).invoke() }.toTypedArray()
    return constructor.newInstance(*parameters) as Type
  }

  /**
   * Allows to bind a provider to multiple classes in hierarchy of the provided instances class.
   * @param to list of interfaces and classes in the class hierarchy of the objects provided by the provider. Any time
   *    any of the passed classes will be requested for injection, the selected provider will be invoked.
   * @param provider provides instances of classes compatible with the passed types.
   * @throws InjectionException if provider for any of the selected types is already defined.
   */
  fun <Type : Any> bind(
    vararg to: KClass<out Type>,
    provider: () -> Type,
  ) = to.forEach {
    setProvider(it.java, provider)
  }

  /**
   * Allows to bind a singleton instance to multiple classes in its hierarchy.
   * @param to list of interfaces and classes in the class hierarchy of the singleton. Any time any of the passed classes
   *    will be requested for injection, the selected singleton will be returned.
   * @param singleton instance of class compatible with the passed types.
   * @throws InjectionException if provider for any of the selected types is already defined.
   */
  fun <Type : Any> bindSingleton(
    vararg to: KClass<out Type>,
    singleton: Type,
  ) = bind(*to, provider = SingletonProvider(singleton))

  /**
   * Allows to bind the result of the provider to multiple classes in its hierarchy.
   * @param to list of interfaces and classes in the class hierarchy of the provider. Any time any of the passed classes
   *    will be requested for injection, the selected provider will be returned.
   * @param provider inlined. Immediately invoked a single time. Its result will be registered as a singleton.
   * @throws InjectionException if provider for any of the selected types is already defined.
   */
  inline fun <Type : Any> bindSingleton(
    vararg to: KClass<out Type>,
    provider: () -> Type,
  ) = bind(*to, provider = SingletonProvider(provider()))

  /**
   * Removes all user-defined providers and singletons from the context. [Context] itselfs will still be present and
   * injectable with [provider] and [inject].
   */
  open fun clear() {
    providers.clear()
    bindSingleton(this)
  }

  /**
   * Disposes of all [Disposable] singletons and providers registered in the context and removes them. Note that if
   * registered provider _provides_ [Disposable] instances, but it does not track the resources and does not implement
   * [Disposable] itself, the provided assets will not be disposed by this method. [Context] does not track all injected
   * assets: only directly registered objects are disposed. [clear] is called after all assets are disposed. Errors are
   * caught and logged.
   *
   *     bindSingleton(SpriteBatch()) // SpriteBatch would be disposed.
   *     bind { BitmapFont() }        // Each provided BitmapFont would have to be disposed manually.
   *
   * @see clear
   */
  override fun dispose() {
    providers.remove(Context::class.java)
    providers.values.filterIsInstance<Disposable>().forEach { provider ->
      try {
        provider.dispose()
      } catch (error: Exception) {
        Gdx.app.error("KTX", "Unable to dispose of component: $provider.", error)
      }
    }
    clear()
  }
}

/**
 * Allows to register new components in the context with builder-like DSL.
 * @param init will be invoked on this context.
 * @return this context.
 * @see Context.bind
 * @see Context.bindSingleton
 */
@OptIn(ExperimentalContracts::class)
inline fun Context.register(init: Context.() -> Unit): Context {
  contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
  this.init()
  return this
}

/**
 * Wraps singletons registered in a [Context], allowing to dispose them.
 * @param singleton will be always provided by this provider.
 * @see Disposable
 */
data class SingletonProvider<out Type : Any>(
  val singleton: Type,
) : Disposable,
  () -> Type {
  /** @return [singleton]. */
  override operator fun invoke(): Type = singleton

  /** Disposes of the [singleton] if it implements the [Disposable] interface. */
  override fun dispose() {
    (singleton as? Disposable)?.dispose()
  }
}

/**
 * Thrown in case of any problems with the dependency injection mechanism.
 */
class InjectionException(
  message: String,
  cause: Throwable? = null,
) : RuntimeException(message, cause)
