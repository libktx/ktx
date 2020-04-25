package ktx.preferences

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Json
import org.junit.Assert.*
import org.junit.Test

/**
 * Mock-up [Preferences] implementation wrapping a [MutableMap].
 */
class TestPreferences : Preferences {
  private val map = mutableMapOf<String, Any>()
  var flushed = false

  override fun contains(key: String) = map.containsKey(key)
  override fun get(): MutableMap<String, *> = map
  override fun getBoolean(key: String) = map[key] as Boolean
  override fun getBoolean(key: String, defValue: Boolean) = map.getOrDefault(key, defValue) as Boolean
  override fun getInteger(key: String) = map[key] as Int
  override fun getInteger(key: String, defValue: Int) = map.getOrDefault(key, defValue) as Int
  override fun getLong(key: String) = map[key] as Long
  override fun getLong(key: String, defValue: Long) = map.getOrDefault(key, defValue) as Long
  override fun getFloat(key: String) = map[key] as Float
  override fun getFloat(key: String, defValue: Float) = map.getOrDefault(key, defValue) as Float
  override fun getString(key: String) = map[key] as String
  override fun getString(key: String, defValue: String) = map.getOrDefault(key, defValue) as String

  override fun put(vals: MutableMap<String, *>): Preferences {
    vals.forEach { map[it.key] = it.value as Any }
    return this
  }

  override fun putLong(key: String, value: Long): Preferences {
    map[key] = value
    return this
  }

  override fun putInteger(key: String, value: Int): Preferences {
    map[key] = value
    return this
  }

  override fun putBoolean(key: String, value: Boolean): Preferences {
    map[key] = value
    return this
  }

  override fun putFloat(key: String, value: Float): Preferences {
    map[key] = value
    return this
  }

  override fun putString(key: String, value: String): Preferences {
    map[key] = value
    return this
  }

  override fun remove(key: String) {
    map.remove(key)
  }

  override fun clear() = map.clear()
  override fun flush() {
    flushed = true
  }
}

/**
 * For JSON serialization tests.
 */
data class JsonSerializable(val data: String = "")

class PreferencesTest {
  private val preferences: Preferences = TestPreferences()

  @Test
  fun `should set string value`() {
    preferences["Key"] = "Value"

    assertTrue("Key" in preferences)
    assertEquals("Value", preferences.getString("Key"))
  }

  @Test
  fun `should set boolean value`() {
    preferences["Key"] = true

    assertTrue("Key" in preferences)
    assertEquals(true, preferences.getBoolean("Key"))
  }

  @Test
  fun `should set int value`() {
    preferences["Key"] = 1

    assertTrue("Key" in preferences)
    assertEquals(1, preferences.getInteger("Key"))
  }

  @Test
  fun `should set float value`() {
    preferences["Key"] = 1f

    assertTrue("Key" in preferences)
    assertEquals(1f, preferences.getFloat("Key"))
  }

  @Test
  @Suppress("DEPRECATION")
  fun `should set double value and cast to float`() {
    preferences["Key"] = 2.0

    assertTrue("Key" in preferences)
    assertEquals(2.0f, preferences.getFloat("Key"))
  }

  @Test(expected = GdxRuntimeException::class)
  @Suppress("DEPRECATION")
  fun `should throw an exception when setting double value outside of float positive range`() {
    preferences["Key"] = Double.MAX_VALUE
  }

  @Test(expected = GdxRuntimeException::class)
  @Suppress("DEPRECATION")
  fun `should throw an exception when setting double value outside of float negative range`() {
    preferences["Key"] = Double.MIN_VALUE
  }

  @Test
  fun `should set long value`() {
    preferences["Key"] = 1L

    assertTrue("Key" in preferences)
    assertEquals(1L, preferences.getLong("Key"))
  }

  @Test
  fun `should set values as pairs`() {
    preferences.set("Key1" to "Value")
    preferences.set("Key2" to 1)
    preferences.set("Key3" to 2L)
    preferences.set("Key4" to 3.0f)
    preferences.set("Key5" to 4.0)
    preferences.set("Key6" to true)
    preferences.set("Key7" to JsonSerializable("test"))

    assertEquals("Value", preferences.getString("Key1"))
    assertEquals(1, preferences.getInteger("Key2"))
    assertEquals(2L, preferences.getLong("Key3"))
    assertEquals(3.0f, preferences.getFloat("Key4"))
    assertEquals(4.0f, preferences.getFloat("Key5"))
    assertEquals(true, preferences.getBoolean("Key6"))
    assertEquals(
      JsonSerializable("test"),
      Json().fromJson(JsonSerializable::class.java, preferences.getString("Key7"))
    )
  }

  @Test(expected = GdxRuntimeException::class)
  fun `should throw an exception when setting double value outside of float range as pair`() {
    preferences.set("Key" to Double.MAX_VALUE)
  }

  @Test
  fun `should set any value`() {
    preferences["Key"] = JsonSerializable("test")

    assertTrue("Key" in preferences)
    assertEquals(
      JsonSerializable("test"),
      Json().fromJson(JsonSerializable::class.java, preferences.getString("Key"))
    )
  }

  @Test
  fun `should get string value`() {
    preferences["Key"] = "Value"

    val result: String? = preferences["Key"]

    assertEquals("Value", result)
  }

  @Test
  fun `should get boolean value`() {
    preferences["Key"] = true

    val result: Boolean? = preferences["Key"]

    assertEquals(true, result)
  }

  @Test
  fun `should get int value`() {
    preferences["Key"] = 1

    val result: Int? = preferences["Key"]

    assertEquals(1, result)
  }

  @Test
  fun `should get float value`() {
    preferences["Key"] = 1f

    val result: Float? = preferences["Key"]

    assertEquals(1f, result)
  }

  @Test
  fun `should get long value`() {
    preferences["Key"] = 1L

    val result: Long? = preferences["Key"]

    assertEquals(1L, result)
  }

  @Test
  fun `should get any value`() {
    preferences["Key"] = JsonSerializable("test")

    val result: JsonSerializable? = preferences["Key"]

    assertEquals(JsonSerializable("test"), result)
  }

  @Test
  fun `should get missing string value`() {
    val result: String? = preferences["Key"]

    assertNull(result)
  }

  @Test
  fun `should get missing boolean value`() {
    val result: Boolean? = preferences["Key"]

    assertNull(result)
  }

  @Test
  fun `should get missing int value`() {
    val result: Int? = preferences["Key"]

    assertNull(result)
  }

  @Test
  fun `should get missing float value`() {
    val result: Float? = preferences["Key"]

    assertNull(result)
  }

  @Test
  fun `should get missing long value`() {
    val result: Long? = preferences["Key"]

    assertNull(result)
  }

  @Test
  fun `should get missing object value`() {
    val result: JsonSerializable? = preferences["Key"]

    assertNull(result)
  }

  @Test
  fun `should get string with default value`() {
    preferences["Key"] = "Value"

    val result: String = preferences["Key", "default"]

    assertEquals("Value", result)
  }

  @Test
  fun `should get boolean with default value`() {
    preferences["Key"] = true

    val result: Boolean = preferences["Key", false]

    assertEquals(true, result)
  }

  @Test
  fun `should get int with default value`() {
    preferences["Key"] = 1

    val result: Int = preferences["Key", 0]

    assertEquals(1, result)
  }

  @Test
  fun `should get float with default value`() {
    preferences["Key"] = 1f

    val result: Float = preferences["Key", 0f]

    assertEquals(1f, result)
  }

  @Test
  fun `should get long with default value`() {
    preferences["Key"] = 1L

    val result: Long = preferences["Key", 0L]

    assertEquals(1L, result)
  }

  @Test
  fun `should get any object with default value`() {
    preferences["Key"] = JsonSerializable("test")

    val result: JsonSerializable = preferences["Key", JsonSerializable("")]

    assertEquals(JsonSerializable("test"), result)
  }

  @Test
  fun `should get missing string with default value`() {
    val result: String = preferences["Key", "default"]

    assertEquals("default", result)
  }

  @Test
  fun `should get missing boolean with default value`() {
    val result: Boolean = preferences["Key", true]

    assertTrue(result)
  }

  @Test
  fun `should get missing int with default value`() {
    val result: Int = preferences["Key", 42]

    assertEquals(42, result)
  }

  @Test
  fun `should get missing float with default value`() {
    val result: Float = preferences["Key", 4.2f]

    assertEquals(4.2f, result)
  }

  @Test
  fun `should get missing long with default value`() {
    val result: Long = preferences["Key", 42L]

    assertEquals(42L, result)
  }

  @Test
  fun `should get missing object with default value`() {
    val result: JsonSerializable = preferences["Key", JsonSerializable("Test")]

    assertEquals(JsonSerializable("Test"), result)
  }

  @Test
  fun `should flush changes after executing passed operations`() {
    var wasExecuted: Boolean
    var wasFlushedDuringExecution: Boolean
    preferences.flush {
      wasExecuted = true
      wasFlushedDuringExecution = (preferences as TestPreferences).flushed
    }

    assertTrue((preferences as TestPreferences).flushed)
    assertTrue(wasExecuted)
    assertFalse(wasFlushedDuringExecution)
  }

  @Test
  fun `should perform operations on flushing exactly once`() {
    val variable: Int

    preferences.flush {
      variable = 42
    }

    assertEquals(42, variable)
  }
}
