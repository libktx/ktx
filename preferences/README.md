[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-preferences.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-preferences)

# KTX: preference utilities

Utilities and extension function for LibGDX preferences.

### Why?

LibGDX [`Preferences`](https://github.com/libgdx/libgdx/wiki/Preferences) do not provide a consistent
API for setting and getting values, and they do not support Kotlin operators either. Since in principle
`Preferences` work very similarly to a `Map`, ideally they should support a similar syntax -
especially since in Kotlin you can take advantage of the square bracket operators.

### Guide

- Values can be set and read via the new `set` and `get` operators using the square bracket (`[]`) syntax.
It is no longer necessary to call type specific methods like `putString` or `getBoolean` for
each type separately. `set` and `get` support objects of any type. If the type is not of `String`, `Boolean`,
`Int`, `Float`, `Double` or `Long` type, the value is stored and retrieved using 
[JSON](https://github.com/libgdx/libgdx/wiki/Reading-and-writing-JSON) serialization.
  - Note that `Double` type is not supported by LibGDX `Preferences` and converted to `Float`
  instead. Use explicit casts (`toFloat()`) or wrap the value with a JSON-serializable object
  when storing numbers that do not fit in a `Float`.
- Preferences can now be set with a `Pair<String, Any>` parameter. Keys and values can be paired using
the standard library `to` infix function.
- New `flush` extension supports a lambda parameter to easily update the `Preferences` before flushing.

### Usage examples

Reading basic application preferences:

```kotlin
import com.badlogic.gdx.Gdx
import ktx.preferences.*

fun readValuesFromPreferences() {
  val preferences = Gdx.app.getPreferences("myPreferences")
  // Accessing preferences by keys passed as parameters:
  val string: String? = preferences["SomeString"]
  val float: Float? = preferences["SomeFloat"]
  val boolean: Boolean? = preferences["SomeBoolean"]
  val integer: Int? = preferences["SomeInt"]
  val long: Long? = preferences["SomeLong"]
  // Do something with preferences here.
}
```

Reading basic preferences with custom default values in case they are missing:

```kotlin
import com.badlogic.gdx.Preferences
import ktx.preferences.*

fun readValuesWithDefaultsFromPreferences(preferences: Preferences) {
  // If the key (given as the first parameter) is absent in the preferences,
  // the default value (passed as the second parameter) will be returned:
  val string: String = preferences["SomeString", "default value"]
  val float: Float = preferences["SomeFloat", 2.0f]
  val boolean: Boolean = preferences["SomeBoolean", true]
  val integer: Int = preferences["SomeInt", 42]
  val long: Long = preferences["SomeLong", 3L]
  // Alternative syntax without the [] operator:
  val preference = preferences.get<Int>("Key", defaultValue = 42)
  // Do something with preferences here.
}
```

Loading objects serialized as JSON from preferences:

```kotlin
import com.badlogic.gdx.Preferences
import ktx.preferences.*

/**
 * This class will be serialized.
 * 
 * Remember to add default values for each variable,
 * so the class remains JSON-serializable!
 */
data class Player(val name: String = "Player", val life: Int = 100)

fun readObjectFromPreferences(preferences: Preferences): Player? {
  // Will automatically deserialize the object from JSON:
  return preferences["Player"]
}
```

Saving preferences:

```kotlin
import com.badlogic.gdx.Preferences
import ktx.preferences.*

fun saveInPreferences(
    preferences: Preferences,
    string: String, float: Float, bool: Boolean, int: Int, long: Long) {
  // The values will be stored in preferences under the given keys:
  preferences["SomeString"] = string
  preferences["SomeFloat"] = float
  preferences["SomeBoolean"] = bool
  preferences["SomeInt"] = int
  preferences["SomeLong"] = long
  // Remember to call flush in order to save the preferences:
  preferences.flush()
}
```

Saving preferences with automatic flushing using `flush` extension method:

```kotlin
import com.badlogic.gdx.Preferences
import ktx.preferences.*

fun saveInPreferencesWithFlush(
    preferences: Preferences,
    string: String, float: Float, bool: Boolean, int: Int, long: Long) {
  preferences.flush {
    // This extension method changes `this` to preferences within the scope:
    this["SomeString"] = string
    this["SomeFloat"] = float
    this["SomeBoolean"] = bool
    this["SomeInt"] = int
    this["SomeLong"] = long 
    // preferences.flush() will be called automatically after this block.
  }
}
```

Saving objects as JSON in preferences:

```kotlin
import com.badlogic.gdx.Preferences
import ktx.preferences.*

data class Player(val name: String = "Player", val life: Int = 100)

fun saveObjectInPreferences(preferences: Preferences, player: Player) {
  preferences.flush {
    // Will automatically serialize the object to JSON:
    this["Player"] = player
  }
}
```

Setting preferences using Kotlin pairs and `to` infix function:

```kotlin
import com.badlogic.gdx.Preferences
import ktx.preferences.*

data class Player(val name: String = "Player", val life: Int = 100)

fun addPreferencesUsingKotlinPairs(preferences: Preferences) {
  preferences.set("SomeInt" to 1)
  preferences.set("SomeFloat" to 1.0f)
  preferences.set("SomeLong" to 1L)
  preferences.set("SomeBoolean" to true)
  preferences.set("SomeString" to "value")
  preferences.set("Player" to Player(name = "Example", life = 75))
}
```

### Additional documentation

- [Official LibGDX `Preferences` article](https://github.com/libgdx/libgdx/wiki/Preferences).
- [Official LibGDX `Json` article](https://github.com/libgdx/libgdx/wiki/Reading-and-writing-JSON).
