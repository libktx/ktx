[![Ashley](https://img.shields.io/badge/ashley-1.7.4-red.svg)](https://github.com/libgdx/ashley)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-ashley.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-ashley)

# KTX: `Ashley` ECS utilities

Utilities and type-safe builders for the [Ashley](https://github.com/libgdx/ashley) entity-component-system.

### Why?

Since [Ashley](https://github.com/libgdx/ashley) contains many generic methods consuming `Class` instances, Kotlin can
provide a pleasant DSL via inlined methods with reified generic types. Additionally, creating `Entities` and their respective
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
- Accessors for `Entity` objects using `ComponentMappers`: `get` (`[]` operator), `has`, `hasNot`,
`contains` (`in` operator), `remove`.
- `Entity.addComponent` extension method allows creating and adding a single `Component` to an existing `Entity`.
- `Entity.plusAssign` (`+=`) operator allows to add a `Component` to an existing `Entity`.
- Top-level and `Builder` extension DSL methods for constructing `Family` builders with `KClass` instances: `oneOf`,
`allOf`, `exclude`.
- `EntityAdditionListener` is an interface extending `EntityListener` which provides improved typing, as well as empty
implementations of event listening methods except for `entityAdded`.
- `EntityRemovalListener` is an interface extending `EntityListener` which provides improved typing, as well as empty
implementations of event listening methods except for `entityRemoved` event.
- `Engine.onEntityAdded` and `Engine.onEntityRemoved` extension methods make it possible to attach an entity listener
created from a passed with a lambda and add them to the `Engine` immediately.
- Wrappers for `Engine.onEntityAdded` and `Engine.onEntityRemoved` for `IteratingSystem`, `IntervalIteratingSystem` and
`SortedIteratingSystem` that use system's `Family` and `Engine` automatically.
- `propertyFor` and `optionalPropertyFor` allow extending `Entity` class with properties that automatically extract
the chosen component types with the property syntax.
- `tagFor` allow extending `Entity` class with properties that automatically check for presence of components used
as boolean flags (e.g. `Visible`).
- `mapperFor` factory method allows creating `ComponentMapper` instances.
- `Mapper` abstract class can be extended by `companion object`s of `Component` to obtain `ComponentMapper` instances.

> Note that `Mapper` class relies on reflection API unsupported by libGDX `ClassReflection`. While it should be safe
> to use on the officially supported platforms, it might not work correctly with the third-party backends.

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

Using an `Entity` extension property to access and modify a mandatory `Component`:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

class Transform: Component
// Using an extension property creates a mapper internally:
var Entity.transform by propertyFor<Transform>()
// propertyFor creates a non-nullable property that is meant
// to be used for mandatory components. Attempting to use the
// property on an Entity that does not have the specific
// component type might result in a runtime error.

val engine = PooledEngine()
val entity = engine.entity {
  with<Transform>()
}

// Obtaining an instance of the component:
val transform: Transform = entity.transform
// Replacing or setting a component instance:
fun setComponent(transform: Transform) {
  entity.transform = transform
}
```

Using an `Entity` extension property to access and modify an optional `Component`:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

class Transform: Component
// Using an extension property creates a mapper internally:
var Entity.transform by optionalPropertyFor<Transform>()
// optionalPropertyFor creates a nullable property that is meant
// to be used for optional components. It might return null if
// the entity does not contain the specific component type.

val engine = PooledEngine()
val entity = engine.entity {
  with<Transform>()
}

// Obtaining an instance of the component:
val transform: Transform? = entity.transform
// Checking if the component exists:
val exists: Boolean = entity.transform != null
// Replacing or setting a component instance:
fun setComponent(transform: Transform) {
  entity.transform = transform
}
// Removing a component instance:
fun removeComponent() {
  entity.transform = null
}
```

Using an `Entity` extension property to handle a flag `Component`:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.*

class Visible: Component
// Using an extension property creates a mapper internally:
var Entity.isVisible by tagFor<Visible>()
// tagFor creates a property that checks if an entity has
// a component of the given type. When you assign "true"
// to the property, an instance of the component is added
// to the entity; similarly, when "false" is assigned,
// the component is removed.

val engine = PooledEngine()
val entity = engine.entity {
  with<Visible>()
}

// Checking if the component exists:
val exists: Boolean = entity.isVisible
// Adding component instance to the entity:
fun setVisible() {
  entity.isVisible = true
}
// Removing the component instance:
fun setInvisible() {
  entity.isVisible = false
}

// Note that tagFor should only be used for immutable components.
```

Defining `Entity` tag extension properties:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import ktx.ashley.*

class Visible: Component

// The tagFor utility offers multiple overloads with different
// approaches to obtaining component instance.

// By default, if no parameters are passed, a single instance
// of the component will be created with reflection and reused
// by the property:
var Entity.isVisible by tagFor<Visible>()
// If you'd like to create a new instance each time the flag
// is set on an entity, set `singleton` setting to false:
var Entity.isVisible by tagFor<Visible>(singleton = false)

// The second option is to use a lambda expression that provides
// the component. By default, the lambda will be called once
// and its result will be reused:
var Entity.isVisible by tagFor<Visible> { Visible() }
// Similarly to the reflection example, you can change the
// `singleton` setting to call the lambda each time a component
// is required:
var Entity.isVisible by tagFor(singleton = false) { Visible() }

// The last option is to pass a component instance that will be
// reused by the property. This is a good approach if your
// component is already implemented as a singleton:
var Entity.isVisible by tagFor(Visible())

// Note that singleton tagFor variants should only be used
// for immutable components.
```

Creating a `ComponentMapper`:

```kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class Transform: Component

val transformMapper = mapperFor<Transform>()
```

Getting a `Component` from an `Entity` with a mapper:

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

Checking if an `Entity` has a `Component` with a mapper:

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
    val ID = mapperFor<Transform>()
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
// Will throw exceptions!
object CMapper: Mapper<C>()
```

Creating an `EntitySystem` that doubles as a listener of entity addition events:

```kotlin
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import ktx.ashley.EntityAdditionListener

class MySystem : EntitySystem(), EntityAdditionListener {
  override fun entityAdded(entity: Entity) {
    println("Entity added: $entity")
  }
}

```

Creating, storing and removing `EntityAdditionListener`s and `EntityRemovalListener`s with lambdas:

```kotlin
import com.badlogic.ashley.core.Engine
import ktx.ashley.onEntityAdded
import ktx.ashley.onEntityRemoved

val engine = Engine()

fun registerListeners() {
  val additionListener = engine.onEntityAdded { entity ->
    println("Entity added: $entity")
  }

  val removalListener = engine.onEntityRemoved { entity ->
    println("Entity removed: $entity")
  }

  // If you need to remove such listeners later,
  // retain a reference to them and call
  // Engine.removeEntityListener when necessary:
  engine.removeEntityListener(additionListener)
  engine.removeEntityListener(removalListener)
}
```

Managing `EntityAdditionListener`s and `EntityRemovalListener`s with an `IteratingSystem`'s `Family`:

```kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ktx.ashley.EntityAdditionListener
import ktx.ashley.EntityRemovalListener
import ktx.ashley.allOf
import ktx.ashley.onEntityAdded
import ktx.ashley.onEntityRemoved

class ExampleComponent : Component

// Listeners can be added to all Ashley iterating systems such as
// IteratingSystem, IntervalIteratingSystem or SortedIteratingSystem.
class MyIteratingSystem : IteratingSystem(allOf(ExampleComponent::class).get()) {
  // You can retain references to your listeners to remove them from the engine.
  private lateinit var additionListener: EntityAdditionListener
  private lateinit var removalListener: EntityRemovalListener

  override fun addedToEngine(engine: Engine) {
    super.addedToEngine(engine)

    additionListener = onEntityAdded { entity ->
      // Handle entities with an ExampleComponent being added.
    }

    removalListener = onEntityRemoved { entity ->
      // Handle entities with an ExampleComponent being removed.
    }
  }

  override fun processEntity(entity: Entity, deltaTime: Float) {
    // System logic goes here.
  }

  override fun removedFromEngine(engine: Engine) {
    super.removedFromEngine(engine)

    engine.removeEntityListener(additionListener)
    engine.removeEntityListener(removalListener)
  }
}
```

### Alternatives

- [Fleks](https://github.com/Quillraven/Fleks/) is a high performance Kotlin ECS library. Written with a Kotlin DSL
from day one, it does not require similar utilities to the Java ECS frameworks.
- [Artemis-odb](https://github.com/junkdog/artemis-odb) is a high performance Java ECS library. KTX provides utilities
for Artemis via the [`ktx-artemis`](../artemis) module.

#### Additional documentation

- [Ashley repository.](https://github.com/libgdx/ashley)
- [A classic article on Entity Component Systems.](http://t-machine.org/index.php/2007/09/03/entity-systems-are-the-future-of-mmog-development-part-1/)
