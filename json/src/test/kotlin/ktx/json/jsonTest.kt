package ktx.json

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import org.junit.Test

/**
 * Tests [Json] extensions.
 */
class StyleTest {

  @Test
  fun `should read simple object from string`() {
    val json = Json()
    val simple = json.fromJson<Simple>(
        """{int:10,bool:true,str:"Hello world"}""")

    simple.int shouldBe 10
    simple.bool shouldBe true
    simple.str shouldBe "Hello world"
  }

  @Test
  fun `should read complex object from string`() {
    val json = Json()
    val complex = json.fromJson<Complex>(
        """{bool:true,simple:{int:31,bool:true,str:"a"},list:[1,1,2,3,5,8,13]}""")

    complex.bool shouldBe true
    complex.simple.int shouldBe 31
    complex.simple.bool shouldBe true
    complex.simple.str shouldBe "a"
    complex.list shouldBe listOf(1, 1, 2, 3, 5, 8, 13)
  }

  @Test
  fun `should set and return tags`() {
    val json = Json()
    json.addClassTag<Simple>("simple")
    json.addClassTag<Complex>("complex")

    json.getTag<Simple>() shouldBe "simple"
    json.getTag<Complex>() shouldBe "complex"
  }

  @Test
  fun `should return null tag if unset`() {
    val json = Json()
    json.getTag<Simple>() shouldBe null
    json.getTag<Complex>() shouldBe null
  }

  @Test
  fun `should recreate same object`() {
    val custom1 = Custom().apply {
      float = 100f
      simple = Simple().apply { int = 1 }
      list = List(3) { Simple().apply { int = it } }
    }

    val json = Json()
    val custom2 = json.fromJson<Custom>(json.toJson(custom1))

    custom2.float shouldEqual custom1.float
    custom2.simple.int shouldEqual custom1.simple.int
    custom2.list.size shouldEqual custom1.list.size
    for (i in 0 until custom1.list.size) {
      custom2.list[i].int shouldEqual custom1.list[i].int
    }
  }

  private class Simple {
    var int = 0
    var bool = false
    var str = ""
  }

  private class Complex {
    var bool = false
    lateinit var simple: Simple
    lateinit var list: List<Int>
  }

  private class Custom : Json.Serializable {
    var float = 0f
    lateinit var simple: Simple
    lateinit var list: List<Simple>

    override fun read(json: Json, jsonData: JsonValue) {
      float = json.readValue("float", jsonData)
      simple = json.readValue("simple", jsonData)
      list = json.readArrayValue("list", jsonData)
    }

    override fun write(json: Json) {
      json.writeValue("float", float)
      json.writeValue("simple", simple)
      json.writeValue("list", list)
    }
  }

}