# KTX : TiledMap utilities

TiledMap utilities for LibGDX applications written with Kotlin.

### Why?

As usual with plain libgdx Java the code compared to Kotlin becomes boilerplate and is not
very easy to read especially for users who are not familiar with your code.
With the TiledMap functionality it becomes worse since there are a lot of wrapped collections
and iterators and accessing properties or iterating over objects in the map becomes very cumbersome.

Luckily for us with Kotlin we can write extension functions and properties to make our life
a little bit easier and most notably make our code more readable.

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

`MapObject`:

```kotlin
// assume mapObj refers to a MapObject instance

// before
val myProp = mapObj.properties.get("myProperty", "", String::class.java)
val myOtherProp = mapObj.properties.get("myOtherProperty", Int::class.java)
val x = mapObj.properties.get("x", 0f, Float::class.java)
val y = mapObj.properties.get("y", 0f, Float::class.java)
val shape = (mapObj as RectangleMapObject).rectangle

// using libktx
val myProp = mapObj.property("myProperty", "")
val myOtherProp: Int? = mapObj.propertyOrNull("myOtherProperty")
val x = mapObj.x
val y = mapObj.y
val shape = mapObj.shape // if you only need the Shape2D instance
val rect = mapObj.shape as Rectangle // if you need the rect
```

`TiledMap`:

```kotlin
// assume map refers to a TiledMap instance

// before
val myProp = map.properties.get("myProp", String::class.java)
val myProp2 = map.properties.get("myProp2", true, Boolean::class.java)
val width = map.properties.get("width", Float::class.java)
val totalWidth = map.properties.get("width", Float::class.java) * map.properties.get("tilewidth", Float::class.java)

map.layers.get("myLayer")?.objects?.forEach {
    val x = it.properties.get("x", Float::class.java)
    val y = it.properties.get("y", Float::class.java)
    // create an object at x/y position
}

val myLayerProp = map.layers.get("myLayer")?.properties?.get("myLayerProp", Int::class.java)



// using libktx
val myProp:String? = map.propertyOrNull("myProp")
val myProp2 = map.property("myProp2", true)
val width = map.width
val totalWidth = map.totalWidth()

map.forEachMapObject("myLayer") {
    val x = it.x
    val y = it.y
    // create an object at x/y position
}

val myLayerProp: Int? = map.layer("myLayer").propertyOrNull("myLayerProp")
```

#### Additional documentation

- [LibGDX Tile maps official wiki.](https://github.com/libgdx/libgdx/wiki/Tile-maps)
