[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-tiled.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-tiled)

# KTX: Tiled map editor utilities

[Tiled](https://www.mapeditor.org/) API utilities.

### Why?

LibGDX brings its own set of Tiled map utilities, including loading and handling of maps exported from the editor.
However, the API contains many wrapped non-standard collections, which makes accessing the loaded maps cumbersome.
With Kotlin's reified types and extension methods, the Tiled API can be significantly improved.

### Guide

#### `MapProperties`

In many maps that you create with Tiled you will need to access the properties defined in the editor.
They are either defined on map, layer, object, tileset or tile level. The original libGDX `MapProperties`
class returns untyped `Object` (or Kotlin's `Any!`) whenever retrieving a property and is therefore not ideal
and unsafe.

To improve this, multiple extension methods were added to `TiledMap`, `MapLayer`, `MapObject`, `TiledMapTileSet`
and `TiledMapTile`. Also, a new `MissingPropertyException` to handle missing properties with an explicit exception.
The new additions include:
- `property(key: String): T`: returns an existing property or throws a `MissingPropertyException`
- `property(key: String, defaultValue: T): T`: returns the value of a property or the default value
if the property is missing.
- `propertyOrNull(key: String): T?`: same as `property(key)`, but returns `null` instead of throwing an exception.
- `containsProperty(key: String): Boolean`: returns `true` if and only if the property exists.

`MapProperties` were also extended to support the `contains` (`in`) and `set` (`[]` assignment) operators.
Unfortunately, since the original class already defines `get` method, type-safe `get` (`[]`) operator was not added.

#### `MapObject`

In addition to the property extensions, `MapObject` automatically comes with a set of standard properties.
They allow to retrieve the properties automatically set and initialized by the `TmxMapLoader`.
New extension fields include:
- `id`
- `x`
- `y`
- `width`
- `height`
- `rotation`: this property is only available if you rotate your object in Tiled.
- `type`: this property is only available if you enter a text for the `Type` property in Tiled.

Almost all objects are related to a shape except for  `TextureMapObject`. Sometimes you need
access to these shapes like e.g. when creating [Box2D](../box2d) bodies out of those objects. For that reason
a new extension field was added:
- `shape`: returns the `Shape2D` of a map object. This can either be a `Rectangle`, `Circle`,
`Ellipse`, `Polyline` or `Polygon`. If there is an object that is not linked to a shape then a
`MissingShapeException` is thrown.

#### `TiledMap`

Similar to `MapObject`, there were several standard properties added to `TiledMap` as well:
- `width`
- `height`
- `tileWidth`
- `tileHeight`
- `backgroundColor`: this property is only available if you explicitly select a background color for your map.
- `orientation`
- `hexSideLength`
- `staggerAxis`
- `staggerIndex`

Two new extension methods will provide you with the total width and height of your map in pixels:
- `totalWidth()`
- `totalHeight()`

The problems that we face with properties are also relevant in case of map layers. To improve layer handling API
the following extensions were added:
- `contains(layerName: String)`: works as the `in` operator.
- `layer(layerName: String)`: returns the layer or throws a `MissingLayerException` in case the layer does not exist.

Inlined `forEachMapObject` extension method iterates over all `MapObject` instances present on the chosen
map layer.

Inlined `forEachLayer` extension method iterates over all `MapLayer` instances of a specific type to execute
a certain function on them.

### `MapLayers` and `MapObjects`

`isEmpty` and `isNotEmpty` extension method to check if the specific collection is empty or not.

### `BatchTiledMapRenderer`

`use` extension method to call `beginRender()` and `endRender()` automatically before
your render logic.

### Usage examples

#### General

Accessing properties of `TiledMap`, `MapLayer`, `MapObject`, `TiledMapTileSet` and `TiledMapTile`:

```kotlin
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*

val mapObj: MapObject = getMapObject()
val mapLayer: MapLayer = getMapLayer()
val map: TiledMap = getMap()

// Retrieves Float property - throws MissingPropertyException if missing:
val myFloatProp: Float = mapObj.property("myFloatProperty")

// Retrieves String property with a default value:
val myProp: String = mapObj.property("myProperty", defaultValue = "")

// The explicit type can be omitted as it is automatically derived from the type of the default value.
// myProp2 is of type Float
val myProp2 = mapLayer.property("myProperty2", defaultValue = 1f)

// Retrieves Int property or null if the property does not exist.
val myOtherProp: Int? = map.propertyOrNull("myOtherProperty")

// Check if a certain property exists:
if (map.containsProperty("lightColor")) {
    changeAmbientLightColor(map.property<String>("lightColor"))
}
```

Using `MapProperties` API:

```kotlin
import com.badlogic.gdx.maps.MapProperties
import ktx.tiled.contains
import ktx.tiled.set

val mapProperties = MapProperties()

// Adds a property to the map:
mapProperties["key"] = "value"

// Checks if a property exists:
if ("key" in mapProperties) {
  // Reads a property:
  val value: String = mapProperties["key"] as String

  // Note: since MapProperties already defines `get` method,
  // a fully typed extension could not have been added.
  // Use property-retrieving extensions of other Tiled classes
  // or handle casting manually as in the example above.
}
```

#### `MapObject`

Retrieving standard properties of a `MapObject` like `id`, `x` or `shape`:

```kotlin
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.math.Rectangle
import ktx.tiled.*

val mapObj: MapObject = getMapObject()

// Retrieve position of an object:
val x = mapObj.x
val y = mapObj.y

// Retrieves ID of an object:
val id = mapObj.id

// Retrieves shape of an object:
val shape = mapObj.shape // if you only need the Shape2D instance
val rect = mapObj.shape as Rectangle // if you need the rect
```

#### `TiledMap`

Retrieving standard properties of a `TiledMap` like `width` or `tileheight`:

```kotlin
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*

val map: TiledMap = getTiledMap()

// Gets map size in pixels to e.g. lock camera movement within map boundaries:
val totalWidth = map.totalWidth()
val totalHeight = map.totalHeight()

// Retrieve map size information:
val width = map.width
val height = map.height
val tileWidth = map.tileWidth
val tileHeight = map.tileHeight
```

Working with layers of a `TiledMap`:

```kotlin
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*

val map: TiledMap = getTiledMap()

// Contains can be used either with the regular method call syntax:
if (map.contains("enemyLayer")) {
    val enemyLayer = map.layer("enemyLayer")
}

// Or with the "in" operator:
if ("collision" in map) {
    val collisionLayer = map.layer("collision")
}

// This will throw a MissingLayerException if the layer does not exist:
val layer = map.layer("myMapLayer")
```

Iterating over map objects of a specific `MapLayer` of a map:

```kotlin
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*

val map: TiledMap = getTiledMap()

// Creates collision bodies for every map object of the collision layer:
map.forEachMapObject("collision") { mapObj ->
    createStaticBox2DCollisionBody(mapObj.x, mapObj.y, mapObj.shape)
}
```

Iterating over a specific type of layers of a map:

```kotlin
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*

val map: TiledMap = getTiledMap()

// Iterate over all object layers and parse them.
// Note that println is only called with layers of the exact MapLayer type.
// For example, TiledMapTileLayer - which is a subclass of MapLayer - does not
// have this exact class and will not be matched.
map.forEachLayer<MapLayer> { layer ->
  println(layer)
}
```

Checking if `MapLayers` and `MapObjects` collections are empty:

```kotlin
import com.badlogic.gdx.maps.MapObjects
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.*

val map: TiledMap = TiledMap()

if (map.layers.isNotEmpty()) {
  map.layers.forEach { layer ->
    if (layer.objects.isEmpty()) {
      // nothing to do if there are no objects
      return@forEach
    }
    parseObjects(layer.objects)
  }
}
```

Using the `use` extension function to render background layers:

```kotlin
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

val tiledMap = TiledMap()
val renderer = OrthogonalTiledMapRenderer(tiledMap)
val camera = OrthographicCamera()
val bgdLayers: List<TiledMapTileLayer> = tiledMap.layers
    .filter { it.name.startsWith("bgd") }
    .filterIsInstance<TiledMapTileLayer>()

renderer.use(camera) { mapRenderer ->
    bgdLayers.forEach { mapRenderer.renderTileLayer(it) }
}
```

#### Additional documentation

- [Official Tiled website.](https://www.mapeditor.org/)
- [Official libGDX tile maps article.](https://libgdx.com/wiki/graphics/2d/tile-maps)
- [Official Tiled documentation.](https://doc.mapeditor.org/en/stable/)
