# KTX: `Ashley` entity component system utilities

Utilities and type-safe builders for the [Ashley](https://github.com/libgdx/ashley) entity component system.

### Why?
We can make using [Ashley](https://github.com/libgdx/ashley) with Kotlin more pleasant where Java Classes are 
required for many method calls by providing helper methods using reified types. Additionally, creating Entities and 
their respective Components can result in a lot of declarative-style code which is suited nicely to a readable type-safe 
builder DSL. 
 
### Guide

`ktx-ashley` provides extensions and utilities for using [Ashley](https://github.com/libgdx/ashley) with Kotlin:

- `Engine.add` and `Engine.entity` extension methods provide type-safe building DSL for creating non-pooled Entities
- `PooledEngine.add` and `PooledEngine.entity` extension methods provide type-safe building DSL for creating pooled Entities
- `mapperFor` factory method for create `ComponentMapper` instance.
- `ktx.ashley.entities.*` accessors for Entity objects
- `ktx.ashley.families.*` for constructing Family builders with KClasses

### Usage examples

Creating a new pooled Entity (similar for non-pooled Entities):
```Kotlin
val engine = PooledEngine()

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
val engine = PooledEngine()

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

Creating a ComponentMapper:
```Kotlin
val transformMapper = mapperFor<Transform>()
```

Getting a Component from an Entity:
```Kotlin
val component = entity[transformMapper]
```

Check a Component on an Entity:
```Kotlin
val component = entity.has(transformMapper)
```

Remove a Component from an Entity:
```Kotlin
fun removeTransform() = entity.remove<Transform>()
```

Create component Family to match all Components and excluding any:
```Kotlin
var family = all(Texture::class, Transform::class).exclude(RigidBody::class)
```

#### Additional documentation

- [Ashley](https://github.com/libgdx/ashley)
