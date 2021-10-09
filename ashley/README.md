[![Ashley](https://img.shields.io/badge/ashley-1.7.4-red.svg)](https://github.com/libgdx/ashley)
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
- `Engine.getSystem` and `Engine.get` (`[]` operator) ease access to registered `EntitySystem` instances of the engine.
`Engine.getSystem` throws a `MissingEntitySystemException` in case the system is not part of the `Engine`.
`Engine.get` returns `null` in such cases.
- `EngineEntity` is an `Entity` wrapper that allows creating `Component` instances using the `Engine` via
`with` method. It is available when calling `Engine.entity` or `Engine.configureEntity`.
- `Engine.configureEntity` extension method allows adding components to an existing entity.
- `mapperFor` factory method allows creating `ComponentMapper` instances.
- `Mapper` abstract class can be extended by `companion object`s of `Component` to obtain `ComponentMapper` instances.
- Accessors for `Entity` objects using `ComponentMappers`: `get` (`[]` operator), `has`, `hasNot`,
`contains` (`in` operator), `remove`.
- `Entity.addComponent` extension method allows creating and adding a single `Component` to an existing `Entity`.
- `Entity.plusAssign` (`+=`) operator allows to add a `Component` to an existing `Entity`.
- Top-level and `Builder` extension DSL methods for constructing `Family` builders with `KClass` instances: `oneOf`,
`allOf`, `exclude`.

### Usage examples

Creating a new pooled `Entity`:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

val engine = PooledEngine()

class Texture: Component
class Transform(var x: Float = 0f, var y: Float = 0f): Component

val entity = engine.entity {
  with<Texture>()
  with<Transform> {
    x = 1f
    y = 1f
  }
}
```

Creating multiple new entities:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

val engine = PooledEngine()

class Transform(var x: Float = 0f, var y: Float = 0f): Component

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

Adding new components to an existing entity with `Engine.configureEntity`:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

val engine = PooledEngine()
val entity = Entity()

class Transform(var x: Float = 0f, var y: Float = 0f): Component

fun extendEntity(){
  engine.configureEntity(entity) {
    with<Transform> {
      x = 1f
      y = 1f
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

val engine = PooledEngine()

fun getSystem() {
    engine.addSystem(MoveSystem())
    engine.addSystem(RenderSystem())

    // Non-nullable variant - throws an exception if the system is missing:
    val moveSystem = engine.getSystem<MoveSystem>()
    // Nullable variant - returns null if the system is missing:
    val renderSystem = engine[RenderSystem::class]
}
```

Creating a `ComponentMapper`:

```kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class Transform: Component

val transformMapper = mapperFor<Transform>()
```

Adding a `Component` to an existing `Entity`:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import ktx.ashley.addComponent
import ktx.ashley.plusAssign

class Transform(var x: Float = 0f, var y: Float = 0f): Component

fun addComponentToEntity(entity: Entity, engine: Engine) {
  // Creating and adding a component:
  entity.addComponent<Transform>(engine) {
    x = 2.5f
    y = 5f
  }

  // Or alternatively, if you already have a constructed component:
  entity += Transform(x = 1f, y = 2f)
}
```

Getting a `Component` from an `Entity`:

```kotlin
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

```kotlin
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

```kotlin
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

```kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.*

class Texture: Component
class Transform: Component
class RigidBody: Component

var family = allOf(Texture::class, Transform::class).exclude(RigidBody::class)
```

Using a custom `companion object` to store a `ComponentMapper`:

```kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.*

// Instead of storing a reference to the ComponentMapper
// as a top-level variable, you can use a companion object
// to tie the mapper with a specific component class:
class Transform: Component {
  companion object {
    val ID = mapperFor<A>()
  }
}

// The mapper is now available statically under `ID`:
val myMapper = Transform.ID
```

Using a `companion object` extending `Mapper` to obtain a `ComponentMapper`:

```kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.*

class Transform: Component {
  companion object: Mapper<Transform>()
}

// The mapper is now available statically under `mapper`:
val mapper = Transform.mapper
```

Note that to extend `Mapper`, the object _must_ be nested inside the corresponding `Component` class.
Both of these examples will result in runtime errors:

```kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.*

// Do not do this!

// Type mismatch:
class A: Component
class B: Component {
  // Wrong class - <A> instead of <B>!
  companion object : Mapper<A>()
}

// Not nested inside the class:
class C: Component
// Will throw an exception!
object CMapper: Mapper<C>()
```

#### Additional documentation

- [Ashley repository.](https://github.com/libgdx/ashley)
- [A classic article on Entity Component Systems.](http://t-machine.org/index.php/2007/09/03/entity-systems-are-the-future-of-mmog-development-part-1/)
