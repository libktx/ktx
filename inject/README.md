[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-inject.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-inject)

# KTX: dependency injection

A tiny, lightweight dependency injection system with simple syntax and no reflection usage.

### Why?

Games *beg* for dependency injection. There are a lot of components from different worlds that need to work together.
Assets, physics renderers, input listeners, game logic services - as much as we would like to separate them from each
other, some interaction between them is inevitable. You can create `XxxManager` classes, maintain huge constructors or
use a bunch of global variables, but that is not exactly the most portable, comfortable or modular solution. After all,
you want your code to be readable and reusable, right? It is much easier to inject a different instance to class
depending on the current application setup than refactor sources with a ton of static calls.

Java dependency injection mechanisms usually rely on annotations and compile-time code generation (like
[Dagger](http://google.github.io/dagger/)) or runtime class analysis (like [Spring](https://spring.io/)). Kotlin, with
its inline functions, allows to omit the reflection and annotations usage altogether, while still providing a pleasant
DSL.

Why not use an existing Kotlin DI library? `ktx-inject` is a tiny extension consisting of a single source file with a few
hundred lines, most of which are the documentation. Being as lightweight as possible and generating little to no garbage
at runtime, it aims to be a viable choice for even the slowest devices out there. It sacrifices extra features for
simplicity and nearly zero overhead at runtime.

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
singletons and providers that implement the `Disposable` interface and logs all errors on the LibGDX error logging
level. Use `clear()` instead of `dispose()` if you want to fully control assets lifecycle.

### Usage examples

Creating a new `Context`:

```Kotlin
val context = Context()
```

Registering a provider:
```Kotlin
import ktx.inject.*
import java.util.Random

context.register {
  // A new Random instance will be injected on each request:
  bind { Random() }
}
```

Registering a singleton:
```Kotlin
import ktx.inject.*
import java.util.Random

context.register {
  // The same Random instance will be injected on each request:
  bindSingleton(Random())
}
```

Registering a singleton via an init block:
```Kotlin
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
```Kotlin
import java.util.Random

val random: Random = context.inject()
```

Injecting a provider:
```Kotlin
import java.util.Random

val randomProvider = context.provider<Random>()
// Type of `randomProvider` variable is () -> Random.

// Getting an instance of Random using the provider:
val random = randomProvider()
```

Injection on demand (_lazy_ injection):
```Kotlin
import ktx.inject.*

class ClassWithLazyInjectedValue(context: Context) {
  val lazyInjection by lazy { context.inject<Random>() }
  // Will provide Random instance on first lazyInjection call.
}
```

Removing a registered provider:
```Kotlin
context.remove<Random>()
// Note that this method work for both singletons and providers.
```

Removing all components from the `Context`:
```Kotlin
context.clear()
```

Removing all components from the `Context` and disposing of all `Disposable` singletons and providers:
```Kotlin
context.dispose()
```

### Notes on implementation and design choices

> How does it work?

You can think of the `Context` as a simple, huge map with `Class<T>` instances as keys and `() -> T` (so-called providers)
as values. When you call a method like `inject<YourClass>()`, it is inlined at compile-time - hence allowing the
framework to extract the actual `Class` object from generic argument - and used to find a provider for `YourClass`.
Singletons are implemented as providers that always return the same instance of the selected type on each call. It _is_
dead simple and aims to introduce as little runtime overhead as possible.

> No scopes? Huh?

How often do you need these in simple games, anyway? More complex projects might benefit from features of mature
projects like [Koin](https://insert-koin.io/), but in most simple games you just end up needing some glue between
the components. Sometimes simplicity is something you aim for.

As for testing scope, it should be obvious that you can just register different components during testing. Don't worry,
classes using `ktx-inject` are usually trivial to test.

> Not even any named providers?

Nope. Providers are mapped to the class of instances that they return - and that's it. Criteria systems - which are a
sensible alternative to simple string names - are somewhat easy to use when your system is based on annotations, but we
don't have much to work with when the goal is simplicity.

> Kodein-style single-parameter factories, anyone?

It seems that Kodein keeps all its "providers" as single-parameter functions. To avoid wrapping all no-arg providers
(which seem to be the most common by far) in `null`-consuming functions, factories are not implemented in `ktx-inject`
at all. Honestly, it's hard to get it right - single-parameter factories might not be enough in many situations and
type-safe multi-argument factories might look _really_ awkward_ in code thanks to a ton of generics. If you need
specialized providers, just create a simple class with `invoke` operator.

> Is this framework for me?

This dependency injection system is as trivial as it gets. It will help you with platform-specific classes and gluing
your application together, but don't expect wonders. This library might be great if you're just starting with dependency
injection - all you need to learn is using a few simple functions. It's also hard to imagine a more lightweight
solution: getting a provider is a single call to a map.

If you never end up needing more features, you might consider sticking with `ktx-inject` altogether, but just so you
know - there _are_ other Kotlin dependency injection and they work _great_ with LibGDX. There was no point in creating
another _complex_ dependency injection framework, and we were fully aware of that. Simplicity and little-to-none runtime
overhead - this pretty much sums up the strong sides of `ktx-inject`.

### Alternatives

- [Koin](https://insert-koin.io/), [Kodein](https://github.com/Kodein-Framework/Kodein-DI) are powerful Kotlin
dependency injection frameworks that support multiple platforms. If you ever feel that `ktx-inject` is not enough
for your use case, try these out.
- [Dagger](http://google.github.io/dagger/) is a Java dependency injection library based on annotations and compile-time
code generation. It generates human-readable POJO classes, which makes it both easier to debug and more efficient that
the usual reflection-based solutions. However, it is harder to set up and Kotlin solutions usually offer better syntax.
- [Spring](https://spring.io/) is a powerful dependency injection framework with automatic component scan. It relies on
runtime class analysis with reflection, which generally makes it less efficient than Dagger or most Kotlin solutions.
Thanks to its huge ecosystem and useful extensions, it might be a good solution for complex desktop games. Otherwise it
might be an overkill.
- [Guice](https://github.com/google/guice) is another commonly used reflection-based dependency injection mechanism.
- [Autumn](https://github.com/czyzby/gdx-lml/tree/master/autumn) is a multi-platform reflection-based dependency
injection library with automatic component scan for LibGDX written in Java. It works even on GWT (although Kotlin does
not work well with GWT in the first place). Reflection overhead is generally small, but hacky Kotlin-based solutions are
obviously expected to be more efficient.
