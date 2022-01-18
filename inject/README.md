[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-inject.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-inject)

# KTX: Dependency injection

A tiny, lightweight dependency injection system with simple syntax that does not rely on reflection.

### Why?

Games can be vastly simplified with dependency injection. There are a lot of components that need to work together.
Assets, physics renderers, input listeners, game logic services - as much as we would like to separate them from each
other, some interaction between them is inevitable. You can create manager classes, maintain huge constructors or
use a bunch of global variables, but that is often not the most portable, comfortable or modular solution. It is much
easier to inject a different instance depending on the current application setup than refactor sources with a ton of
static calls.

Java dependency injection mechanisms usually rely on annotations and compile-time code generation (like
[Dagger](http://google.github.io/dagger/)) or runtime class analysis (like [Spring](https://spring.io/)). Kotlin, with
its inline functions, allows to omit the reflection and annotations usage altogether, while still providing a pleasant
DSL.

It should be noted that there are other Kotlin DI libraries with more features. `ktx-inject` is a tiny extension
consisting of a single source file with a few hundred lines, most of which are the documentation. Being as lightweight
as possible and generating little to no garbage at runtime, it aims to be a viable choice for even the slowest mobile
devices out there. It sacrifices extra features for simplicity, quick startup and zero overhead at runtime when
the objects are constructed.

### Guide

`Context` stores components of your application and providers of classes that you need to create dynamically.

With `register` function, you can customize your `Context` with a syntax similar to the usual type-safe Kotlin builders.
`bind` and `bindSingleton` can be used to register new providers and components in the context.

`inject<Type>()` is a function that will extract an instance of the selected type from the context. It is typically used
when you need only a single instance directly from the `Context` upon application initiation.

`provider<Type>()` is a function that returns functional objects which provide instances of the selected type when
invoked. It should be used for all non-singleton components of the `Context` that you need to obtain dynamically.
Instead of passing `Context` around, inject appropriate providers to your components to avoid excessive dependency on
`ktx-inject` in your project.

`remove<Type>()` allows you to remove components from the `Context` that are no longer needed.

`clear()` and `dispose()` methods can be used after you no longer need the `Context`. `clear()` removes references to
all registered singletons and providers. `dispose()`, additionally to clearing the context, attempts to dispose all
singletons and providers that implement the `Disposable` interface and logs all errors on the libGDX error logging
level. Use `clear()` instead of `dispose()` if you want to fully control assets lifecycle.

Additional `bind<Type>()`, `bindSingleton<Type>()`, and `newInstanceOf<Type>()` methods allow constructing objects
with reflection taking the constructor parameters directly from the `Context`. These methods are annotated with
`@Reflection` from [`ktx-reflect`](../reflect) and require opt-in to prevent from accidental usage. Reflection usage
is entirely optional, although it does simplify registering components with multiple constructor parameters.
Note that the classes constructed via reflection must have a single public constructor, otherwise an exception
will be thrown.

### Usage examples

Creating a new `Context`:

```kotlin
val context = Context()
```

Registering a provider:
```kotlin
import ktx.inject.*
import java.util.Random

context.register {
  // A new Random instance will be injected on each request:
  bind { Random() }
}
```

Registering a singleton:
```kotlin
import ktx.inject.*
import java.util.Random

context.register {
  // The same Random instance will be injected on each request:
  bindSingleton(Random())
}
```

Registering a singleton via an init block:
```kotlin
import ktx.inject.*

context.register {
  // The same Component instance will be injected on each request:
  bindSingleton {
    if (isAndroid()) {
      AndroidComponent()
    } else {
      DesktopComponent()
    }
  }
}
```

Injecting an instance:
```kotlin
import java.util.Random

val random: Random = context.inject()
```

Injecting a provider:
```kotlin
import java.util.Random

val randomProvider = context.provider<Random>()
// Type of `randomProvider` variable is () -> Random.

// Getting an instance of Random using the provider:
val random = randomProvider()
```

Injection on demand (_lazy_ injection):
```kotlin
import ktx.inject.*

class ClassWithLazyInjectedValue(context: Context) {
  val lazyInjection by lazy { context.inject<Random>() }
  // Will provide Random instance on first lazyInjection call.
}
```

Removing a registered provider:
```kotlin
context.remove<Random>()
// Note that this method works for both singletons and providers.
```

Removing all components from the `Context`:
```kotlin
context.clear()
```

Removing all components from the `Context` and disposing of all `Disposable` singletons and providers:
```kotlin
context.dispose()
```

Using reflection to automatically create components with injected dependencies:
```kotlin
import ktx.inject.Context
import ktx.inject.register
import ktx.reflect.Reflection

class MyDependency
class MyClass(val myDependency: MyDependency)

// Note that reflection usage requires explicit opt-in:
@OptIn(Reflection::class)
fun create() {
  val context = Context()
  context.register {
    // Will construct a new instance of MyDependency with reflection
    // each time it is requested:
    bind<MyDependency>()

    // Will construct a single instance of MyClass with MyDependency
    // taken from Context:
    bindSingleton<MyClass>()
  }
}
```

#### Adding `this` to the `Context`

Be careful when adding a disposable context container class as `this` to the `Context`. Consider this example:

```kotlin
class Container: Disposable {
  val context = Context()

  init {
    context.bindSingleton(this)
  }

  override fun dispose() {
    context.dispose()
  }
}
```

As soon as `Container.dispose` is called, it will cause a `StackOverflowError`, as `Context` will try to dispose of the
`Container`, which will attempt to dispose of the `Context`, and so on. This can be a issue, if you keep a `Context`
in extensions of classes such as `KtxGame` or `KtxScreen` from [`ktx-app`](../app). To fix this problem, remove the
container class from the `Context` before attempting to dispose of it:

```diff
class Container: Disposable {
  val context = Context()

  init {
    context.bindSingleton(this)
  }

  override fun dispose() {
+   context.remove<Container>()
    context.dispose()
  }
}
```

This will ensure that the `Context` itself will not attempt to dispose of the `Container`.

Note that this also applies to extensions of `KtxApplicationAdapter` and `KtxGame`, both of which are `Disposable`.

### Notes on implementation and design choices

> How does it work?

You can think of the `Context` as a map with `Class<T>` instances as keys and `() -> T` (so-called providers)
as values. When you call a method like `inject<YourClass>()`, it is inlined at compile-time - hence allowing the
framework to extract the actual `Class` object from generic argument - and used to find a provider for `YourClass`.
Singletons are implemented as providers that always return the same instance of the selected type on each call. Other
than using `Class` instances at keys, it does not rely on reflection.

> Are scopes supported?

No. More complex projects might benefit from features of mature projects like [Koin](https://insert-koin.io/),
but in most simple games you just end up needing some glue between the components. Sometimes simplicity is something
you aim for.

As for testing scope, you can just register different components during testing. Classes using `ktx-inject` are usually
easy to test. If necessary, you can also use multiple contexts.

> Are named providers supported?

No. Providers are mapped only to the class of instances that they return.

> What about Kodein-style single-parameter factories?

It seems that Kodein keeps all its "providers" as single-parameter functions. To avoid wrapping all no-arg providers
(which seem to be the most common by far) in `null`-consuming functions, factories are not implemented in `ktx-inject`
at all. If you need specialized providers, create a simple class with `invoke` operator that consumes the necessary
parameters.

> Is this framework for me?

This dependency injection system is as trivial as it gets. It will help you with platform-specific classes and gluing
your application together, but don't expect much more. This library might be great if you're just starting with
dependency injection, as it does not introduce many complex concepts. The main advantages of `ktx-inject` include
simplicity, quick startup, and compatibility with all libGDX desktop and mobile backends.

If you end up needing more features, there are other Kotlin dependency injection frameworks, and they mostly work well
with libGDX.

### Alternatives

- [Koin](https://insert-koin.io/), [Kodein](https://github.com/Kodein-Framework/Kodein-DI) are powerful Kotlin
dependency injection frameworks that support multiple platforms. If you ever feel that `ktx-inject` is not enough
for your use case, try these out.
- [Dagger](http://google.github.io/dagger/) is a Java dependency injection library based on annotations and compile-time
code generation. It generates human-readable POJO classes, which makes it both easier to debug and more efficient that
the usual reflection-based solutions. However, it is harder to set up and Kotlin solutions usually offer better syntax.
- [Spring](https://spring.io/) is a powerful dependency injection framework with automatic component scan. It relies on
runtime class analysis with reflection, which generally makes it less efficient than Dagger or most Kotlin solutions.
Thanks to its huge ecosystem and useful extensions, it might be a good solution for complex desktop games. Otherwise, it
might be an overkill.
- [Guice](https://github.com/google/guice) is another commonly used reflection-based dependency injection mechanism.
- [Autumn](https://github.com/czyzby/gdx-lml/tree/master/autumn) is a multi-platform reflection-based dependency
injection library with automatic component scan for libGDX written in Java. It works even on GWT (although Kotlin does
not work well with GWT in the first place). Reflection overhead is generally small, but hacky Kotlin-based solutions are
obviously expected to be more efficient.

#### Additional documentation

- [Official reflection article.](https://libgdx.com/wiki/utils/reflection)
