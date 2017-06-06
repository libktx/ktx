# KTX: `Ashley` entity component system utilities

Utilities and type-safe builders for the [Ashley](https://github.com/libgdx/ashley) entity component system.

### Why?
We can make using [Ashley](https://github.com/libgdx/ashley) with Kotlin more pleasant by providing helper methods using 
reified types where Java `Classes` are required for method calls. Additionally, creating `Entities` and 
their respective `Components` can result in a lot of declarative-style code which is suited nicely to a readable type-safe 
builder DSL. 
 
### Guide

`ktx-ashley` provides extensions and utilities for using [Ashley](https://github.com/libgdx/ashley) with Kotlin:

- `Engine.add` and `Engine.entity` extension methods provide type-safe building DSL for creating non-pooled `Entities`
- `PooledEngine.add` and `PooledEngine.entity` extension methods provide type-safe building DSL for creating pooled `Entities`
    - Note: ensure that extensions are imported from the correct package `ktx.ashley.engine.pool.*`, otherwise
    the `Engine` extensions will be imported entities and components will not be pooled
- `mapperFor` factory method for create `ComponentMapper` instance.
- `ktx.ashley.entities.*` accessors for `Entity` objects
- `ktx.ashley.families.*` for constructing `Family` builders with `KClasses`

### Usage examples

Creating a new pooled `Entity`:
```Kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.engine.pool.entity

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
import ktx.ashley.engine.pool.*

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

Create new non-pooled `Entity`:
```Kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import ktx.ashley.engine.*

val engine = Engine()

class Texture: Component
class Transform(var x:Float = 0f, var y:Float = 0f): Component

val entity = engine.entity {
  add(Texture())
  add(Transform(
    x = 1f,
    y = 1f
  ))
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
import ktx.ashley.engine.pool.entity
import ktx.ashley.get
import ktx.ashley.mapperFor

class Transform: Component

val engine = PooledEngine()
val transformMapper = mapperFor<Transform>()
val entity = engine.entity {
  with<Transform>()
}
val component = entity[transformMapper]
```

Check a `Component` on an `Entity``:
```Kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.engine.pool.entity
import ktx.ashley.get
import ktx.ashley.mapperFor

class Transform: Component

val engine = PooledEngine()
val transformMapper = mapperFor<Transform>()
val entity = engine.entity {
  with<Transform>()
}
val component = entity.has(transformMapper)
```

Remove a `Component` from an `Entity`:
```Kotlin
import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.PooledEngine
import ktx.ashley.engine.pool.entity
import ktx.ashley.remove

class Transform: Component

val engine = PooledEngine()
val entity = engine.entity {
  with<Transform>()
}

fun removeTransform() {
  entity.remove<Transform>()
}
```

Create component `Family` to match all `Components` with an exclusion:
```Kotlin
import com.badlogic.ashley.core.Component
import ktx.ashley.all
import ktx.ashley.exclude

class Texture: Component
class Transform: Component
class RigidBody: Component

var family = allOf(Texture::class, Transform::class).exclude(RigidBody::class)
```

#### Additional documentation

- [Ashley](https://github.com/libgdx/ashley)
