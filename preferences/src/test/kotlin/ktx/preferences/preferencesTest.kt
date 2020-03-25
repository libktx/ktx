package ktx.preferences

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.ObjectSet
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TestPreferences : Preferences {
  private val map = HashMap<String, Any>()
  var flushed = false

  override fun contains(key: String) = map.containsKey(key)

  override fun getBoolean(key: String) = map[key] as Boolean

  override fun getBoolean(key: String, defValue: Boolean) = map.getOrDefault(key, defValue) as Boolean

  override fun clear() = map.clear()

  override fun putLong(key: String, value: Long): Preferences {
    map[key] = value
    return this
  }

  override fun put(vals: MutableMap<String, *>): Preferences {
    vals.forEach { map[it.key] = it.value as Any }
    return this
  }

  override fun putInteger(key: String, value: Int): Preferences {
    map[key] = value
    return this
  }

  override fun remove(key: String) {
    map.remove(key)
  }

  override fun putBoolean(key: String, value: Boolean): Preferences {
    map[key] = value
    return this
  }

  override fun flush() {
    flushed = true
  }

  override fun getInteger(key: String) = map[key] as Int

  override fun getInteger(key: String, defValue: Int) = map.getOrDefault(key, defValue) as Int

  override fun getLong(key: String) = map[key] as Long

  override fun getLong(key: String, defValue: Long) = map.getOrDefault(key, defValue) as Long

  override fun getFloat(key: String) = map[key] as Float

  override fun getFloat(key: String, defValue: Float) = map.getOrDefault(key, defValue) as Float

  override fun putFloat(key: String, value: Float): Preferences {
    map[key] = value
    return this
  }

  override fun getString(key: String) = map[key] as String

  override fun getString(key: String, defValue: String) = map.getOrDefault(key, defValue) as String

  override fun get(): MutableMap<String, *> = map

  override fun putString(key: String, value: String): Preferences {
    map[key] = value
    return this
  }
}

class PreferencesTest {
  private lateinit var preferences: TestPreferences

  @Before
  fun `setup preferences`() {
    preferences = TestPreferences()
  }

  @Test
  fun `put string value`() {
    preferences["Key"] = "Value"

    Assert.assertTrue("Key" in preferences)
    Assert.assertEquals("Value", preferences.getString("Key"))
  }

  @Test
  fun `put boolean value`() {
    preferences["Key"] = true

    Assert.assertTrue("Key" in preferences)
    Assert.assertEquals(true, preferences.getBoolean("Key"))
  }

  @Test
  fun `put int value`() {
    preferences["Key"] = 1

    Assert.assertTrue("Key" in preferences)
    Assert.assertEquals(1, preferences.getInteger("Key"))
  }

  @Test
  fun `put float value`() {
    preferences["Key"] = 1f

    Assert.assertTrue("Key" in preferences)
    Assert.assertEquals(1f, preferences.getFloat("Key"))
  }

  @Test
  fun `put long value`() {
    preferences["Key"] = 1L

    Assert.assertTrue("Key" in preferences)
    Assert.assertEquals(1L, preferences.getLong("Key"))
  }

  @Test
  fun `put values as pairs`() {
    preferences.set("Key1" to "Value")
    preferences.set("Key2" to 1)

    Assert.assertEquals("Value", preferences.getString("Key1"))
    Assert.assertEquals(1, preferences.getInteger("Key2"))
  }

  @Test
  fun `put Any value`() {
    preferences["Key"] = ObjectSet<Any>()

    Assert.assertTrue("Key" in preferences)
    Assert.assertEquals(ObjectSet<Any>(), Json().fromJson(ObjectSet::class.java, preferences.getString("Key")))
  }

  @Test
  fun `get string value`() {
    preferences["Key"] = "Value"

    val result: String = preferences["Key"]

    Assert.assertEquals("Value", result)
  }

  @Test
  fun `get boolean value`() {
    preferences["Key"] = true

    val result: Boolean = preferences["Key"]

    Assert.assertEquals(true, result)
  }

  @Test
  fun `get int value`() {
    preferences["Key"] = 1

    val result: Int = preferences["Key"]

    Assert.assertEquals(1, result)
  }

  @Test
  fun `get float value`() {
    preferences["Key"] = 1f

    val result: Float = preferences["Key"]

    Assert.assertEquals(1f, result)
  }

  @Test
  fun `get long value`() {
    preferences["Key"] = 1L

    val result: Long = preferences["Key"]

    Assert.assertEquals(1L, result)
  }

  @Test
  fun `get Any value`() {
    preferences["Key"] = ObjectSet<Any>()

    val result: ObjectSet<Any> = preferences["Key"]

    Assert.assertEquals(ObjectSet<Any>(), result)
  }

  @Test
  fun `flush changes`() {
    preferences.flush {
      this["Key1"] = "Value1"
      this["Key2"] = 1
    }

    Assert.assertTrue(preferences.flushed)
    Assert.assertTrue("Key1" in preferences)
    Assert.assertTrue("Value1" == preferences.getString("Key1"))
    Assert.assertTrue("Key2" in preferences)
    Assert.assertTrue(1 == preferences.getInteger("Key2"))
  }
}
