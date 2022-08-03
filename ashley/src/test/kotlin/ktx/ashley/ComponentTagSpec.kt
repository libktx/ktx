package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class TagComponent(val v: Int = 0) : Component
class ComponentWithoutDefault(val v: Int) : Component

var Entity.defaultConstructorTag by tagFor<TagComponent>()
var Entity.singletonTag by tagFor(TagComponent(1))
var Entity.providerTag by tagFor<TagComponent> { TagComponent(42) }
val Entity.readOnlyTag by tagFor<ComponentWithoutDefault>()

object ComponentTagSpec : Spek({

  describe("utilities for tag component properties") {
    val entity = Entity()

    it("Default ConstructorTag") {
      assertThat(entity.defaultConstructorTag).isFalse()
      entity.defaultConstructorTag = true
      assertThat(entity.defaultConstructorTag).isTrue()
      assertThat(entity.getComponent(TagComponent::class.java).v).isEqualTo(0)
      entity.defaultConstructorTag = false
      assertThat(entity.defaultConstructorTag).isFalse()
    }
    it("Singleton Tag") {
      assertThat(entity.singletonTag).isFalse()
      entity.singletonTag = true
      assertThat(entity.singletonTag).isTrue()
      assertThat(entity.getComponent(TagComponent::class.java).v).isEqualTo(1)
      entity.singletonTag = false
      assertThat(entity.singletonTag).isFalse()
    }
    it("Provider Tag") {
      assertThat(entity.providerTag).isFalse()
      entity.providerTag = true
      assertThat(entity.providerTag).isTrue()
      assertThat(entity.getComponent(TagComponent::class.java).v).isEqualTo(42)
      entity.providerTag = false
      assertThat(entity.providerTag).isFalse()
    }
    it("ReadOnly Tag") {
      assertThat(entity.readOnlyTag).isFalse()
      entity.add(ComponentWithoutDefault(1))
      assertThat(entity.readOnlyTag).isTrue()
      entity.remove(ComponentWithoutDefault::class.java)
      assertThat(entity.readOnlyTag).isFalse()
    }
  }
})
