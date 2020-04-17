[![Ashley](https://img.shields.io/badge/ashley-1.7.3-red.svg)](https://libgdx.badlogicgames.com/)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-ashley.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-ashley)

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
- `Engine.getSystem` and `Engine.get` (`[]` operator) to access an `EntitySystem` of the engine.
`Engine.getSystem` throws a `MissingEntitySystemException` in case the system is not part of the `Engine`.
`Engine.get` returns null in such cases.
- `EngineEntity` is an `Entity` wrapper that allows to create `Component` instances using using the `Engine` via
`with` methods.
- `mapperFor` factory method allows to create `ComponentMapper` instances.
- Accessors for `Entity` objects using `ComponentMappers`: `get` (`[]` operator), `has`, `hasNot`,
`contains` (`in` operator), `remove`.
- `Entity.addComponent` extension to create and add a `Component` to an existing entity.
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

Getting an `EntitySystem` from an `Engine`:

```kotlin
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.get
import ktx.ashley.getSystem

class MoveSystem : EntitySystem()
class RenderSystem : EntitySystem()
class DamageSystem : EntitySystem()

val engine = PooledEngine()

fun getSystem() {
    engine.addSystem(MoveSystem())
    engine.addSystem(RenderSystem())

    val moveSystem = engine.getSystem<MoveSystem>()
    val renderSystem = engine[RenderSystem::class]
    // the next line throws a MissingEntitySystemException
    engine.getSystem<DamageSystem>()

    val damageSystem = engine[DamageSystem::class]
    println(damageSystem) // prints null
}
```

Creating a `ComponentMapper`:

```Kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class Transform: Component

val transformMapper = mapperFor<Transform>()
```

Adding a `Component` to an `Entity`:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.add
import ktx.ashley.entity

class Remove(var delay: Float = 0f) : Component

val engine = PooledEngine()
val entity = engine.entity {}

fun addComponent() {
    entity.addComponent<Remove>(engine) {
        delay = 2.5f
    }
}
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
val hasTransform: Boolean = entity.has(transform)
// Or alternatively:
val containsTransform: Boolean = transform in entity
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
