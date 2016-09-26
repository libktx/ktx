# KTX: math utilities

Math extensions and operator overloads for LibGDX math API.

### Why?

Java does not feature operator overloading, which leads to weird constructs like `vector.add(a).sub(b)`. Kotlin brings
the possibility to use a much more readable and natural syntax with its operators overloading: `vector + a - b`. However,
LibGDX API does not match Kotlin naming conventions (necessary for operators to work), which means extension functions
are necessary to make it work like that.

### Guide

#### `Vector2`

- `vec2` is a global factory function that can create `Vector2` instances with named parameters for extra readability.
- `+`, `-`, `*` and `/` can be used to add, subtract, multiply or divide current values according to the second vector.
- Unary `-` operator (a single minus before the vector) allows to negate both vector values.
- `*` and `/` can be used with floats and ints to multiply or divide both vector values.
- `++` and `--` operators can be used to increment and decrement both x and y values of the vector. Note that since
`Vector2` class is mutable, both these operations modify the internal state of the vector. This means that both `++vector`
and `vector++` are effectively the same, as previous state of the vector is not kept (to limit the amount of constructed
objects).
- `Vector2` instances can be destructed to two float variables in one step with `val (x, y) = vector2` syntax thanks to
`component1()` and `component2()` operator methods.
- `Vector2` instances are now comparable - `<`, `>`, `<=`, `>=` operators can be used to determine which vector has greater
(or equal) overall length. While this certainly does not fit all use cases, we consider it the most commonly compared
value. It can be used to determine which vector is further from a certain common point (for example, which *Box2D* `Body`
is further from the center of the `World` or which touch event is further from the `Viewport` center). It can be also
used to quickly determine which velocity or force is greater. Note that length *squared* is actually compared, as it is
much faster to calculate and yields the same results in most cases.

Note that since `Shape2D` has `contains(Vector2)` method, `in` operator can be used for any `Shape2D` implementation
(like `Rectangle`, `Ellipse` or `Circle`). For example, given `vec: Vector2` and `rect: Rectangle` variables, you can
call `vec in rect` (or `vec !in rect`) to check if the rectangle contains (or doesn't) the point stored by the vector.

#### `Vector3`

- `vec3` is a global factory function that can create `Vector3` instances with named parameters for extra readability.
- `+`, `-`, `*` and `/` can be used to add, subtract, multiply or divide current values according to the second vector.
- Unary `-` operator (a single minus before the vector) allows to negate all vector values.
- `*` and `/` can be used with floats and ints to multiply or divide all vector values.
- `++` and `--` operators can be used to increment and decrement x, y and z values of the vector. Note that since
`Vector3` class is mutable, both these operations modify the internal state of the vector. This means that both `++vector`
and `vector++` are effectively the same, as previous state of the vector is not kept (to limit the amount of constructed
objects).
- `Vector3` instances can be destructed to tree float variables in one step with `val (x, y, z) = vector3` syntax thanks
to `component1()`, `component2()` and `component3` operator methods.
- `Vector3` instances are now comparable - `<`, `>`, `<=`, `>=` operators can be used to determine which vector has greater
(or equal) overall length, similarly to how `Vector2` now works.

#### `Matrix3`

- `mat3` is a human-readable global factory function that allows to easily create `Matrix3` instances.
- Unary `-` operator (a single minus before the matrix) can be used to negate all matrix values.
- `!` operator before the matrix inverts it, calling `inv()`.
- `+` and `-` can be used to add and subtract values from other matrices.
- `*` operator can be used to right-multiply the matrix with another matrix using `mul(Matrix3)` method.
- `*` operator can be used to scale the X and Y components of the matrix with a float of a vector using `scl` methods.
- `Vector3` and `Vector2` instances can be multiplied with a `Matrix3` using `*` operator.
- `Matrix3` instances can be destructed into nine float variables (each representing one of its cells) thanks to the
`component1()` - `component9()` operator functions.

#### `Matrix4`

- `mat4` is a human-readable global factory function that allows to easily create `Matrix4` instances.
- Unary `-` operator (a single minus before the matrix) can be used to negate all matrix values.
- `!` operator before the matrix inverts it, calling `inv()`.
- `+` and `-` can be used to add and subtract values from other matrices.
- `*` operator can be used to right-multiply the matrix with another matrix using `mul(Matrix4)` method.
- `*` operator can be used to scale the X and Y components of the matrix with a float of a vector using `scl` methods.
- `Vector3` instances can be multiplied with a `Matrix4` using `*` operator.
- `Matrix4` instances can be destructed into sixteen float variables (each representing one of its cells) thanks to the
`component1()` - `component16()` operator functions.

### Alternatives

Sadly, it does not seem that there are any public math-related Kotlin libraries that would make LibGDX math API usage more
natural. You can try browsing through existing general purpose LibGDX extensions - some of them contain static methods
for various math operations missing in the official API.

#### Additional documentation

- [LibGDX math utilities article.](https://github.com/libgdx/libgdx/wiki/Math-utilities)
- [`Vectors` and `Matrices` article.](https://github.com/libgdx/libgdx/wiki/Vectors%2C-matrices%2C-quaternions)

