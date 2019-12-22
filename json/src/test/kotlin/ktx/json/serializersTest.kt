package ktx.json

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.matchers.shouldThrow
import org.junit.Test

/**
 * Guarantees that [write] method is optional to implement.
 */
@Suppress("unused", "ClassName")
class `should implement ReadOnlyJsonSerializer with no 'write' method overridden`<T> : ReadOnlyJsonSerializer<T> {
  override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): T = throw NotImplementedError()
}

class ReadOnlyJsonSerializerTest {
  @Test
  fun `default implementation for 'write' method should throw UnsupportedOperationException`() {
    val readOnlyJsonSerializer = object : ReadOnlyJsonSerializer<Any> {
      override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): Any = throw NotImplementedError()
    }

    shouldThrow<UnsupportedOperationException> {
      readOnlyJsonSerializer.write(Json(), JsonValue(JsonValue.ValueType.`object`), Any::class.java)
    }
  }
}

class ReadOnlyJsonSerializerFactoryTest {
  @Test
  fun `should return readonly serializer that calls lambda function with 3 parameters`() {
    val json = Json()
    val jsonValue = JsonValue(JsonValue.ValueType.`object`)
    val readFunction: (Json, JsonValue, Class<*>?) -> Data = mock()

    readOnlySerializer(readFunction).read(json, jsonValue, Data::class.java)

    verify(readFunction).invoke(json, jsonValue, Data::class.java)
  }

  @Test
  fun `should return readonly serializer that returns result of calling lambda with 3 parameters`() {
    val data = Data(intField = 10, stringField = "Test", doubleField = 11.254)
    val jsonData = JsonValue(JsonValue.ValueType.`object`)
    val readFunction: (Json, JsonValue, Class<*>?) -> Data = { _, _, _ -> data }

    val value = readOnlySerializer(readFunction).read(Json(), jsonData, Data::class.java)

    value shouldEqual data
  }

  @Test
  fun `should return readonly serializer that calls lambda function with 2 parameters`() {
    val jsonData = JsonValue(JsonValue.ValueType.`object`)
    val readFunction: (JsonValue) -> Data = mock()

    readOnlySerializer(readFunction).read(Json(), jsonData, Data::class.java)

    verify(readFunction).invoke(jsonData)
  }

  @Test
  fun `should return readonly serializer that returns result of calling lambda with 2 parameters`() {
    val data = Data(intField = 10, stringField = "Test", doubleField = 11.254)
    val jsonData = JsonValue(JsonValue.ValueType.`object`)
    val readFunction: (JsonValue) -> Data = { data }

    val value = readOnlySerializer(readFunction).read(Json(), jsonData, Data::class.java)

    value shouldEqual data
  }

  data class Data(val intField: Int, val stringField: String, val doubleField: Double)
}
