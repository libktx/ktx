package ktx.inject

/**
 * Handles dependency injection mechanism. Allows to bind selected classes with their providers.
 *
 * Note that [Context] instances are not considered fully thread-safe - while it is _usually_ safe to access a fully
 * built context from within multiple threads, you should override [createProvidersMap] method and return a thread-safe
 * [MutableMap] implementation to avoid concurrency bugs.
 */
open class Context {
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
   * Utility mirror function, allowing to call context as a function.
   * @return instance of the class with the selected type if a provider is present in the context.
   * @see inject
   */
  inline operator fun <reified Type : Any> invoke(): Type {
    return getProvider(Type::class.java)()
  }

  /**
   * @return instance of the class with the selected type if a provider is present in the context.
   * @throws InjectionException if no provider is registered for the selected type.
   */
  inline fun <reified Type : Any> inject(): Type {
    return getProvider(Type::class.java)()
  }

  /**
   * @return instance of a provider of objects with the selected type.
   * @throws InjectionException if no provider is registered for the selected type.
   */
  inline fun <reified Type : Any> provider(): () -> Type {
    return getProvider(Type::class.java)
  }

  /**
   * @param forClass type of objects provided by the selected provider.
   * @return provider instance bind to the selected class.
   * @throws InjectionException if no provider is registered for the selected type.
   * @see provider
   * @see inject
   */
  @Suppress("UNCHECKED_CAST")
  fun <Type> getProvider(forClass: Class<Type>): () -> Type {
    val provider = providers[forClass]
    return if (provider == null)
      throw InjectionException("No provider registered for class: $forClass") else provider as () -> Type
  }

  /**
   * @param forClass type of objects provided by the registered provider.
   * @param provider will be bind to the selected class.
   * @throws InjectionException if provider is already defined.
   * @see bind
   * @see bindSingleton
   */
  fun setProvider(forClass: Class<*>, provider: () -> Any) {
    if (forClass in providers) throw InjectionException("Already defined provider for class: $forClass")
    providers[forClass] = provider
  }

  /**
   * @param type class of the provided components.
   * @return true if there is a provider registered for the selected type.
   */
  operator fun contains(type: Class<*>): Boolean = type in providers

  /**
   * @return true if there is a provider registered for the selected type.
   */
  inline fun <reified Type : Any> contains(): Boolean = Type::class.java in this

  /**
   * Allows to register new components in the context with builder-like DSL
   * @param init will be invoked on this context.
   * @return this context.
   * @see bind
   * @see bindSingleton
   */
  inline fun register(init: Context.() -> Unit): Context {
    this.init()
    return this
  }

  /**
   * @param provider will be bind with the selected type. If no type argument is passed, it will be bind to the same
   *    exact class as the object it provides.
   * @throws InjectionException if provider for the selected type is already defined.
   */
  inline fun <reified Type : Any> bind(noinline provider: () -> Type) = setProvider(Type::class.java, provider)

  /**
   * @param singleton will be converted to a provider that always returns the same instance. If no type argument is passed,
   *    it will be bind to its own class.
   * @throws InjectionException if provider for the selected type is already defined.
   */
  inline fun <reified Type : Any> bindSingleton(singleton: Type) = setProvider(Type::class.java, { singleton })

  /**
   * @param to list of interfaces and classes in the class hierarchy of the objects provided by the provider. Any time
   *    any of the passed classes will be requested for injection, the selected provider will be invoked.
   * @param provider provides instances of classes compatible with the passed types.
   * @throws InjectionException if provider for any of the selected types is already defined.
   */
  fun <Type : Any> bind(vararg to: Class<out Type>, provider: () -> Type) = to.forEach { setProvider(it, provider) }

  /**
   * @param singleton instance of class compatible with the passed types.
   * @param to list of interfaces and classes in the class hierarchy of the singleton. Any time any of the passed classes
   *    will be requested for injection, the selected singleton will be returned.
   * @throws InjectionException if provider for any of the selected types is already defined.
   */
  fun <Type : Any> bindSingleton(singleton: Type, vararg to: Class<out Type>) = bind(*to) { singleton }

  /**
   * Removes all user-defined providers and singletons from the context. [Context] itselfs will still be present and
   * injectable with [provider] and [inject].
   */
  fun clear() {
    providers.clear()
    bindSingleton(this)
  }
}

/**
 * Thrown in case of any problems with the dependency injection mechanism.
 */
class InjectionException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
