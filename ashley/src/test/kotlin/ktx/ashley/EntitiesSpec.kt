package ktx.ashley

import com.badlogic.ashley.core.Entity
import org.assertj.core.api.Assertions.*
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object EntitiesSpec : Spek({
  describe("utilities for entities") {
    val transform by memoized {
      Transform()
    }
    val entity by memoized {
      Entity().apply {
        add(transform)
      }
    }
    describe("get operator") {
      it("should get component with component mapper for better performance") {
        assertThat(entity[Transform.mapper]).isSameAs(transform)
      }
      it("should return null if there is no component") {
        assertThat(entity[Texture.mapper]).isNull()
      }
    }

    describe("get component function") {
      it("should get component by reified type") {
        assertThat(entity.get<Transform>()).isSameAs(transform)
      }
      it("should return null if there is no component") {
        assertThat(entity.get<Texture>()).isNull()
      }
    }

    describe("remove component function") {
      it("should remove a component by reified type") {
        val component = entity.remove<Transform>()

        assertThat(entity.getComponent(Transform::class.java)).isNull()
        assertThat(component).isEqualTo(transform)
      }
      it("should fail to remove an absent component by reified type") {
        val component = entity.remove<Texture>()

        assertThat(entity.getComponent(Texture::class.java)).isNull()
        assertThat(component).isNull()
      }
    }

    describe("has component function") {
      it("should return true if exists") {
        assertThat(entity.has(Transform.mapper)).isTrue()
      }
      it("should return false if does not exists") {
        assertThat(entity.has(Texture.mapper)).isFalse()
      }
    }

    describe("has not component function") {
      it("should return false if exists") {
        assertThat(entity.hasNot(Transform.mapper)).isFalse()
      }
      it("should return true if does not exists") {
        assertThat(entity.hasNot(Texture.mapper)).isTrue()
      }
    }
  }
})
