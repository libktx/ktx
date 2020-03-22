package ktx.preferences

import com.badlogic.gdx.Preferences

operator fun Preferences.set(key: String, value: String): Preferences = this.putString(key, value)

operator fun Preferences.set(key: String, value: Boolean): Preferences = this.putBoolean(key, value)

operator fun Preferences.set(key: String, value: Int): Preferences = this.putInteger(key, value)

operator fun Preferences.set(key: String, value: Long): Preferences = this.putLong(key, value)

operator fun Preferences.set(key: String, value: Float): Preferences = this.putFloat(key, value)

fun Preferences.flush(operations: Preferences.() -> Unit) {
  this.operations()
  this.flush()
}
