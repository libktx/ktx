# KTX: assets management

Utilities for management of assets and heavy resources.

### Why?

LibGDX does a good job of helping you with assets (through `AssetManager`, for example), but - as usual in case of Java
APIs - it does not use the full potential of Kotlin features. This library aims to provide Kotlin extensions and wrappers
for the existing API to make assets usage more natural in Kotlin applications.

### Guide

#### `Disposable`

- Null-safe `Disposable.disposeSafely()` method was added. Can be called on a nullable `Disposable?` variable. Ignores
most thrown exceptions (except for internal JVM `Error` instances, which should not be caught anyway).
- `Disposable.dispose` with an exception catch block was added. Using `asset.dispose { exception -> doSomething() }`
syntax, you can omit a rather verbose try-catch block and handle exceptions with a Kotlin lambda.
- Any `Iterable` or `Array` storing `Disposable` instances will have `dispose`, `dispose { exception -> }` and
`disposeSafely` methods that dispose stored assets ignoring any `null` elements. This is a utility for disposing
collections of assets en masse.
- All exceptions get a utility `ignore()` method that you can switch at compile time (for debugging or logging) when
needed. See `Throwable.ignore()` documentation for further details.

#### `Pool`

- `Pool` instances can be invoked like a function to provide new instances of objects. Basically, this syntax: `pool()`
has the same effect as directly calling `pool.obtain()`.
- `Pool` instances can be invoked like a one argument function to free instances of objects. Basically, this syntax:
`pool(instance)` has the same effect as directly calling `pool.free(instance)`.
- New instances of `Pool` can be easily created with Kotlin lambda syntax using `pool` method. For example, this pool
would return new instances of `Entity` once empty: `pool { Entity() }`. Since this method is inlined, you should not
worry about unnecessary extra method calls or extra objects - the `Pool` implementations are prepared at compile time.

### Alternatives

- [libgdx-utils](https://bitbucket.org/dermetfan/libgdx-utils/) feature an annotation-based asset manager implementation
which easies loading of assets (through internal reflection usage).
- [Autumn MVC](https://github.com/czyzby/gdx-lml/tree/master/mvc) is a [Spring](https://spring.io/)-inspired
model-view-controller framework built on top of LibGDX. It features its own asset management module which loads and
injects assets into annotated fields thanks to reflection.
- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) library has some utilities for assets handling, like
graceful `Disposable` destruction methods and LibGDX collections implementing `Disposable` interface. It is aimed at
Java applications, though - **KTX** syntax feels more natural when using Kotlin.

