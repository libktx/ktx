# KTX: collection utilities

Utilities and extension function for custom LibGDX collections.

### Why?

For better and for worse, LibGDX collections do not implement interfaces or extend any abstract classes from the legendary
`java.util` package. Although standard Kotlin library features superb utilities, some of them be used along with the
LibGDX collections simply because they do not implement the `Collection` interface. Not to mention LibGDX collections
could use some extensions and factory methods, as well as fully benefit from the syntax sugar (like square brackets
operators) that Kotlin comes with.

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
- `getLast` and `removeLast` utility extension methods were added.
- Get-or-else extension method was added and is available through `array[index, alternative]` syntax.
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

- `ObjectMap` instances can be constructed with `gdxMapOf` methods, similarly to how you create `java.util` maps in Kotlin.
- `IdentityMap` instances can be constructed with `gdxIdentityMapOf` methods. Added some basic support for LibGDX
`IdentityMap` and optimized primitive `IntIntMap`, `IntFloatMap` and `IntMap` collections.
- Null-safe `isEmpty()`, `isNotEmpty()` and `size()` methods where added. They allow you to inspect the collection even
if the variable is a possible null.
- `in` operator can be used to check if a particular key is stored in the map.
- Square bracket syntax can be used to add new elements to the maps: `map[key] = value` is an equivalent to
`map.put(key, value)`.
- `iterate` method allows to iterate over map elements with a reference to `MutableIterator`. Can be used to easily
remove elements from the map.
- Keys stored in the map can be quickly converted to an `ObjectSet` using `toGdxSet` method.
- Every iterable and array can be converted to `ObjectMap` using `toGdxMap` method. A lambda that converts values to keys
has to be provided - since the method is inline, no new lambda object will be created, though.d

#### Lists

LibGDX features `PooledLinkedList` class, which is an equivalent to `LinkedList` - but since it does not even implement
the `Iterable` interface (making it rather unpleasant to use), a custom `PooledList` was included. It caches its nodes
and iterators, limiting garbage collection. This collection should be used for storage of objects that are often
iterated over, and their efficient removal and insertion during iteration is necessary. It can be created with
`gdxListOf` method. Every `Array` and `Iterable` can be converted to `PooledList` with `toGdxList` utility method.

### Alternatives

- Kotlin standard library provides utilities for default Java collections, although you should be aware that `java.util`
collections can cause garbage collection issues on slower devices.
- [LibGDX Kiwi utilities](https://github.com/czyzby/gdx-lml/tree/master/kiwi) contains a module which helps with LibGDX
collections - but since it is written with Java, `ktx-collections` is arguably easier to use in Kotlin applications. It
is still worth looking into for its so-called lazy, disposable and immutable collections.

