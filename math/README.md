[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-math.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-math)

# KTX: Math utilities

Math extensions and operator overloads for libGDX math API and Kotlin ranges.

### Why?

Java does not feature operator overloading, which leads to weird constructs like `vector.add(a).sub(b)`. Kotlin brings
the possibility to use a much more readable and natural syntax with its operators overloading: `vector + a - b`. However,
libGDX API does not match Kotlin naming conventions (necessary for operators to work), which means extension functions
are necessary to make it work like that.

Kotlin also provides convenient syntax for ranges, which can be used for clearly describing criteria for selecting random
numbers.

### Guide

#### `Vector2`

- `vec2` is a global factory function that can create `Vector2` instances with named parameters for extra readability.
- `+=`, `-=`, `*=` and `/=` can be used to add, subtract, multiply (`scl`) or divide current values according to the second
vector or number. Use these operators to _mutate_ existing vectors.
- `+`, `-`, `*` and `/` can be used to add, subtract, multiply (`scl`) or divide vectors according to the second vector or
number, resulting in a new vector. Use these operators to _create_ new instances of vectors.
- Unary `-` operator (a single minus before the vector) allows to negate both vector values, creating a new vector.
- `++` and `--` operators can be used to increment and decrement both x and y values of the vector, resulting in a new
vector. To avoid creating new vectors, prefer `+= 1` and `-= 1` instead.
- `Vector2` instances can be destructed to two float variables in one step with `val (x, y) = vector2` syntax thanks to
`component1()` and `component2()` operator methods.
- `Vector2` instances are now comparable - `<`, `>`, `<=`, `>=` operators can be used to determine which vector has greater
(or equal) overall length. While this certainly does not fit all use cases, we consider it the most commonly compared
value. It can be used to determine which vector is further from a certain common point (for example, which *Box2D* `Body`
is further from the center of the `World` or which touch event is further from the `Viewport` center). It can be also
used to quickly determine which velocity or force is greater. Note that length *squared* is actually compared, as it is
much faster to calculate and yields the same results in most cases.
- `dot` infix function allows to calculate the dot product of 2 vectors.
- `x` infix function allows to calculate the cross product of 2 vectors.

Note that since `Shape2D` has `contains(Vector2)` method, `in` operator can be used for any `Shape2D` implementation
(like `Rectangle`, `Ellipse` or `Circle`). For example, given `vec: Vector2` and `rect: Rectangle` variables, you can
call `vec in rect` (or `vec !in rect`) to check if the rectangle contains (or doesn't) the point stored by the vector.

#### `ImmutableVector2`

- `ImmutableVector2` is an immutable equivalent to `Vector2`. It provides most of the functionality of `Vector2`, but
mutation methods return new vectors instead of mutating the objects.
- Note that one may want to create type aliases to make the usage more concise: `typealias Vect2 = ImmutableVector2`.
- `ImmutableVector` is comparable (`>`, `>=`, `<`, `<=` are available). Comparison is evaluated by length.
- Instances can be destructed: `val (x, y) = vector2`.
- `Vector2.toImmutable()` Returns an immutable vector with same `x` and `y` attributes than this `Vector2`
- `ImmutableVector2.toVector2()` Returns a mutable vector with same `x` and `y` attributes than this `ImmutableVector2`
- Most of the functions of `Vector2` which mutate the vector are provided but deprecated. This allows smooth migration from
`Vector2`.
- Notable differences with `Vector2`:
  - `+`, `-`, `*`, `/` are available and replace `add`, `sub` and `scl`.
  - `withLength()` and `withLength2()` replace `setLength()` and `setLength2()` and return a new vector of same direction
  with the specified length.
  - `withRandomRotation` replace `setToRandomRotation` and return a new vector of same length and a random rotation.
  - `withAngleDeg()` and `withAngleRad` replace `setAngle` and `setAngleRad` and return a new vector of same length and
  the given angle to x-axis.
  - `cpy` is deprecated and is not necessary. Immutable vectors can be safely shared. However, since `ImmutableVector` is
  a `data class`, there is a `copy(x, y)` method available allowing to easily create new vectors based on existing ones.
  - `set(x, y)` and `setZero()` are not provided.   
  - Functions dealing with angles in degree are suffixed with `Deg` and all returns values between `-180` and `+180`.
  - All angle functions return the angle toward positive y-axis.
  - `dot` is an infix function.
  - `x` and `crs` infix functions replace `crs` (cross product).

##### Usage examples

Obtaining `ImmutableVector2` instances:

```kotlin
import ktx.math.*

val v0 = ImmutableVector2.ZERO // pre-defined vector
val v1 = ImmutableVector2(1f, 2f) // arbitrary vector
val v2 = ImmutableVector2.X.withRotationDeg(30f) // unit vector of given angle
val v3 = -ImmutableVector2.X // inverse of a vector
```

Converting from libGDX `Vector2` to `ImmutableVector2` (and vice versa):

```kotlin
import ktx.math.*
import com.badlogic.gdx.math.Vector2

val mutable1: Vector2 = Vector2()
val immutable: ImmutableVector2 = mutable1.toImmutable()
val mutable2: Vector2 = immutable.toMutable()
```

Working with immutable vectors:

```kotlin
import ktx.math.*

var vector1 = ImmutableVector2.X

// Reassignment of variables is only possible with `var`.
// Note that the original vector instance is not modified.
vector1 += ImmutableVector2.Y
vector1 *= 3f

val vector2 = vector1.withClamp(0f, 1f) * 5f // `vector1` is not modified.
```

Creating convenience type alias to ease the use of immutable vectors:

```kotlin
import ktx.math.*

// If you don't want to use the rather verbose ImmutableVector2,
// you can declare a more convenient typealias.
typealias Vec2 = ImmutableVector2

var v1 = (Vec2.X + Vec2.Y).nor
var v2 = Vec2(1f, 2f).withLength(3f)
```

#### `Vector3`

- `vec3` is a global factory function that can create `Vector3` instances with named parameters for extra readability.
It is also overloaded with a second variant that allows to convert `Vector2` instances to `Vector3`.
- `+=`, `-=`, `*=` and `/=` can be used to add, subtract, multiply (`scl`) or divide current values according to the second
vector or number. Use these operators to _mutate_ existing vectors.
- `+`, `-`, `*` and `/` can be used to add, subtract, multiply (`scl`) or divide vectors according to the second vector or
number, resulting in a new vector. Use these operators to _create_ new instances of vectors.
- Unary `-` operator (a single minus before the vector) allows to negate both vector values, creating a new vector.
- `++` and `--` operators can be used to increment and decrement x, y and z values of the vector, resulting in a new
vector. To avoid creating new vectors, prefer `+= 1` and `-= 1` instead.
- `Vector3` instances can be destructed to three float variables in one step with `val (x, y, z) = vector3` syntax thanks
to `component1()`, `component2()` and `component3` operator methods.
- `Vector3` instances are now comparable - `<`, `>`, `<=`, `>=` operators can be used to determine which vector has greater
(or equal) overall length, similarly to how `Vector2` now works.
- `dot` infix function allows to calculate the dot product of 2 vectors.
- `x` infix function allows to calculate the cross product of 2 vectors.

#### `Vector4`

- `vec4` is a global factory function that can create `Vector4` instances with named parameters for extra readability.
  It is also overloaded with a second variant that allows to convert `Vector2` and `Vector3` instances to `Vector4`.
- `+=`, `-=`, `*=` and `/=` can be used to add, subtract, multiply (`scl`) or divide current values according to the second
  vector or number. Use these operators to _mutate_ existing vectors.
- `+`, `-`, `*` and `/` can be used to add, subtract, multiply (`scl`) or divide vectors according to the second vector or
  number, resulting in a new vector. Use these operators to _create_ new instances of vectors.
- Unary `-` operator (a single minus before the vector) allows to negate both vector values, creating a new vector.
- `++` and `--` operators can be used to increment and decrement x, y, z, and w values of the vector, resulting in a new
  vector. To avoid creating new vectors, prefer `+= 1` and `-= 1` instead.
- `Vector4` instances can be destructed to four float variables in one step with `val (x, y, z, w) = vector4` syntax thanks
  to `component1()`, `component2()` and `component3` operator methods.
- `Vector3` instances are now comparable - `<`, `>`, `<=`, `>=` operators can be used to determine which vector has greater
  (or equal) overall length.
- `dot` infix function allows to calculate the dot product of 2 vectors.

#### `Matrix3`

- `mat3` is a human-readable global factory function that allows to easily create `Matrix3` instances.
- Unary `-` operator (a single minus before the matrix) can be used to negate all matrix values.
- `!` operator before the matrix inverts it, calling `inv()`.
- `+=` and `-=` can be used to add and subtract values from second matrix. Use these operators to _mutate_
existing matrix instances.
- `+` and `-` can be used to add and subtract values from other matrices. Use these operators to _create_
new instances of matrices.
- `*` operator can be used to right-multiply the matrix with another matrix using `mul(Matrix3)` method.
- `*` operator can be used to scale the X and Y components of the matrix with a float of a vector using `scl` methods.
- `Matrix3` instances can be multiplied with a `Vector2` using `*` operator.
- `Matrix3` instances can be destructed into nine float variables (each representing one of its cells) thanks to the
`component1()` - `component9()` operator functions.

#### `Matrix4`

- `mat4` is a human-readable global factory function that allows to easily create `Matrix4` instances.
- Unary `-` operator (a single minus before the matrix) can be used to negate all matrix values.
- `!` operator before the matrix inverts it, calling `inv()`.
- `+=` and `-=` can be used to add and subtract values from second matrix. Use these operators to _mutate_
existing matrix instances.
- `+` and `-` can be used to add and subtract values from other matrices. Use these operators to _create_
new instances of matrices.
- `*` operator can be used to right-multiply the matrix with another matrix using `mul(Matrix4)` method.
- `*` operator can be used to scale the X and Y components of the matrix with a float of a vector using `scl` methods.
- `Matrix4` instances can be multiplied with a `Vector3` using `*` operator.
- `Matrix4` instances can be destructed into sixteen float variables (each representing one of its cells) thanks to the
`component1()` - `component16()` operator functions.

#### Ranges

- The `amid` infix function for Int and Float allows easy creation of a range by using a center and a tolerance. Such a
definition is a convenient way to think about a range from which random values will be selected.
- The four arithmetic operators are available for easily shifting or scaling ranges. This allows intuitive modification
of ranges in code, which can be useful for code clarity when defining a range for random number selection, or for
rapidly iterating a design.
- `IntRange.random(random: java.util.Random)` allows using a Java Random to select a number from the range, and is
provided in case there is a need to use the `MathUtils.random` instance or an instance of libGDX's fast RandomXS128.
- `ClosedRange<Float>.random()` allows a evenly distributed random number to be selected from a range (but treating
the `endInclusive` as exclusive for simplicity).
- `ClosedRange<Float>.randomGaussian()` selects a normally distributed value to be selected from the range, scaled so the
range is six standard deviations wide.
- `ClosedRange<Float>.randomTriangular()` allow easy selection of a triangularly distributed number from the range. A
a `normalizedMode` can be passed for asymmetrical distributions.
- `ClosedRange<Float>.lerp(Float)` linearly interpolates between the ends of a range.
- `ClosedRange<Float>.interpolate(Float, Interpolation)` interpolates between the ends of a range using an
`Interpolation` instance.

##### Usage examples

Suppose there is a class that has a random behavior. It can be constructed by passing several ranges to its constructor.

```kotlin
class CreatureSpawner(val spawnIntervalRange: ClosedRange<Float>) {
  //...
  fun update(dt: Float){
    untilNext -= dt
    while (untilNext <= 0){
      untilNext += spawnIntervalRange.random()
      spawnSomething()
    }
  }
}
```

In a parent class, there are many of these instances set up. The ranges can be described intuitively:

```kotlin
val spawners = listOf(
  //...
  CreatureSpawner(0.5f amid 0.2f),
  //...
)
```

And as the design is iterated, the range can be adjusted quickly and intuitively by applying arithmetic operations:

```kotlin
val spawners = listOf(
  //...
  CreatureSpawner((0.5f amid 0.2f) * 1.2f + 0.1f),
  //...
)
```

#### `Shape2D` extensions

- `Rectangle` and `Ellipse` instances can be destructed to four float variables in one step with
  `val (x, y, w, h) = rectangle/ellipse` syntax thanks to `component1()`, `component2()`, `component3()` and `component4()` operator methods.
- `Circle` instances can be destructed to three float variables in one step with
  `val (x, y, radius) = circle` syntax thanks to `component1()`, `component2()` and `component3()` operator methods.
- `Polygon` and `Polyline` instances can be destructed to two float variables in one step with
  `val (x, y) = polygon/polyline` syntax thanks to `component1()` and `component2()` operator methods.

### Alternatives

You can use libGDX APIs directly or rely on third-party math libraries:

- [Kotlin Statistics](https://github.com/thomasnield/kotlin-statistics) contains idiomatic Kotlin wrappers over
[Apache Commons Math](http://commons.apache.org/proper/commons-math/userguide/stat.html). Its extension functions might
prove useful during game development.
- [Jvm Glm](https://github.com/kotlin-graphics/glm) is a Kotlin port of the [glm](https://github.com/g-truc/glm)
lib by `g-truc`.

#### Additional documentation

- [Official libGDX math utilities article.](https://libgdx.com/wiki/math-utils/math-utilities)
- [`Vectors` and `Matrices` article.](https://libgdx.com/wiki/math-utils/vectors-matrices-quaternions)
