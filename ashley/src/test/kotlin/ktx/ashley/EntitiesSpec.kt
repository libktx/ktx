package ktx.ashley

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import org.assertj.core.api.Assertions.assertThat
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
    val engine by memoized {
      PooledEngine()
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

    describe("contains component function") {
      it("should return true if component exists") {
        assertThat(entity.contains(Transform.mapper)).isTrue()
      }
      it("should return false if component does not exists") {
        assertThat(entity.contains(Texture.mapper)).isFalse()
      }
      it("should return true as operator if component exists") {
        assertThat(Transform.mapper in entity).isTrue()
      }
      it("should return false as operator if component does not exists") {
        assertThat(Texture.mapper !in entity).isTrue()
      }
    }

    describe("plus assignment operator") {
      it("should add a component to an entity") {
        entity.remove<Transform>()

        entity += Transform(1f, 0f)

        assertThat(Transform.mapper in entity).isTrue()
        assertThat(entity[Transform.mapper]?.x).isEqualTo(1f)
      }

      it("should replace a component of an entity") {
        entity += Transform(1f, 0f)

        assertThat(entity[Transform.mapper]?.x).isEqualTo(1f)
        assertThat(entity[Transform.mapper]).isNotSameAs(transform)
      }
    }

    describe("add component function") {
      it("should add a component with a configuration and return it") {
        val component =
          entity.addComponent<Transform>(engine) {
            x = 3f
          }
        assertThat(Transform.mapper in entity).isTrue()
        assertThat(entity[Transform.mapper]).isEqualTo(component)
        assertThat(component.x).isEqualTo(3f)
      }
      it("should configure component exactly once") {
        val variable: Int
        entity.addComponent<Transform>(engine) {
          variable = 42
        }
        assertThat(variable).isEqualTo(42)
      }
    }
  }
})
