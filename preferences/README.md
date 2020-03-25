[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-collections.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-collections)

# KTX: preference utilities

Utilities and extension function for LibGDX preferences.

### Why?

LibGDX [Preferences](https://github.com/libgdx/libgdx/wiki/Preferences) do not support a generic way to
set and get values. Since they work very similar to a Map, they should ideally support a similar
syntax. Especially with Kotlin we can use the advantage of square bracket operators.

### Guide

- Values can be set via new `set` operators using the square bracket syntax. It is no longer needed
to call type specific methods like `putString` or `putBoolean`. `Set` already supports any type.
- Values can be retrieved via new generic `get` operator.
- Preferences now support `Pair<String, Any>` parameter. Values can be set by using the `infix to` function
- New `flush` extension that supports a Lambda to easily update the Preferences before flushing.
- `set` and `get` support objects of any type. If the type is not a String, Boolean, Int, Float or Long value
then the value is stored and retrieved using [Json](https://github.com/libgdx/libgdx/wiki/Reading-and-writing-JSON). 
.
### Usage examples

```kotlin
import com.badlogic.gdx.Gdx
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set

private class Player(val name: String = "Player 1", val life: Int = 100)

fun main() {
  val prefs = Gdx.app.getPreferences("MyPreferences")

  // set values with new set operator functions
  prefs["Key1"] = "someStringValue"
  prefs["Key2"] = 1
  prefs["Key3"] = 1.23f
  prefs["Key4"] = 100L
  prefs["Key5"] = true
  // Any classes are automatically converted to a Json string
  prefs["Key6"] = Player()

  prefs.run {
    // use Pair<String, Any> to update preference values
    set("Key7" to 123)
    set("Key8" to "someOtherStringValue")
  }

  // get values with new get operator function
  val value1: String = prefs["Key1"]
  val value2: Int = prefs["Key2"]
  val value3: Float = prefs["Key3"]
  val value4: Long = prefs["Key4"]
  val value5: Boolean = prefs["Key5"]
  // Any classes are automatically loaded from a Json string
  val value6: Player = prefs["Key6"]
  val value7: Int = prefs["Key7"]
  val value8: String = prefs["Key8"]

  println(value1) // prints 'someStringValue'
  println(value2) // prints 1
  println(value3) // prints 1.23
  println(value4) // prints 100
  println(value5) // prints true
  println("${value6.name} - ${value6.life}") // prints 'Player 1 - 100'
  println(value7) // prints 123
  println(value8) // prints 'someOtherStringValue'

  // adjust preferences before calling flush
  // Key9 and Key10 will be flushed as well
  prefs.flush {
    set("Key9" to 10000)
    set("Key10" to true)
  }
}
```

### Additional documentation

- [LibGDX Json](https://github.com/libgdx/libgdx/wiki/Reading-and-writing-JSON)
- [LibGDX Preferences](https://github.com/libgdx/libgdx/wiki/Preferences)
- [Kotlin infix to function for Pair](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/to.html)
