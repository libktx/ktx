# KTX: JSON serialization utilities

Extension methods for LibGDX JSON serialization API.

### Why?

The LibGDX JSON reader and writer methods often consume `Class` parameters, which forces the
`Type::class.java` syntax on Kotlin users. Fortunately, Kotlin brings reified generics which effectively
allow passing a `Class` parameter through a generic type. This module mostly offers extension methods 
with reified generics to avoid using `::class.java` in your code, as well as to allow type inference
and better type safety. Additionally it provides couple of classes to facilitate creation of custom
serializers.

### Guide

KTX brings the following additions to LibGDX `Json` API:
- Extension methods and functions:
    - `fromJson`
    - `addClassTag`
    - `getTag`
    - `setElementType`
    - `setSerializer`
    - `readValue`
    - `readOnlySerializer`
- Classes:
    - `JsonSerializer<T>`
    - `ReadOnlyJsonSerializer<T>`

All of these extension methods are consistent with the official `Json` API, but provide inlined reified typing
to avoid passing `Class` instances to improve code readability.

A comparison of the APIs when used from Kotlin:

```kotlin
import com.badlogic.gdx.utils.Json
import ktx.json.fromJson

val json = Json()

// Using LibGDX API designed for Java:
json.fromJson(MyClass::class.java, file)

// Using KTX Kotlin extensions:
json.fromJson<MyClass>(file)
```

#### Usage examples

Creating a new `Json` serializer instance with custom parameters:

```kotlin
import ktx.json.*

val json = Json()

// Add shorthands for two classes:
json.addClassTag<Vector2>("vec2")
json.addClassTag<Color>("color")

// Set the type of elements in the "cards" collection of Player objects:
json.setElementType<Player, Card>("cards")

// Add a custom serializer for Vector2:
json.setSerializer(object : Json.Serializer<Vector2>() { /* ... */ })
```

A class with custom serializable implementation:

```kotlin
import com.badlogic.gdx.math.Vector2
import ktx.json.*

class Player(
    var position: Vector2 = Vector2(),
    var cards: List<Card> = emptyList()
  ) : Json.Serializable {

  override fun read(json: Json, jsonData: JsonValue) {
    pos = json.readValue("position", jsonData)  // Type inference.
    cards = json.readArrayValue("cards", jsonData)  // Type inference, better type safety.
  }

  override fun write(json: Json) {
    json.writeValue("position", position)
    json.writeValue("cards", cards)
  }
}
```

Parsing a JSON object:

```kotlin
import ktx.json.*

val json = Json()
val player: Player = json.fromJson("""{
  "pos": {"x": 10, "y": 10},
  "cards": [1, 2, 3, 5, 8, 13]
}""")
```

A custom serializer class:
```kotlin
import ktx.json.*
import com.badlogic.gdx.math.Vector2

class Vector2AsArraySerializer: JsonSerializer<Vector2> {
  override fun write(json: Json, obj: Vector2, knownType: Class<*>?) {
    json.writeArrayStart()
    json.writeValue(obj.x)
    json.writeValue(obj.y)
    json.writeArrayEnd()
  }

  override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): Vector2
      = jsonData.asFloatArray().let { (x, y) -> Vector2(x, y) }
}

// You can also create a read-only serializer:
val vector2AsArraySerializer = readOnlySerializer { jsonValue ->
 jsonValue.asFloatArray().let { (x, y) -> Vector2(x, y) }
}

val json = Json()
json.setSerializer(Vector2AsArraySerializer())

val player: Player = json.fromJson("""{
    "pos": [10, 10]
    "cards": [1, 2, 3, 5, 8, 13]
}""")
```

### Alternatives

LibGDX JSON is quite limited, verbose and poorly tested compared to some other JSON
serialization libraries. It also accepts and produces corrupted JSON files by default,
since it omits quotation marks, which might be problematic when integrating with external
services.

However, if your project is simple enough and you want to avoid including additional JSON 
serialization libraries in your game, official LibGDX `Json` can be enough. In most other
cases, you should probably look into other popular serialization libraries:

- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) provides
reflection-free serialization to JSON, CBOR and protobuf. Serialization code is produced
at compile time for classes marked with an annotation.
- Many popular JSON serialization libraries for Java: `Gson`, `Jackson`, `Moshi`, `org.json`
to name a few.

#### Additional documentation

- [Official `Json` usage article.](https://github.com/libgdx/libgdx/wiki/Reading-and-writing-JSON)
