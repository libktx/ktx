# KTX : TiledMap utilities

Tiled utilities for LibGDX applications written with Kotlin.

### Why?

As usual with plain libgdx Java the code compared to Kotlin becomes boilerplate and is not
very easy to read especially for users who are not familiar with your code.
With the TiledMap functionality it becomes worse since there are a lot of wrapped collections
and iterators and accessing properties or iterating over objects in the map becomes very cumbersome.

Luckily for us with Kotlin we can write extension functions and properties to make our life
a little bit easier and most notably make our code more readable and safe.

Due to Kotlin's `reified` possibility we can retrieve properties without passing the `Class`
parameter and therefore also have this information during runtime. Also, most of the wrapper classes return `platform types (Any!)`
as the original Java code is null-unsafe. One of the goals of these extensions is also
to improve that situation and make it explicit so that a method either returns `null` or
throws an `exception`.

### Guide

#### Miscellaneous utilities

#### `MapProperties`

In most maps that you create with Tiled you will need access to the properties defined in the editor.
They are either defined on map, layer, object, tileset or tile level. The original libgdx
`MapProperties` class returns `Any!` whenever retrieving a property and is therefore not ideal and unsafe.

For that reason four extension methods got added to `TiledMap`, `MapLayer`, `MapObject`, `TiledMapTileSet`
and `TiledMapTile`. Also, a new `MissingPropertyException` was added for one of these extensions which allows us
to get informed when trying to access a missing mandatory property:
- `property(key)`: returns an existing property or throws a `MissingPropertyException`
- `property(key, defaultValue`: returns the value of a property or the default value if the property is missing
- `propertyOrNull(key)`: same as `property(key)` but returns `null` instead of throwing an exception
- `containsProperty(key)`: returns `true` if and only if the property exists

#### `MapObject`

In addition to the property extensions, `MapObject` automatically comes with a set of standard properties.
They get automatically set and initialized by the `TmxMapLoader`. Extension properties got added
to ease the access:
- `id`
- `x`
- `y`
- `width`
- `height`
- `rotation`: this property is only available if you rotate your object in Tiled
- `type`: this property is only available if you enter a text for the `Type` property in Tiled

Almost all objects are related to a shape except for  `TextureMapObject`. Sometimes you need
access to these shapes like e.g. when creating box2d bodies out of those objects. For that reason
a new extension property got added:
- `shape`: returns the `Shape2D` of a map object. This can either be a `Rectangle`, `Circle`, 
`Ellipse`, `Polyline` or `Polygon`. If there is an object that is not linked to a shape then a
`MissingShapeException` is thrown

#### `TiledMap`

Similar to `MapObject` there are several standard properties for `TiledMap` as well:
- `width`
- `height`
- `tileWidth`
- `tileHeight`
- `backgroundColor`: this property is only available if you explicitly select a background color for your map
- `orientation`
- `hexSideLength`
- `staggerAxis`
- `staggerIndex`

Two new extensions will provide you with the total width and height of your map in pixels:
- `totalWidth()`
- `totalHeight()`

The problems that we face with properties is also true for map layers. To improve the
situation here as well following extensions got added:
- `contains(layerName)`
- `layer(layerName)`: returns the layer or throws a `MissingLayerException` in case the layer does not exist

Often you need to iterate over all objects of a layer to do something with them like
creating collision bodies for box2d or other things. To ease that use case a new
extension was added called `forEachMapObject` which takes a lambda that is executed for each
object of the given layer:
- `forEachMapObject(layerName, action)`

### Usage examples

#### General

Properties functionality is the same for `TiledMap`, `MapLayer`, `MapObject`, `TiledMapTileSet` and `TiledMapTile`:

```kotlin
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*


val mapObj: MapObject = getMapObject()
val mapLayer: MapLayer = getMapLayer()
val map: TiledMap = getMap()

// retrieve Float property - throws MissingPropertyException if missing
val myFloatProp: Float = mapObj.property("myFloatProperty")

// retrieve String property via defaultValue
val myProp: String = mapObj.property("myProperty", "")

// the explicit type can be omitted as it is automatically derived from the type of the default value
// myProp2 is of type Float
val myProp2 = mapLayer.property("myProperty2", 1f)

// retrieve Int property without defaultValue
val myOtherProp: Int? = map.propertyOrNull("myOtherProperty")

// check if a certain property exists
if (map.containsProperty("lightColor")) {
    changeAmbientLightColor(map.property<String>("lightColor"))
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

Check and retrieve a layer of a `TiledMap`:

```kotlin
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*


val map: TiledMap = getTiledMap()

// contains can either be used with the normal syntax
if(map.contains("enemyLayer")) {
    val enemyLayer = map.layer("enemyLayer")
}

// or with the "in" syntax
if("collision" in map) {
    val collisionLayer = map.layer("collision")    
}

// the next line will throw a MissingLayerException if the layer does not exist
val layer = map.layer("myMapLayer")
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
- [Tiled editor official documentation](https://doc.mapeditor.org/en/stable/)
