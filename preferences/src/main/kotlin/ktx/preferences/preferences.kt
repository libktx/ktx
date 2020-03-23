package ktx.preferences

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.GdxRuntimeException

inline operator fun <reified T> Preferences.set(key: String, value: T): Preferences {
  when (value) {
    is String -> this.putString(key, value as String)
    is Boolean -> this.putBoolean(key, value as Boolean)
    is Int -> this.putInteger(key, value as Int)
    is Float -> this.putFloat(key, value as Float)
    is Long -> this.putLong(key, value as Long)
    else -> throw GdxRuntimeException("Unsupported ${T::class}")
  }
  return this
}

inline fun <reified T> Preferences.set(pair: Pair<String, T>) {
  when (pair.second) {
    is String -> this.putString(pair.first, pair.second as String)
    is Boolean -> this.putBoolean(pair.first, pair.second as Boolean)
    is Int -> this.putInteger(pair.first, pair.second as Int)
    is Float -> this.putFloat(pair.first, pair.second as Float)
    is Long -> this.putLong(pair.first, pair.second as Long)
    else -> throw GdxRuntimeException("Unsupported ${T::class}")
  }
}

inline operator fun <reified T> Preferences.get(key: String): T {
  return when (T::class) {
    String::class -> this.getString(key) as T
    Boolean::class -> this.getBoolean(key) as T
    Int::class -> this.getInteger(key) as T
    Float::class -> this.getFloat(key) as T
    Long::class -> this.getLong(key) as T
    else -> throw GdxRuntimeException("Unsupported ${T::class}")
  }
}

inline fun Preferences.flush(operations: Preferences.() -> Unit) {
  this.operations()
  this.flush()
}
