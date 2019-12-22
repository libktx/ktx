# KTX : TiledMap utilities

TiledMap utilities for LibGDX applications written with Kotlin.

### Why?

As usual with plain libgdx Java the code compared to Kotlin becomes boilerplate and is not
very easy to read especially for users who are not familiar with your code.
With the TiledMap functionality it becomes worse since there are a lot of wrapped collections
and iterators and accessing properties or iterating over objects in the map becomes very cumbersome.

Luckily for us with Kotlin we can write extension functions and properties to make our life
a little bit easier and most notably make our code more readable.

From a code point of view it makes it easier to read as most of the time we can skip the
wrapper class and directly access things like e.g. we can directly get the `layer` from a `TiledMap`
without mentioning the `layers` collection wrapper.

Also, due to Kotlin's `reified` possibility we can retrieve properties without passing the `Class`
parameter and therefore also have this information during runtime. 

### Guide

#### Miscellaneous utilities

#### `MapObject`

In most maps that you create with Tiled you will create objects in it. There are a few
things that are common for any map object like `id`, `shape`, `x-` and `y` coordinates.
Properties in libgdx are stored in a wrapper class called `MapProperties` which is nothing less
than a `ObjectMap<String,Object>`.

For easier access of those properties, including the standard properties mentioned above, you can use
following new extensions:
- `property(key, defaultValue)`
- `propertyOrNull(key)`
- `containsProperty(key)`
- `x`, `y`, `id` and `shape`

#### `MapLayer`

The same thing regarding properties holds true for `MapLayer`. Therefore, following three extensions
will help you out:
- `property(key, defaultValue)`
- `propertyOrNull(key)`
- `containsProperty(key)`

#### `TiledMap`

What is true for `MapObject` and `MapLayer` is of course also true for `TiledMap`.
Standard properties for a map are `width`, `height`, `tilewidth` and `tileheight`. They
are part of any map and are useful to e.g. lock the camera movement to the map boundaries.

As usual with Kotlin we are trying to avoid **null** and for that reason a new layer extension
was added to retrieve a non-null `MapLayer` which is just an empty default layer without any properties
or objects. Its name is **ktx-default-map-layer**.

Often you need to iterate over all objects of a layer to do something with them like
creating collision bodies for box2d or other things. To ease that use case a new
extension was added called `forEachMapObject` which takes a lambda that is executed for each
object of the given layer. Together with the `layer` extension mentioned above it becomes easy to write
such loops.

To summarize it, following extensions are available:
- `property(key, defaultValue)`
- `propertyOrNull(key)`
- `containsProperty(key)`
- `width`, `height`, `tileWidth`, `tileHeight`
- `totalWidth()` and `totalHeight()`
- `layer(layerName)`
- `forEachMapObject(layerName, action)`

### Usage examples

#### General

Properties functionality is the same for `MapObject`, `MapLayer` and `TiledMap`:

```kotlin
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*


val mapObj: MapObject = getMapObject()
val mapLayer: MapLayer = getMapLayer()
val map: TiledMap = getMap()

// retrieve String property via defaultValue
val myProp: String = mapObj.property("myProperty", "")

// the explicit type can be omitted as it is automatically derived from the type of the default value
// myProp2 is of type Float
val myProp2 = mapLayer.property("myProperty2", 1f)

// retrieve Int property without defaultValue
val myOtherProp: Int? = map.propertyOrNull("myOtherProperty")

// check if a certain property exists
if (map.containsProperty("lightColor")) {
    // change box2d light ambient color
}
```

#### `MapObject`

Retrieving standard properties of a `MapObject` like `id`, `x` or `shape`:

```kotlin
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.math.Rectangle
import ktx.tiled.*


val mapObj: MapObject = getMapObject()

// retrieve position of object
val x = mapObj.x
val y = mapObj.y

// retrieve id of object
val id = mapObj.id

// retrieve shape
val shape = mapObj.shape // if you only need the Shape2D instance
val rect = mapObj.shape as Rectangle // if you need the rect
```

#### `TiledMap`

Retrieving standard properties of a `TiledMap` like `width` or `tileheight`:

```kotlin
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*


val map: TiledMap = getTiledMap()

// get map size in pixels to e.g. lock camera movement within map boundaries
val totalWidth = map.totalWidth()
val totalHeight = map.totalHeight()

// retrieve map size information
val width = map.width
val height = map.height
val tileWidth = map.tileWidth
val tileHeight = map.tileHeight
```

Retrieving a non-null layer of a `TiledMap`:

```kotlin
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*


val map: TiledMap = getTiledMap()

// get collision layer - if it is missing an empty default layer is returned
val collisionLayer = map.layer("collision")
```

Iterating over map objects of a specific `MapLayer` of a map:

```kotlin
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*


val map: TiledMap = getTiledMap()

// create collision bodies for every map object of the collision layer
map.forEachMapObject("collision") { mapObj ->
    createStaticBox2DCollisionBody(mapObj.x, mapObj.y, mapObj.shape)
}
```

#### Additional documentation

- [LibGDX Tile maps official wiki.](https://github.com/libgdx/libgdx/wiki/Tile-maps)
