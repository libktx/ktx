# KTX: math utilities

Math extensions and operator overloads for LibGDX math-related API.

### Why?

Java does not feature operator overloading, which leads to weird constructs like `vector.add(a).sub(b)`. However, when
using Kotlin a much more readable syntax like `vector + a - b` is possible to achieve - but then again, LibGDX method
names do not match the required Kotlin operator identifiers. Extension operator functions are necessary to make it work.

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
call `vec in rect` (or `vec !in rect`) to check if the rectangle (not) contains the point stored by the vector.

### Alternatives

Sadly, it does not seem that there are any math-related Kotlin libraries that would make LibGDX math API usage more
natural. You can try browsing through existing general purpose LibGDX utility extensions - some of them contain static
methods for various math operations missing in the official API.

