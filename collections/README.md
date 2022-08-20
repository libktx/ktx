[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-collections.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-collections)

# KTX: Collection utilities

Utilities and extension function for custom libGDX collections.

### Why?

Unfortunately, libGDX collections do not implement interfaces or extend any abstract classes from the `java.util`
package. Although standard Kotlin library features superb utilities, many of them cannot be used along with the libGDX
collections because they do not implement the Java `Collection` interface. Not to mention libGDX collections
could use some extensions and factory methods, as well as fully benefit from the syntax sugar (like the square bracket
operator) that Kotlin comes with.

### Guide

#### Arrays

libGDX features `Array` class (with unfortunate naming), which works similarly to `ArrayList`, but it reuses its
iterators. Its name clashes with `kotlin.Array`, so it is advised to use the `GdxArray` type alias provided by **KTX**,
or alternatively import it with `import com.badlogic.gdx.utils.Array as GdxArray` when necessary.

- `Array` instances can be constructed with `gdxArrayOf` methods, similarly to how you create native arrays in Kotlin.
- Null-safe `isEmpty()`, `isNotEmpty()` and `size()` methods where added. They allow you to inspect the collection even
if the variable is a possible null.
- Null-safe inlined extension property `lastIndex` was added. It returns index of last element in the list - or `-1` if
the list is null or empty.
- `+` and `-` operators were overridden: they allow adding or removing elements from the collection. They can be invoked
with a compatible element type or another collection storing values of the same type. Both operator invocations can be
chained. These operators return a new collection with specified added or removed elements.
- `+=` and `-=` operators were overridden: they allow adding or removing elements from an existing collection, modifying
its content.
- `getLast` and `removeLast` utility extension methods were added.
- Utility `sortDescending`, `sortBy` and `sortByDescending` extension methods were added to ease list sorting.
- Get-or-else extension method was added and is available through `array[index, alternative]` syntax.
- Missing `addAll` and `removeAll` methods for arrays and iterables were added.
- `iterate` method allows iterating over collection's elements, while providing reference to `MutableInterator`. Can be
used to easily remove collection elements during iteration.
- `removeAll` and `retainAll` higher-order functions that work like collection extensions in Kotlin stdlib. A `Pool` can
optionally be passed to automatically free the removed items.
- `transfer` extension method can be used to move elements from one array to another using a lambda predicate.
- `map`, `filter`, `flatten` and `flatMap` methods that work like methods in Kotlin stdlib but return `GdxArray`.
- Every iterable and array can be converted to `Array` using `toGdxArray` method.
- `IntArray`, `BooleanArray` and `FloatArray` can be converted to corresponding libGDX primitive collections using
`toGdxArray` method.
- Type aliases added for libGDX collections to avoid collisions with the standard library:
  - `GdxArray`: `com.badlogic.gdx.utils.Array`
  - `GdxIntArray`: `com.badlogic.gdx.utils.IntArray`
  - `GdxFloatArray`: `com.badlogic.gdx.utils.FloatArray`
  - `GdxBooleanArray`: `com.badlogic.gdx.utils.BooleanArray`
  - `GdxCharArray`: `com.badlogic.gdx.utils.CharArray`
  - `GdxLongArray`: `com.badlogic.gdx.utils.LongArray`
  - `GdxShortArray`: `com.badlogic.gdx.utils.ShortArray`
  - `GdxByteArray`: `com.badlogic.gdx.utils.ByteArray`
- Factory methods for libGDX arrays storing primitives:
  - `gdxBooleanArrayOf`
  - `gdxByteArrayOf`
  - `gdxCharArrayOf`
  - `gdxShortArrayOf`
  - `gdxIntArrayOf`
  - `gdxLongArrayOf`
  - `gdxFloatArrayOf`

#### Sets

libGDX features `ObjectSet` class, which works similarly to `HashSet`, but it reuses its iterators.

- `ObjectSet` instances can be constructed with `gdxSetOf` methods, similarly to how you create native arrays in Kotlin.
- Null-safe `isEmpty()`, `isNotEmpty()` and `size()` methods where added. They allow you to inspect the collection even
if the variable is a possible null.
- `+` and `-` operators were overridden: they allow adding or removing elements from the collection. They can be invoked
with a compatible element type or another collection storing values of the same type. Both operator invocations can be
chained. These operators return a new collection with specified added or removed elements.
- `+=` and `-=` operators were overridden: they allow adding or removing elements from an existing collection, modifying
its content.
- Missing `addAll` and `removeAll` methods for arrays and iterables were added.
- `iterate` method allows iterating over collection's elements, while providing reference to `MutableIterator`. Can be
used to easily remove collection elements during iteration.
- `map`, `filter`, `flatten` and `flatMap` methods that work like methods in Kotlin stdlib but return `GdxSet`.
- Every iterable and array can be converted to `ObjectSet` using `toGdxSet` method.
- `IntArray` can be converted to `IntSet` using `toGdxSet` method.
- Type alias added for consistency with other collections: `GdxSet` - `com.badlogic.gdx.utils.ObjectSet`.

#### Maps

libGDX features `ObjectMap` class, which works similarly to `HashMap`, but it reuses its iterators.

- `ObjectMap` instances can be constructed with `gdxMapOf` methods, similarly to how you create `java.util` maps in Kotlin.
- `IdentityMap` instances can be constructed with `gdxIdentityMapOf` methods.
- Basic support for optimized primitive `IntIntMap`, `IntFloatMap` and `IntMap` collections.
- Null-safe `isEmpty()`, `isNotEmpty()` and `size()` methods where added. They allow you to inspect the collection even
if the variable is a possible null.
- `in` operator can be used to check if a particular key is stored in the map.
- Square bracket syntax can be used to add new elements to the maps: `map[key] = value` is an equivalent to
`map.put(key, value)`.
- `iterate` method allows iterating over map elements with a reference to `MutableIterator`. Can be used to easily
remove elements from the map.
- `map`, `filter`, `flatten` and `flatMap` methods that work like methods in Kotlin stdlib but return `GdxMap` and `GdxArray`.
- Keys stored in the map can be quickly converted to an `ObjectSet` using `toGdxSet` method.
- Every iterable and array can be converted to `ObjectMap` using `toGdxMap` method. A lambda that converts values to
keys has to be provided - since the method is inlined, no new lambda object will be created at runtime.
- Type aliases were added for consistency with other collections:
  - `GdxMap`: `com.badlogic.gdx.utils.ObjectMap`
  - `GdxIdentityMap`: `com.badlogic.gdx.utils.IdentityMap`
  - `GdxArrayMap`: `com.badlogic.gdx.utils.ArrayMap`
- All libGDX map entries now feature `component1()` and `component2()` operator extension methods, so they can be
destructed into a key and a value.
- `getOrPut` for `ObjectMap`, `IdentityMap`, `ArrayMap` and `IntMap` method to get an existing value to a given key
or if it does not exist, create a default value, add it to the map and return it.

#### Note

It is highly advised to use `ktx.collections.*` import when working with libGDX collections. Kotlin standard library
comes with multiple unoptimized utility methods for `Iterable` instances - like `-` operator that iterates over the
whole collection to remove an element, which would be highly inefficient in case of `ObjectSet`, for example. By using
a wildcard import of all **KTX** utilities, you can make sure that you're using the correct extension method
implementations.

IntelliJ allows marking packages for automatic wildcard import at `Settings > Editor > Code Style > Kotlin > Imports`.

### Usage examples

Working with libGDX `Array`:
```kotlin
import ktx.collections.*

val array = gdxArrayOf("zero", "one", "two")
array[0] // "zero"
"one" in array // true
array += "three" // array[3] == "three"; array.size == 4
array -= "three" // "three" in array == false; array.size == 3
array += arrayOf("three", "four") // array[3] == "three", array[4] = "four"

val empty = gdxArrayOf<String>()

val arrayOfPrimitives = gdxIntArrayOf(1, 2, 3)
```

Working with libGDX `ObjectSet`:
```kotlin
import ktx.collections.*

val set = gdxSetOf("zero", "one", "two")
"one" in set // true
set += "three" // "three" in set == true; set.size = 4
set -= "three" // "three" in set == false; set.size == 3
set += arrayOf("three", "four") // "three" in set == true; "four" in set == true

val empty = gdxSetOf<String>()
```

Working with libGDX `ObjectMap`:
```kotlin
import ktx.collections.*

val map = gdxMapOf(0 to "zero", 1 to "one", 2 to "two")
0 in map // true
map[0] // "zero"
map[3] = "three" // 3 in map == true; map[3] == "three"

val empty = gdxMapOf<Int, String>()
```

Iterating over libGDX maps with destructing syntax:
```kotlin
import ktx.collections.*

val map = gdxMapOf(0 to "zero", 1 to "one", 2 to "two")
map.forEach { (key, value) ->
  println("$value was mapped to $key.")
}
```

### Alternatives

- Kotlin standard library provides utilities for default Java collections, although you should be aware that `java.util`
collections can cause garbage collection issues on slower devices.
- [Kiwi](https://github.com/czyzby/gdx-lml/tree/master/kiwi) contains a module which helps with libGDX collections -
but since it is written with Java, `ktx-collections` is arguably easier to use in Kotlin applications. It
is still worth looking into for its *lazy*, *disposable* and *immutable* collections.
- [Koloboke](https://github.com/leventov/Koloboke) contains efficient implementations of sets and maps that can use
unboxed primitive types as keys or values. Its API design and implementation is most likely significantly better and
more efficient than libGDX collections, it is also a major dependency due to the sheer amount of available collections
and its code generators. Note that Koloboke collections are compatible with `java.util` collections API, while libGDX
collections are not - Koloboke maps and sets can fully benefit from Kotlin standard library utilities.

#### Additional documentation

- [Official libGDX collections article.](https://libgdx.com/wiki/utils/collections)
