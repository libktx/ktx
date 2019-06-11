# KTX: Json extensions

Extension methods for LibGDX JSON reader and writer.

### Why?

The JSON reader and writer in LibGDX uses a lot of `Class` parameter, which leads to a lot
of `Type::class.java` in method arguments. Kotlin brings reified generics which create the
possibility to get the `Class` of a generic type parameter. This module offers extension methods 
with reified generics to avoid using `::class.java` in your code and to allow type inference and
in some cases better type safety.

### Guide
A JSON instance can be used the same as usual, but using the extension method whenever there's
a class parameter in the original method.

##### Usage examples

Creating a new JSON instance with custom parameters:
```kotlin
import ktx.json.*

val json = Json()

// Add shorthands for two classes
json.addClassTag<Vector2>("vec2")
json.addClassTag<Color>("color")

// Set the type of elements in the "cards" collection of Player objects
json.setElementType<Player, Card>("cards")

// Add a custom serializer for Vector2
json.setSerializer(object : Json.Serializer<Vector2>() { /* ... */ })
```

A class with a custom serializer:
```kotlin
import ktx.json.*

class Player : Json.Serializable {
  lateinit var pos: Vector2
  lateinit var cards: List<Simple>

  override fun read(json: Json, jsonData: JsonValue) {
    pos = json.readValue("pos", jsonData)  // Type inference
    cards = json.readArrayValue("cards", jsonData)  // Type inference, better type safety
  }

  override fun write(json: Json) {
    json.writeValue("pos", pos)
    json.writeValue("cards", cards)
  }
}
```

Parsing a JSON object:
```kotlin
import ktx.json.*

val json = Json()
val player: Player = json.fromJson("{pos:{x:10,y:10},cards:[1,2,3,5,8,13]}")
```

### Alternatives

LibGDX JSON is quite limited and verbose compared to other JSON serialization libraries.
It's a mostly untested alternative to all the other libraries around. But if your project
is simple and you want to avoid including two JSON serialization libraries in your game
(since it's already used for Skin parsing), LibGDX can be enough. In most cases however,
you should probably look into other popular serialization libraries:

- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) provides
reflection-free serialization to JSON, CBOR and protobuf. Serialization code is produced
at compile time for classes marked with an annotation.
- Many JSON libraries are already available for Java: Gson, Moshi, Jackson, org.json to name a few.


#### Additional documentation

- [Reading and writing JSON article.](https://github.com/libgdx/libgdx/wiki/Reading-and-writing-JSON)