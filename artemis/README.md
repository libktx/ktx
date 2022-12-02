[![Artemis-odb](https://img.shields.io/badge/artemis--odb-2.3.0-red.svg)](https://github.com/junkdog/artemis-odb)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-artemis.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-artemis)

# KTX: `Artemis-odb` ECS utilities

Utilities and type-safe builders for the [Artemis-odb](https://github.com/junkdog/artemis-odb) entity-component-system.

### Why?

[Artemis-odb](https://github.com/junkdog/artemis-odb) does not target Kotlin out of the box. Kotlin can provide
a highly readable DSL via inlined methods with reified generic types. Additionally, creating Entities and their
respective Components can result in a lot of declarative-style code which is greatly improved by an easily readable
type-safe builder DSL.

### Guide

`ktx-artemis` provides the following extensions and utilities:

- `ArchetypeBuilder.add` adds components to an ArchetypeBuilder.
- `ArchetypeBuilder.remove` removes component from an ArchetypeBuilder.
- `Aspect.Builder` has extension functions to use `KClass` instances: `oneOf`, `allOf`, `exclude`.
- `oneOf`, `allOf`, `exclude` are also available as standalone Aspect builder functions.
- `EntityEdit.plusAssign` adds components to an entity with a `+=` operator function.
- `EntityEdit.with` adds or replaces a component of an entity.
- `EntityEdit.remove` removes a component from the entity.
- `EntityTransmuterFactory.add` adds a component to an EntityTransmuterFactory.
- `EntityTransmuterFactory.remove` removes a component from an EntityTransmuterFactory.
- `ComponentMapper.contains` checks if an entity has a component with the `in` operator function.
- `World.edit` edits an entity by its id with a type-safe builder DSL.
- `World.entity` creates and adds an entity to the world with a type-safe builder DSL.
- `World.get` retrieves a system from the world with `get` (`[]` operator).
- `World.getSystem` retrieves a system from the world with a type-safe builder DSL.
- `World.mapperFor` retrieves a ComponentMapper instance.

### Usage examples

Creating a new `Entity`:

```kotlin
import com.artemis.Component
import com.artemis.World
import ktx.artemis.entity

val world = World()

class Texture : Component()
class Transform(var x: Float = 0f, var y: Float = 0f) : Component()

val entity = world.entity {
    with<Texture>()
    with<Transform> {
        x = 1f
        y = 1f
    }
}
```

Adding new components to an existing entity with `World.edit`:

```kotlin
import com.artemis.Component
import com.artemis.World
import ktx.artemis.edit
import ktx.artemis.entity
import ktx.artemis.with

val world = World()

class Transform(var x: Float = 0f, var y: Float = 0f) : Component()

val entityId = world.entity()

fun editWorld() {
  world.edit(entityId) {
    with<Transform> {
      x = 1f
      y = 1f
    }
  }
}
```

Getting a `BaseSystem` from the `World`:

```kotlin
import com.artemis.*
import ktx.artemis.*

class MoveSystem : BaseSystem() {
  override fun processSystem() = Unit
}
class RenderSystem : BaseSystem() {
  override fun processSystem() = Unit
}

val world = World(
  WorldConfigurationBuilder()
    .with(MoveSystem(), RenderSystem())
    .build()
)

fun getSystem() {
  // Non-nullable variant - throws an exception if the system is missing:
  val moveSystem = world.getSystem<MoveSystem>()
  // Nullable variant - returns null if the system is missing:
  val renderSystem = world[RenderSystem::class]
}
```

Creating a `ComponentMapper`:

```kotlin
import com.artemis.Component
import com.artemis.World
import ktx.artemis.mapperFor

class Transform : Component()

val world = World()
val transformMapper = world.mapperFor<Transform>()
```

Adding a `Component` to an existing `Entity`:

```kotlin
import com.artemis.Component
import com.artemis.World
import ktx.artemis.*

class Transform(var x: Float = 0f, var y: Float = 0f) : Component()

fun addComponentToEntity(entityId: Int, world: World) {
  // Creating and adding a component:
  val entityEdit = world.edit(entityId) {
    with<Transform> {
      x = 2.5f
      y = 5f
    }
  }

  // Or you can add a component with the += operator:
  entityEdit += Transform(x = 1f, y = 2f)
}
```

Removing a `Component` from an `Entity`:

```kotlin
import com.artemis.Component
import com.artemis.World
import ktx.artemis.*

class Transform : Component()

val world = World()
val entityId = world.entity {
  with<Transform>()
}

fun removeTransform() {
  world.edit(entityId).remove<Transform>()
}
```

Getting a `Component` from an `Entity` with a ComponentMapper:

```kotlin
import com.artemis.Component
import com.artemis.World
import ktx.artemis.*

class Transform : Component()

val world = World()
val transformMapper = world.mapperFor<Transform>()
val entityId = world.entity {
  with<Transform>()
}
val transformComponent: Transform = transformMapper[entityId]
```

Checking if an `Entity` has a `Component` with a mapper:

```kotlin
import com.artemis.Component
import com.artemis.World
import ktx.artemis.*

class Transform : Component()

val world = World()
val transformMapper = world.mapperFor<Transform>()
val entityId = world.entity {
  with<Transform>()
}
val hasTransform: Boolean = entityId in transformMapper
```

Creating a component `Aspect` that matches all entities with the selected `Component` types with an exclusion:

```kotlin
import com.artemis.*
import com.artemis.systems.IteratingSystem
import ktx.artemis.*

class Texture : Component()
class Transform : Component()
class RigidBody : Component()

val world = World()
val aspectBuilder = allOf(Texture::class, Transform::class).exclude(RigidBody::class)

// The AspectBuilder can be added to a system for example
class ExampleIteratingSystem : IteratingSystem(aspectBuilder) {
  override fun process(entityId: Int) = Unit
}
```

Adding and removing components from an `Archetype`:

```kotlin
import com.artemis.ArchetypeBuilder
import com.artemis.Component
import ktx.artemis.*

class Texture : Component()
class Transform : Component()
class RigidBody : Component()

fun createArchetype() {
  val archetypeBuilder = ArchetypeBuilder()
    .add(Transform::class, Texture::class)

  // You can also add components one by one with generic type:
  archetypeBuilder.add<RigidBody>()

  // You can just as easily remove components:
  archetypeBuilder.remove<RigidBody>()
}
```

Adding and removing a component from an `EntityTransmuterFactory`:

```kotlin
import com.artemis.Component
import com.artemis.EntityTransmuterFactory
import com.artemis.World
import ktx.artemis.*

val world = World()

class Texture : Component()
class Transform : Component()

val transmuterFactory = EntityTransmuterFactory(world)
  .add<Transform>()
  .remove<Texture>()
```

### Alternatives

- [Fleks](https://github.com/Quillraven/Fleks/) is a high performance Kotlin ECS library. Written with a Kotlin DSL
from day one, it does not require similar utilities to the Java ECS frameworks.
- [Ashley](https://github.com/libgdx/ashley) is a Java ECS library maintained the libGDX organization. KTX provides
utilities for Ashley via the [`ktx-ashley`](../ashley) module.

#### Additional documentation

- [Artemis-odb repository.](https://github.com/junkdog/artemis-odb)
- [A classic article on Entity Component Systems.](http://t-machine.org/index.php/2007/09/03/entity-systems-are-the-future-of-mmog-development-part-1/)
