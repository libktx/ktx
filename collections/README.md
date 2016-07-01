# KTX: collection utilities

Utilities and extension function for custom LibGDX collections.

### Why?

For better and for worse, LibGDX collections do not implement interfaces or extend any abstract classes from the legendary
`java.util` package. Although standard Kotlin library features superb utilities, some of them be used along with the
LibGDX collections simply because they do not implement the `Collection` interface. Not to mention LibGDX collections
could use some extensions and factory methods.

### Guide

#### Arrays

LibGDX features unfortunate `Array` class, which works similarly to `ArrayList`, but it reuses its iterators. Its name
clashes with `kotlin.Array`, so it is advised to import it with `import com.badlogic.gdx.utils.Array as GdxArray` when
necessary.

- `Array` instances can be constructed with `gdxArrayOf` methods, similarly to how you create native arrays in Kotlin.
- Null-safe `isEmpty()`, `isNotEmpty()` and `size()` methods where added. They allow you to inspect the collection even
if the variable is a possible null.
- `+` and `-` operators were overridden: they allow to add and remove elements from the collection. They can be invoked
with a compatible element type or another collection storing values of the same type. Both operator invocations can be
chained.
- `getLast`, `removeLast` and `getOrElse` utility extension methods were added.
- Missing `addAll` and `removeAll` methods for arrays and iterables were added.
- `iterate` method allows to iterate over collection's elements, while providing reference to `MutableInterator`. Can be
used to easily remove collection elements during iteration.
- Every iterable and array can be converted to `Array` using `toGdxArray` method.
- `IntArray`, `BooleanArray` and `FloatArray` can be converted to corresponding LibGDX primitive collections using
`toGdxArray` method.

#### Sets

LibGDX features `ObjectSet` class, which works similarly to `HashSet`, but it reuses its iterators.

- `ObjectSet` instances can be constructed with `gdxSetOf` methods, similarly to how you create native arrays in Kotlin.
- Null-safe `isEmpty()`, `isNotEmpty()` and `size()` methods where added. They allow you to inspect the collection even
if the variable is a possible null.
- `+` and `-` operators were overridden: they allow to add and remove elements from the collection. They can be invoked
with a compatible element type or another collection storing values of the same type. Both operator invocations can be
chained.
- Missing `addAll` and `removeAll` methods for arrays and iterables were added.
- `iterate` method allows to iterate over collection's elements, while providing reference to `MutableInterator`. Can be
used to easily remove collection elements during iteration.
- Every iterable and array can be converted to `ObjectSet` using `toGdxSet` method.
- `IntArray` can be converted to `IntSet` using `toGdxSet` method.

#### Maps

LibGDX features `ObjectMap` class, which works similarly to `HashMap`, but it reuses its iterators.

- Every iterable and array can be converted to `ObjectMap` using `toGdxMap` method. A lambda that converts values to keys
has to be provided - since the method is inline, no new lambda object will be created, though.

#### Lists

LibGDX features `PooledLinkedList` class, which is an equivalent to `LinkedList` - but since it does not even implement
the `Iterable` interface (making it rather unpleasant to use), a custom `PooledList` was included. It can be created with
`gdxListOf` method. It caches its nodes and iterators, limiting garbage collection. `PooledList` was based on the
implementation from [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) library.

### Alternatives

- Kotlin standard library provides utilities for default Java collections, although you should be aware that `java.util`
collections can cause garbage collection issues on slower devices.
- [LibGDX Kiwi utilities](https://github.com/czyzby/gdx-lml/tree/master/kiwi) contains a module which helps with LibGDX
collections - but since it is written with Java, `ktx-collections` is arguably easier to use in Kotlin applications. It
is still worth looking into for its so-called lazy, disposable and immutable collections.
