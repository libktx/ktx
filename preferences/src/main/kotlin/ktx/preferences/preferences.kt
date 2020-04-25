package ktx.preferences

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Json
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Stores a [String] [value] under the given [key] in the [Preferences].
 */
operator fun Preferences.set(key: String, value: String): Preferences = putString(key, value)

/**
 * Stores a [Boolean] [value] under the given [key] in the [Preferences].
 */
operator fun Preferences.set(key: String, value: Boolean): Preferences = putBoolean(key, value)

/**
 * Stores an [Int] [value] under the given [key] in the [Preferences].
 */
operator fun Preferences.set(key: String, value: Int): Preferences = putInteger(key, value)

/**
 * Stores a [Long] [value] under the given [key] in the [Preferences].
 */
operator fun Preferences.set(key: String, value: Long): Preferences = putLong(key, value)

/**
 * Stores a [Float] [value] under the given [key] in the [Preferences].
 */
operator fun Preferences.set(key: String, value: Float): Preferences = putFloat(key, value)

/**
 * Stores a [Double] [value] as [Float] under the given [key] in the [Preferences].
 * Throws [GdxRuntimeException] if [value] is outside of the [Float] range.
 */
@Deprecated(
  message = "Doubles are not supported by LibGDX Preferences. " +
    "Value will be stored as Float instead. Please add explicit cast.",
  replaceWith = ReplaceWith("set(key, value.toFloat()"))
operator fun Preferences.set(key: String, value: Double): Preferences = putFloat(key, value.asFloat())

private fun Double.asFloat(): Float {
  if (this < Float.MIN_VALUE.toDouble() || this > Float.MAX_VALUE.toDouble()) {
    throw GdxRuntimeException("$this is outside of float range.")
  }
  return toFloat()
}

/**
 * Serializes any [value] as a JSON string and stores it under the given [key] in the [Preferences].
 * Note that [value] must be JSON-serializable - see the [Json] module for details.
 *
 * A new [Json] instance is created for each serialized value. If the preference is saved frequently
 * and excessive object creation might be a concern, consider manual serialization and storing the
 * value as a [String].
 */
operator fun Preferences.set(key: String, value: Any): Preferences = putString(key, Json().toJson(value))

/**
 * Stores any value under the given key in the [Preferences]. The first value of the [pair] is considered
 * the key, while the second is treated as the value.
 *
 * If the value is not of [String], [Boolean], [Int], [Float], [Double] or [Long] type, then it will be
 * serialized and stored as a JSON string using the [Json] module.
 *
 * A new [Json] instance is created for each serialized value. If the preference is saved frequently
 * and excessive object creation might be a concern, consider manual serialization and storing the
 * value as a [String].
 *
 * Note that [Double] values are stored as [Float], since [Preferences] do not support doubles. Please
 * add explicit cast to [Float] when storing [Double] values or wrap with a JSON-serializable object.
 * This method throws [GdxRuntimeException] if the value is a [Double] outside of the [Float] range.
 */
fun Preferences.set(pair: Pair<String, Any>): Preferences {
  val (key, value) = pair
  return when (value) {
    is String -> putString(key, value)
    is Boolean -> putBoolean(key, value)
    is Int -> putInteger(key, value)
    is Float -> putFloat(key, value)
    is Double -> {
      Gdx.app?.error("KTX", "Double $key:$value truncated to float.")
      return putFloat(key, value.asFloat())
    }
    is Long -> putLong(key, value)
    else -> putString(key, Json().toJson(value))
  }
}

/**
 * Retrieves a value of [T] type from the [Preferences] for the given [key].
 *
 * If the [T] type is not [String], [Boolean], [Int], [Float], [Double] or [Long] then it is assumed that
 * the value is stored as a JSON string and deserialized using the [Json] module.
 *
 * A new [Json] instance is created for each deserialized value. If the preference is read frequently
 * and excessive object creation might be a concern, consider manual deserialization and retrieving the
 * value as a [String].
 *
 * Note that [Preferences] do not support [Double] values. Instead, a [Float] is read from the [Preferences]
 * and converted to [Double]. Consider using a JSON-serializable object if you need to store [Double] values
 * with high precision.
 *
 * Will return `null` if the key is absent in the [Preferences].
 */
inline operator fun <reified T> Preferences.get(key: String): T? =
  if (key !in this) null else when (T::class) {
    String::class -> getString(key) as T
    Boolean::class -> getBoolean(key) as T
    Int::class -> getInteger(key) as T
    Float::class -> getFloat(key) as T
    Double::class -> getFloat(key).toDouble() as T
    Long::class -> getLong(key) as T
    else -> Json().fromJson(T::class.java, getString(key))
  }

/**
 * Retrieves a value of [T] type from the [Preferences] for the given [key] or returns the [defaultValue]
 * if the [key] is absent.
 *
 * If the [T] type is not [String], [Boolean], [Int], [Float], [Double] or [Long] then it is assumed that
 * the value is stored as a JSON string and deserialized using the [Json] module.
 *
 * A new [Json] instance is created for each deserialized value. If the preference is read frequently
 * and excessive object creation might be a concern, consider manual deserialization and retrieving the
 * value as a [String].
 *
 * Note that [Preferences] do not support [Double] values. Instead, a [Float] is read from the [Preferences]
 * and converted to [Double]. Consider using a JSON-serializable object if you need to store [Double] values
 * with high precision.
 */
inline operator fun <reified T> Preferences.get(key: String, defaultValue: T): T =
  if (key in this) this[key]!! else defaultValue

/**
 * Calls [Preferences.flush] after executing the given [operations].
 */
@OptIn(ExperimentalContracts::class)
inline fun Preferences.flush(operations: Preferences.() -> Unit) {
  contract { callsInPlace(operations, InvocationKind.EXACTLY_ONCE) }
  operations()
  flush()
}
