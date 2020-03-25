package ktx.preferences

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.Json

/**
 * Stores a String **value** under the given **key** in the [Preferences].
 */
operator fun Preferences.set(key: String, value: String): Preferences = putString(key, value)

/**
 * Stores a Boolean **value** under the given **key** in the [Preferences].
 */
operator fun Preferences.set(key: String, value: Boolean): Preferences = putBoolean(key, value)

/**
 * Stores an Int **value** under the given **key** in the [Preferences].
 */
operator fun Preferences.set(key: String, value: Int): Preferences = putInteger(key, value)

/**
 * Stores a Long **value** under the given **key** in the [Preferences].
 */
operator fun Preferences.set(key: String, value: Long): Preferences = putLong(key, value)

/**
 * Stores a Float **value** under the given **key** in the [Preferences].
 */
operator fun Preferences.set(key: String, value: Float): Preferences = putFloat(key, value)

/**
 * Stores Any **value** under the given **key** as Json string in the [Preferences].
 */
operator fun Preferences.set(key: String, value: Any): Preferences = putString(key, Json().toJson(value))

/**
 * Stores Any **value** under the given **key** in the [Preferences]. If the value is not of type
 * String, Boolean, Int, Float or Long then it will be stored as a Json string.
 */
fun Preferences.set(pair: Pair<String, Any>): Preferences {
  return when (pair.second) {
    is String -> putString(pair.first, pair.second as String)
    is Boolean -> putBoolean(pair.first, pair.second as Boolean)
    is Int -> putInteger(pair.first, pair.second as Int)
    is Float -> putFloat(pair.first, pair.second as Float)
    is Long -> putLong(pair.first, pair.second as Long)
    else -> putString(pair.first, Json().toJson(pair.second))
  }
}

/**
 * Retrieves a value from the [Preferences] for the given **key**. If the value is not of type
 * String, Boolean, Int, Float or Long then it will be retrieved as Json string for the given **type**.
 */
inline operator fun <reified T> Preferences.get(key: String): T {
  return when (T::class) {
    String::class -> getString(key) as T
    Boolean::class -> getBoolean(key) as T
    Int::class -> getInteger(key) as T
    Float::class -> getFloat(key) as T
    Long::class -> getLong(key) as T
    else -> Json().fromJson(T::class.java, getString(key))
  }
}

/**
 * Calls [Preferences.flush] after executing the given **operations**.
 * Operations can be any function of the [Preferences] class.
 */
inline fun Preferences.flush(operations: Preferences.() -> Unit) {
  operations()
  flush()
}
