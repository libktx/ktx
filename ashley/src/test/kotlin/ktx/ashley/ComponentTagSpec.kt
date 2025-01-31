package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class FlagComponent : Component

class NoDefaultArgConstructorComponent(
  val text: String,
) : Component

var Entity.singletonTagCreatedByReflection by tagFor<FlagComponent>()
var Entity.providerTagCreatedByReflection by tagFor<FlagComponent>(singleton = false)
var Entity.singletonTagCreatedWithProvider by tagFor<FlagComponent> { FlagComponent() }
var Entity.providerTagCreatedWithProvider by tagFor(singleton = false) { FlagComponent() }
var Entity.singletonTagCreatedWithInstance by tagFor(FlagComponent())
val Entity.readOnlyTag by tagFor<NoDefaultArgConstructorComponent>()

object ComponentTagSpec : Spek({
  describe("utilities tag component delegates") {
    val entity = Entity()
    val mapper = mapperFor<FlagComponent>()

    it("should detect if component is not defined with singleton tag created by reflection") {
      assertThat(entity.singletonTagCreatedByReflection).isFalse()
    }
    it("should detect if component is defined with singleton tag created by reflection") {
      entity.add(FlagComponent())
      assertThat(entity.singletonTagCreatedByReflection).isTrue()
    }
    it("should add component with singleton tag created by reflection") {
      entity.singletonTagCreatedByReflection = true
      assertThat(entity.has(mapper)).isTrue()
    }
    it("should remove component with singleton tag created by reflection") {
      entity.add(FlagComponent())
      entity.singletonTagCreatedByReflection = false
      assertThat(entity.has(mapper)).isFalse()
    }

    it("should detect if component is not defined with provider tag created by reflection") {
      assertThat(entity.providerTagCreatedByReflection).isFalse()
    }
    it("should detect if component is defined with provider tag created by reflection") {
      entity.add(FlagComponent())
      assertThat(entity.providerTagCreatedByReflection).isTrue()
    }
    it("should add component with provider tag created by reflection") {
      entity.providerTagCreatedByReflection = true
      assertThat(entity.has(mapper)).isTrue()
    }
    it("should remove component with provider tag created by reflection") {
      entity.add(FlagComponent())
      entity.providerTagCreatedByReflection = false
      assertThat(entity.has(mapper)).isFalse()
    }

    it("should detect if component is not defined with singleton tag created with provider") {
      assertThat(entity.singletonTagCreatedWithProvider).isFalse()
    }
    it("should detect if component is defined with singleton tag created with provider") {
      entity.add(FlagComponent())
      assertThat(entity.singletonTagCreatedWithProvider).isTrue()
    }
    it("should add component with singleton tag created with provider") {
      entity.singletonTagCreatedWithProvider = true
      assertThat(entity.has(mapper)).isTrue()
    }
    it("should remove component with singleton tag created with provider") {
      entity.add(FlagComponent())
      entity.singletonTagCreatedWithProvider = false
      assertThat(entity.has(mapper)).isFalse()
    }

    it("should detect if component is not defined with provider tag created with provider") {
      assertThat(entity.providerTagCreatedWithProvider).isFalse()
    }
    it("should detect if component is defined with provider tag created with provider") {
      entity.add(FlagComponent())
      assertThat(entity.providerTagCreatedWithProvider).isTrue()
    }
    it("should add component with provider tag created with provider") {
      entity.providerTagCreatedWithProvider = true
      assertThat(entity.has(mapper)).isTrue()
    }
    it("should remove component with provider tag created with provider") {
      entity.add(FlagComponent())
      entity.providerTagCreatedWithProvider = false
      assertThat(entity.has(mapper)).isFalse()
    }

    it("should detect if component is not defined with singleton tag created with instance") {
      assertThat(entity.singletonTagCreatedWithInstance).isFalse()
    }
    it("should detect if component is defined with singleton tag created with instance") {
      entity.add(FlagComponent())
      assertThat(entity.singletonTagCreatedWithInstance).isTrue()
    }
    it("should add component with singleton tag created with instance") {
      entity.singletonTagCreatedWithInstance = true
      assertThat(entity.has(mapper)).isTrue()
    }
    it("should remove component with singleton tag created with instance") {
      entity.add(FlagComponent())
      entity.singletonTagCreatedWithInstance = false
      assertThat(entity.has(mapper)).isFalse()
    }

    it("should support read-only tags with no default argument constructors") {
      assertThat(entity.readOnlyTag).isFalse()
      entity.add(NoDefaultArgConstructorComponent("test"))
      assertThat(entity.readOnlyTag).isTrue()
    }
  }
})
