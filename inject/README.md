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

Why not use an existing Kotlin DI library? `ktx-inject` is a tiny extension consisting a single source file with about
200 lines, most of which are the documentation. Being as lightweight as possible and generating little to no garbage at
runtime, it aims to be a viable choice for even the slowest devices out there. It sacrifices extra features for
simplicity and nearly zero overhead at runtime.

### Guide

`Context` stores providers of your classes. You can access a global `Context` instance thanks to
`ContextContainer.defaultContext`, but if you do not want to rely on statics - you can work directly with a custom
`Context` instance and its API.

`inject<Type>()` is a function that will extract an instance of the selected type from the context. By default, it will
use the static `Context` instance. 

`provider<Type>()` is a function that provides lambdas which return instances of the selected type when invoked. By
default, it will use the static `Context` instance. 

With `register` function, you can customize your `Context` with a syntax similar to the usual type-safe Kotlin builders.
`bind` and `bindSingleton` can be used to register new providers and singletons in the context.

### Usage examples

Registering a provider:
```Kotlin
import ktx.inject.*

register {
  bind { Random() } // Registered provider that returns new Random instance on each call.
}
```

Registering a singleton:
```Kotlin
import ktx.inject.*

register {
  bindSingleton(Random()) // Registered provider that returns same Random instance on each call.
}
```

Injecting an instance:
```Kotlin
import ktx.inject.*

val random = inject<Random>() // Type of `random` variable is Random.
```

Injecting a provider:
```Kotlin
import ktx.inject.*

val randomProvider = provider<Random>() // Type of `randomProvider` variable is () -> Random.

// Getting an instance of Random using a provider:
val random = randomProvider()
```

Injection on demand (_lazy_ injection):
```Kotlin
import ktx.inject.*

class ClassWithLazyInjectedValue {
  val lazyInjection by lazy { inject<Random>() }
}
```

Accessing static `Context` instance:
```Kotlin
import ktx.inject.*

val context = ContextContainer.defaultContext
context.clear()
```

### Implementation notes

> How does it work?

You can think of the `Context` as a simple, huge map with `Class<T>` instances as keys and `() -> T` (so-called providers)
as values. When you call a method like `inject<YourClass>()`, it is inlined at compile-time - hence allowing the
framework to extract the actual `Class` object from generic argument - and used to find a provider for `YourClass`.
Singletons (and their friends) are implemented as providers that always return the same instance of the selected type.
It _is_ dead simple and aims to introduce as little runtime overhead as possible.

> No scopes? Huh?

How often do you need these in simple games, anyway? Agreed: more complex projects might benefit from features of mature
projects like [Kodein](https://github.com/SalomonBrys/Kodein), but in most simple games you just end up needing some
glue between the components. Sometimes simplicity is something you aim for.

As for testing scope, it should be obvious that you can just register different components during testing. Don't worry,
classes using `ktx-inject` are usually trivial to test.

> Not even any named providers?

Nope. Providers are mapped to the class of instances that they return and that's it. Criteria systems - which are a
sensible alternative to simple string names - are somewhat easy to use when your system is based on annotations, but we
don't have much to work with when the goal is simplicity.

If desperately want to use string as IDs of components, create your custom container class with overloaded `get` or
`invoke` operator - it could be worse than `inject<Sprites>()["player"]`, if you think about it. I've certainly _seen_
worse!

> Kodein-style single-parameter factories, anyone?

It seems that Kodein keeps all its "providers" as single-parameter functions. To avoid wrapping all no-arg providers
(which seem to be the most common by far) in `null`-consuming functions, factories are not implemented in `ktx-inject`
at all. Honestly, it's hard to get it right - single-parameter factories might not be enough in many situations and
type-safe multi-argument factories might look _really_ awkward_ in code thanks to a ton of generics. If you need
specialized providers, just create a simple class with `invoke` operator.

> What's with the statics?

A dependency injection framework that does not solve the problem of passing the context (or its equivalent) around might
seem pretty ironic. A reflection-based DI might rely on automatic component scan to do the job, but in case of a "pure
Kotlin" solution, we don't really want to end up with every class having a `Context` instance in its constructor. While
not the prettiest approach out there, `val myInstance = inject<MyClass>()` is as simple as it gets. If you don't want
to rely on the statics at all, `Context` does have a pleasant API that you can work with directly. Create a new instance
and you're good to go.

> Is this framework for me?

This dependency injection system is as trivial as it gets. It will help you with platform-specific classes and gluing
your application together, but don't expect wonders. This library might be great if you're just starting with dependency
injection - all you need to learn is using a few simple functions. It's also hard to imagine a more lightweight
solution: getting a provider is a single call to a single call to a map.

If you never end up needing more features, you might consider sticking with `ktx-inject` altogether, but just so you
know - there _are_ other Kotlin dependency injection and they work _great_ with LibGDX. There was no point in creating
another _complex_ dependency injection framework, and we were fully aware of that. Simplicity and little-to-none runtime
overhead - this pretty much sums up the strong sides of `ktx-inject`.

### Alternatives

- [Kodein](https://github.com/SalomonBrys/Kodein) is a powerful, yet simple dependency injection framework written in
Kotlin. It also detects circular dependencies and is able to pretty-print pretty much anything. While it would require
additional benchmarking, this library *might be* slightly less efficient due to how it stores its data - **KTX** should
keep less meta-data at runtime and create less objects overall, limiting garbage collection. This library shares pretty
similar syntax with **KTX**, although it lacks "static" access to the context - unless you add some utility methods
manually, you have to pass around the `Kodein` instance. If you ever feel the need for a more complex DI system, this is
the way to go.
- [Injekt](https://github.com/kohesive/injekt) is another dependency injection library written in Kotlin. It seems that
the developer moved on to the *Kodein* project, although if you prefer *Injekt* API, it still seems viable to use.
- [Dagger](http://google.github.io/dagger/) is a Java dependency injection library based on annotations and compile-time
code generation. It generates human-readable POJO classes, which makes it both easier to debug and more efficient that
the usual reflection-based solutions. However, it is harder to set up and Kotlin solutions usually offer better syntax.
To be honest, field injection with annotations works great in Java, but can be quite annoying in Kotlin with its
`lateinit`, `?` and whatnot - actually, annotation-based dependency injection syntax
[can be *less* verbose in Java than in Kotlin](https://stackoverflow.com/questions/37388357/which-is-the-preferred-syntax-when-using-annotation-based-dependency-injection-i).
- [Spring](https://spring.io/) is a powerful dependency injection framework with automatic component scan. It relies on
runtime class analysis with reflection, which generally makes it less efficient than Dagger or most Kotlin solutions.
Thanks to its huge ecosystem and useful extensions, it might be a good solution for complex desktop games. Otherwise it
might be an overkill.
- [Guice](https://github.com/google/guice) is another commonly used reflection-based dependency injection mechanism.
- [Autumn](https://github.com/czyzby/gdx-lml/tree/master/autumn) is a multi-platform reflection-based dependency
injection library with automatic component scan for LibGDX written in Java. It works even on GWT (although Kotlin does
not work well with GWT in the first place). Reflection overhead is generally small, but hacky Kotlin-based solutions are
obviously expected to be more efficient.

