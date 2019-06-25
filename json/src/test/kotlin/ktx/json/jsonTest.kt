package ktx.json

import com.badlogic.gdx.backends.lwjgl.LwjglFiles
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import io.kotlintest.matchers.shouldEqual
import org.junit.Assert.assertNotSame
import org.junit.Test

/**
 * Tests [Json] extensions.
 */
class JsonTest {
  @Test
  fun `should read simple object from string`() {
    // Given:
    val json = Json()

    // When:
    val simple = json.fromJson<Simple>("""{
      "int": 10,
      "bool": true,
      "str": "Hello world"
    }""")

    // Then:
    simple shouldEqual Simple(
        int = 10,
        bool = true,
        str = "Hello world"
    )
  }

  @Test
  fun `should read complex object from string`() {
    // Given:
    val json = Json()

    // When:
    val complex = json.fromJson<Complex>("""{
      "bool": true,
      "simple": {
        "int": 31,
        "bool": true,
        "str": "a"
      },
      "list": [1, 1, 2, 3, 5, 8, 13]
    }""")

    // Then:
    complex shouldEqual Complex(
        bool = true,
        simple = Simple(
            int = 31,
            bool = true,
            str = "a"
        ),
        list = listOf(1, 1, 2, 3, 5, 8, 13)
    )
  }

  @Test
  fun `should read object from file`() {
    // Given:
    val json = Json()
    val fileHandle = LwjglFiles().classpath("ktx/json/file.json")

    // When:
    val simple = json.fromJson<Simple>(fileHandle)

    // Then:
    simple shouldEqual Simple(
        int = 10,
        bool = true,
        str = "test"
    )
  }

  @Test
  fun `should set tags`() {
    // Given:
    val json = Json()

    // When:
    json.addClassTag<Simple>("simple")

    // Then:
    json.getTag<Simple>() shouldEqual "simple"
  }

  @Test
  fun `should return null tag if unset`() {
    // Given:
    val json = Json()

    // When:
    val tag = json.getTag<Simple>()

    // Then:
    tag shouldEqual null
  }

  @Test
  fun `should recreate object with equal properties`() {
    // Given:
    val json = Json()
    val serialized = Custom().apply {
      float = 100f
      simple = Simple().apply { int = 1 }
      list = List(3) { Simple().apply { int = it } }
    }

    // When:
    val deserialized = json.fromJson<Custom>(json.toJson(serialized))

    // Then:
    serialized shouldEqual deserialized
    assertNotSame(serialized, deserialized)
    assertNotSame(serialized.simple, deserialized.simple)
  }

  @Test
  fun `should read and write with custom serializer`() {
    // Given:
    val json = Json()
    val simple = Simple(int = 12, str = "b")

    // When:
    json.setSerializer(object : Json.Serializer<Simple> {
      override fun read(json: Json, jsonData: JsonValue, type: Class<*>?): Simple {
        val deserialized = Simple()
        deserialized.int = jsonData.getInt("integer")
        deserialized.str = jsonData.getString("string")
        return deserialized
      }

      override fun write(json: Json, obj: Simple, knownType: Class<*>?) {
        json.writeObjectStart()
        json.writeValue("integer", obj.int)
        json.writeValue("string", obj.str)
        json.writeObjectEnd()
      }
    })

    // Then:
    json.toJson(simple) shouldEqual "{integer:12,string:b}"
    json.fromJson<Simple>("""{"integer":31,"string":"c"}""") shouldEqual Simple(int = 31, str = "c")
  }

  @Test
  fun `should read non-object values`() {
    // Given:
    val json = Json()

    // Expect:
    json.readValue<String>(JsonReader().parse("str")) shouldEqual "str"
    json.readArrayValue<ArrayList<Int>, Int>(JsonReader().parse("[1,2,3,4,5,6]")) shouldEqual arrayListOf(
        1, 2, 3, 4, 5, 6
    )
  }

  @Test
  fun `should set element type of nested collection`() {
    // Given:
    val json = Json()
    json.setElementType<ListContainer, Simple>("list")

    // When:
    val container = json.fromJson<ListContainer>("""{
      "list": [
        {
          "bool": true,
          "int": 42
        },
        {
          "str": "yes"
        }
      ]
    }""")

    // Then:
    container shouldEqual ListContainer(listOf(
        Simple(bool = true, int = 42),
        Simple(str = "yes")
    ))
  }

  /**
   * Serializable class included for tests.
   */
  private data class Simple(
      var int: Int = 0,
      var bool: Boolean = false,
      var str: String = ""
  )

  /**
   * Serializable class included for tests.
   */
  private data class Complex(
      var bool: Boolean = false,
      var simple: Simple = Simple(),
      var list: List<Int> = emptyList()
  )

  /**
   * Serializable class with a list of [Simple] objects.
   */
  private data class ListContainer(
      var list: List<Simple> = emptyList()
  )

  /**
   * Class included for tests of custom serialization.
   */
  private data class Custom(
      var float: Float = 0f,
      var simple: Simple = Simple(),
      var list: List<Simple> = emptyList()) : Json.Serializable {

    override fun read(json: Json, jsonData: JsonValue) {
      float = json.readValue(jsonData, "float")
      simple = json.readValue(jsonData, "simple")
      list = json.readArrayValue(jsonData, "list")
    }

    override fun write(json: Json) {
      json.writeValue("float", float)
      json.writeValue("simple", simple)
      json.writeValue("list", list)
    }
  }
}
