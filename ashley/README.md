# KTX: `Ashley` entity component system utilities

Utilities and type-safe builders for the [Ashley](https://github.com/libgdx/ashley) entity component system.

### Why?

Since [Ashley](https://github.com/libgdx/ashley) contains many generic methods consuming `Class` instances, Kotlin can
provide a pleasant DSL via inlined methods reified generic types. Additionally, creating `Entities` and their respective
`Components` can result in a lot of declarative-style code which is greatly improved by an easily readable type-safe
builder DSL. 
 
### Guide

`ktx-ashley` provides the following extensions and utilities:

- `Engine.add` and `Engine.entity` extension methods provide type-safe building DSL for creating `Entities`.
- `EngineEntity` is an `Entity` wrapper that allows to create `Component` instances using using the `Engine` via
`with` methods.
- `mapperFor` factory method allows to create `ComponentMapper` instances.
- Accessors for `Entity` objects using `ComponentMappers`: `get`, `has`, `hasNot`, `remove`.
- Top-level and `Builder` extension DSL methods for constructing `Family` builders with `KClass` instances: `oneOf`,
`allOf`, `exclude`.

### Usage examples

Creating a new pooled `Entity`:

```Kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

val engine = PooledEngine()

class Texture: Component
class Transform(var x:Float = 0f, var y:Float = 0f): Component

val entity = engine.entity {
  with<Texture>()
  with<Transform> {
    x = 1f
    y = 1f
  }
}
```

Creating multiple new entities:

```Kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

val engine = PooledEngine()

class Transform(var x:Float = 0f, var y:Float = 0f): Component

fun setupEngine() = engine.add {
  entity {
    with<Transform> {
      x = 1f
      y = 1f
    }
  }
  entity {
    with<Transform> {
      x = 2f
      y = 2f
    }
  }
}
```

Creating a `ComponentMapper`:

```Kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class Transform: Component

val transformMapper = mapperFor<Transform>()
```

Getting a `Component` from an `Entity`:

```Kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

class Transform: Component

val engine = PooledEngine()
val transform = mapperFor<Transform>()
val entity = engine.entity {
  with<Transform>()
}
val component: Transform = entity[transform]
```

Checking if an `Entity` has a `Component`:

```Kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

class Transform: Component

val engine = PooledEngine()
val transform = mapperFor<Transform>()
val entity = engine.entity {
  with<Transform>()
}
val canBeTransformed: Boolean = entity.has(transform)
```

Removing a `Component` from an `Entity`:

```Kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

class Transform: Component

val engine = PooledEngine()
val entity = engine.entity {
  with<Transform>()
}

fun removeTransform() {
  entity.remove<Transform>()
}
```

Creating a component `Family` that matches all entities with the selected `Component` types with an exclusion:

```Kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.*

class Texture: Component
class Transform: Component
class RigidBody: Component

var family = allOf(Texture::class, Transform::class).exclude(RigidBody::class)
```

#### Additional documentation

- [Ashley repository.](https://github.com/libgdx/ashley)
- [A classic article on Entity Component Systems.](http://t-machine.org/index.php/2007/09/03/entity-systems-are-the-future-of-mmog-development-part-1/)
