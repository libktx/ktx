package ktx.preferences

import com.badlogic.gdx.Preferences
import com.badlogic.gdx.utils.ObjectMap
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class PreferencesTest {
  private lateinit var preferences: Preferences
  private val map = ObjectMap<String, Any>()

  @Before
  fun `setup preferences`() {
    preferences = Mockito.mock(Preferences::class.java)
    Mockito.`when`(preferences.contains(any())).then { invocation ->
      return@then map.containsKey(invocation.getArgument(0))
    }
    // string
    Mockito.`when`(preferences.putString(any(), any())).then { invocation ->
      map.put(invocation.getArgument(0), invocation.getArgument(1))
      return@then preferences
    }
    Mockito.`when`(preferences.getString(any())).then { invocation ->
      return@then map.get(invocation.getArgument(0))
    }
    // boolean
    Mockito.`when`(preferences.putBoolean(any(), any())).then { invocation ->
      map.put(invocation.getArgument(0), invocation.getArgument(1))
      return@then preferences
    }
    Mockito.`when`(preferences.getBoolean(any())).then { invocation ->
      return@then map.get(invocation.getArgument(0))
    }
    // integer
    Mockito.`when`(preferences.putInteger(any(), any())).then { invocation ->
      map.put(invocation.getArgument(0), invocation.getArgument(1))
      return@then preferences
    }
    Mockito.`when`(preferences.getInteger(any())).then { invocation ->
      return@then map.get(invocation.getArgument(0))
    }
    // float
    Mockito.`when`(preferences.putFloat(any(), any())).then { invocation ->
      map.put(invocation.getArgument(0), invocation.getArgument(1))
      return@then preferences
    }
    Mockito.`when`(preferences.getFloat(any())).then { invocation ->
      return@then map.get(invocation.getArgument(0))
    }
    // long
    Mockito.`when`(preferences.putLong(any(), any())).then { invocation ->
      map.put(invocation.getArgument(0), invocation.getArgument(1))
      return@then preferences
    }
    Mockito.`when`(preferences.getLong(any())).then { invocation ->
      return@then map.get(invocation.getArgument(0))
    }
  }

  @Test
  fun `put string value`() {
    preferences["Key"] = "Value"

    Assert.assertTrue("Key" in preferences)
    Assert.assertTrue("Value" == preferences.getString("Key"))
  }

  @Test
  fun `put boolean value`() {
    preferences["Key"] = true

    Assert.assertTrue("Key" in preferences)
    Assert.assertTrue(preferences.getBoolean("Key"))
  }

  @Test
  fun `put int value`() {
    preferences["Key"] = 1

    Assert.assertTrue("Key" in preferences)
    Assert.assertTrue(1 == preferences.getInteger("Key"))
  }

  @Test
  fun `put float value`() {
    preferences["Key"] = 1f

    Assert.assertTrue("Key" in preferences)
    Assert.assertTrue(1f == preferences.getFloat("Key"))
  }

  @Test
  fun `put long value`() {
    preferences["Key"] = 1L

    Assert.assertTrue("Key" in preferences)
    Assert.assertTrue(1L == preferences.getLong("Key"))
  }

  @Test
  fun `flush changes`() {
    preferences.flush {
      this["Key1"] = "Value1"
      this["Key2"] = 1
    }

    verify(preferences, times(1)).flush()
    Assert.assertTrue("Key1" in preferences)
    Assert.assertTrue("Value1" == preferences.getString("Key1"))
    Assert.assertTrue("Key2" in preferences)
    Assert.assertTrue(1 == preferences.getInteger("Key2"))
  }
}
