[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-collections.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-collections)

# KTX: preference utilities

Utilities and extension function for LibGDX preferences.

### Why?

TBD

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
  prefs["Key3"] = 1f
  prefs["Key4"] = 1L
  prefs["Key5"] = true
  // Any classes are automatically converted to a Json string
  prefs["Key6"] = Player()

  prefs.run {
    // use Pairs to update preference values
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

  println(value1)
  println(value2)
  println(value3)
  println(value4)
  println(value5)
  println("${value6.name} - ${value6.life}")
  println(value7)
  println(value8)

  // adjust preferences before calling flush
  prefs.flush {
    set("Key9" to 10000)
    set("Key10" to true)
  }
}
```
