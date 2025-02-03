package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ComponentWithString(
  var string: String = "",
) : Component

class ComponentWithInt(
  var integer: Int = 0,
) : Component

var Entity.mandatoryComponent by propertyFor<ComponentWithString>()
var Entity.optionalComponent by optionalPropertyFor<ComponentWithInt>()

object ComponentPropertiesSpec : Spek({
  describe("utilities for component properties") {
    val mandatory = ComponentWithString()
    val optional = ComponentWithInt()
    val entity =
      Entity().apply {
        add(mandatory)
        add(optional)
      }
    it("should return a mandatory component value via property") {
      assertThat(entity.mandatoryComponent).isNotNull()
    }
    it("should return an optional component value via property") {
      assertThat(entity.optionalComponent).isNotNull()
    }
    it("should modify mandatory component variables via property") {
      entity.mandatoryComponent.string = "test"
      assertThat(mandatory.string).isEqualTo("test")
    }
    it("should modify optional component variables via property") {
      entity.optionalComponent?.integer = 42
      assertThat(optional.integer).isEqualTo(42)
    }
    it("should replace mandatory component instance via property") {
      entity.mandatoryComponent = ComponentWithString("new")
      assertThat(entity.mandatoryComponent.string).isEqualTo("new")
      assertThat(mandatory.string).isNotEqualTo(entity.mandatoryComponent.string)
      assertThat(entity.getComponent(ComponentWithString::class.java)).isNotSameAs(mandatory)
    }
    it("should replace optional component instance via property") {
      entity.optionalComponent = ComponentWithInt(11)
      assertThat(entity.optionalComponent?.integer).isEqualTo(11)
      assertThat(optional.integer).isNotEqualTo(entity.optionalComponent!!.integer)
      assertThat(entity.getComponent(ComponentWithInt::class.java)).isNotSameAs(optional)
    }
    it("should remove optional component via property") {
      entity.optionalComponent = null
      assertThat(entity.optionalComponent).isNull()
      assertThat(entity.getComponent(ComponentWithInt::class.java)).isNull()
    }
  }
})
