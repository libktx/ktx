[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-box2d.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-box2d)

# KTX: `Box2D` physics engine utilities

Utilities and type-safe builders for the default LibGDX 2D physics engine: *Box2D*.

### Why?

*Box2D* API, being a direct port from C++, can be difficult to work with - especially for beginners. Bodies and fixtures
construction code readability could certainly be improved. Kotlin type-safe builing DSL can help with that.

### Guide

`ktx-box2d` provides some extensions and utilities that aim improve *Box2D* API:

- `createWorld` factory method eases the construction of `World` instances.
- `World.body` and `Body` extension methods provide `Fixture` type-safe building DSL that supports customizing fixtures
with the following shapes:
  - `box`:`PolygonShape` as a box.
  - `circle`: `CircleShape`.
  - `polygon`: `PolygonShape`.
  - `chain`: `ChainShape`.
  - `loop`: looped `ChainShape`.
  - `edge`: looped `EdgeShape`.
  - `fixture`: a custom `Shape` passed as parameter. Note that in contrary to other fixture factory methods,
  the passed custom `Shape` will _not_ be disposed of unless the `disposeOfShape` parameter is set to `true`.
- `FixtureDef.filter` extension methods aim to simplify `Filter` API usage.
- `Body` was extended with the following builder methods that ease creation of `Joint` instances:
  - `gearJointWith`: `GearJoint`.
  - `ropeJointWith`: `RopeJoint`.
  - `weldJointWith`: `WeldJoint`.
  - `motorJointWith`: `MotorJoint`.
  - `mouseJointWith`: `MouseJoint`.
  - `wheelJointWith`: `WheelJoint`.
  - `pulleyJointWith`: `PulleyJoint`.
  - `distanceJointWith`: `DistanceJoint`.
  - `frictionJointWith`: `FrictionJoint`.
  - `revoluteJointWith`: `RevoluteJoint`.
  - `prismaticJointWith`: `PrismaticJoint`.
  - `jointWith`: any `Joint` type supported by the custom `JointDef` passed as the method argument.
- `earthGravity` is a constant that roughly matches Earth's gravity.
- `World.rayCast` extension methods allow creating ray-cast callbacks with Kotlin lambda syntax.
- `World.query` extension method allow creating AABB querying callbacks with Kotlin lambda syntax.

### Usage examples

Creating a new Box2D `World` without gravity:

```Kotlin
import ktx.box2d.createWorld

val world = createWorld()
```

Creating a new Box2D `World` with a custom gravity:

```Kotlin
import ktx.box2d.createWorld
import ktx.box2d.earthGravity
import com.badlogic.gdx.math.Vector2

val world = createWorld(gravity = Vector2(0f, -10f))

val earth = createWorld(gravity = earthGravity)
```

Creating a `Body` with a `PolygonShape` box `Fixture`:

```Kotlin
import ktx.box2d.body


// Building body from scratch:
val body = world.body {
  box(width = 2f, height = 1f) {
    density = 40f
  }
}

// Adding box polygon fixture to an existing body:
val fixture = body.box(width = 2f, height = 1f) {
  density = 40f
}
```

![PolygonShape](img/box.png)


Creating a `Body` with a customized `PolygonShape` `Fixture`:

```Kotlin
import ktx.box2d.body
import com.badlogic.gdx.math.Vector2

// Building body from scratch:
val body = world.body {
  polygon(Vector2(-1f, -1f), Vector2(0f, 1f), Vector2(1f, -1f)) {
    density = 40f
  }
}

// Adding polygon fixture to an existing body:
val fixture = body.polygon(Vector2(-1f, -1f), Vector2(0f, 1f), Vector2(1f, -1f)) {
  density = 40f
}
```

![PolygonShape](img/polygon.png)

Creating a `Body` with a `CircleShape` `Fixture`:

```Kotlin
import ktx.box2d.body

// Building body from scratch:
val body = world.body {
  circle(radius = 1f) {
    restitution = 0.5f
  }
}

// Adding circle fixture to an existing body:
val fixture = body.circle(radius = 1f) {
  restitution = 0.5f
}
```

![CircleShape](img/circle.png)

Creating a `Body` with a `ChainShape` `Fixture`:

```Kotlin
import ktx.box2d.body
import com.badlogic.gdx.math.Vector2

// Building body from scratch:
val body = world.body {
  chain(Vector2(-1f, -1f), Vector2(-1f, 1f), Vector2(1f, -1f), Vector2(1f, 1f)) {
    friction = 0.5f
  }
}

// Adding chain fixture to an existing body:
val fixture = body.chain(Vector2(-1f, -1f), Vector2(-1f, 1f), Vector2(1f, -1f), Vector2(1f, 1f)) {
  friction = 0.5f
}
```

![ChainShape](img/chain.png)

Creating a `Body` with a looped `ChainShape` `Fixture`:

```Kotlin
import ktx.box2d.body
import com.badlogic.gdx.math.Vector2

// Building body from scratch:
val body = world.body {
  loop(Vector2(-1f, -1f), Vector2(-1f, 1f), Vector2(1f, -1f), Vector2(1f, 1f)) {
    friction = 0.5f
  }
}

// Adding looped chain fixture to an existing body:
val fixture = body.loop(Vector2(-1f, -1f), Vector2(-1f, 1f), Vector2(1f, -1f), Vector2(1f, 1f)) {
  friction = 0.5f
}
```

![ChainShape](img/loop.png)

Creating a `Body` with an `EdgeShape` `Fixture`:

```Kotlin
import ktx.box2d.body
import com.badlogic.gdx.math.Vector2

// Building body from scratch:
val body = world.body {
  edge(from = Vector2(-1f, -1f), to = Vector2(1f, 1f)) {
    restitution = 1f
  }
}

// Adding edge fixture to an existing body:
val fixture = body.edge(from = Vector2(-1f, -1f), to = Vector2(1f, 1f)) {
  restitution = 1f
}
```

![EdgeShape](img/edge.png)

Creating a dynamic `Body` with multiple `Fixture` instances:

```Kotlin
import ktx.box2d.*
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType.DynamicBody

val body = world.body {
  type = DynamicBody
  circle(radius = 1f) {
    restitution = 0.5f
    filter {
      categoryBits = 0x01
      maskBits = 0x02
    }
  }
  box(width = 2f, height = 2f) {
    density = 40f
  }
  edge(from = Vector2(-1f, 1f), to = Vector2(1f, 0.5f)) {
    friction = 0.7f
  }
}
```

![CircleShape + PolygonShape + EdgeShape](img/multiple.png)

Customizing `Shape` of a `Fixture`:

```Kotlin
import ktx.box2d.*

val body = world.body {
  // Shapes are available as the default `it` parameter of fixture
  // building blocks. Of course, they can be renamed - in this
  // example we renamed the CircleShape parameter to `shape`:
  circle { shape ->
    shape.radius = 0.5f
    shape.position = Vector2(0.5f, 0.5f)
  }
}
```

![CircleShape](img/circle-custom.png)

Creating two `Body` instances joined with a `DistanceJoint`:

```Kotlin
import ktx.box2d.*

val bodyA = world.body {
  position.set(-1f, 0f)
  box(width = 0.5f, height = 0.5f) {}
}
val bodyB = world.body {
  position.set(1f, 0f)
  box(width = 0.5f, height = 0.5f) {}
}
// Extension methods for all other joint types are also available.
val joint = bodyA.distanceJointWith(bodyB) {
  length = 2f
}
```

![PolygonShape + DistanceJoint](img/joint.png)

Adding callbacks invoked after creation of `Body` and `Fixture` instances:

```Kotlin
import ktx.box2d.*

val body = world.body {
  onCreate { body ->
    // Will be called when the body and all of its fixtures are built.
  }

  circle {
    onCreate { fixture ->
      // Will be called when this particular fixture is created. Note
      // that at invocation time the Box2D body of this fixture might
      // not be fully constructed yet.
    }
  }
}
```

Creating ray-casts:

```Kotlin
import ktx.box2d.*

fun createRayCast() {
  world.rayCast(startX = 0f, startY = 0f, endX = 1f, endY = 1f) { fixture, point, normal, fraction ->
    // Will be called when this ray hits a fixture.
    RayCast.CONTINUE
  }
}
```

Querying the world for fixtures overlapping an AABB:

```Kotlin
import ktx.box2d.*

fun createQuery() {
  world.query(lowerX = 0f, lowerY = 0f, upperX = 1f, upperY = 1f) { fixture ->
    // Will be called when this query overlaps a fixture.
    Query.CONTINUE
  }
}
```

#### Synergy

Pair this library with [`ktx-math`](../math) for `Vector2` factory methods, operator overloads and other math-related
utilities.

### Alternatives

- [gdx-box2d-kotlin](https://github.com/Jkly/gdx-box2d-kotlin) is the first published Kotlin *Box2D* utilities library
that originally inspired `ktx-box2d`.
- [libgdx-utils-box2d](https://bitbucket.org/dermetfan/libgdx-utils) is a set of *Box2D* utilities written in Java.

#### Additional documentation

- [LibGDX *Box2D* article.](https://github.com/libgdx/libgdx/wiki/Box2d)
- [Official *Box2D* website.](http://box2d.org/)
