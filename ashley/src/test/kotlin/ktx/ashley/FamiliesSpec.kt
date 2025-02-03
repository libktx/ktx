package ktx.ashley

import com.badlogic.ashley.core.Entity
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object FamiliesSpec : Spek({
  describe("utilities for component families") {
    val textureEntity =
      Entity().apply {
        add(Texture())
      }

    val rigidBodyEntity =
      Entity().apply {
        add(RigidBody())
      }

    val textureAndTransformEntity =
      Entity().apply {
        add(Texture())
        add(Transform())
      }

    val allComponentsEntity =
      Entity().apply {
        add(Texture())
        add(Transform())
        add(RigidBody())
      }

    it("should create a family that matches one of component") {
      val family = oneOf(Texture::class, Transform::class).get()
      assertThat(family.matches(textureEntity)).isTrue()
      assertThat(family.matches(rigidBodyEntity)).isFalse()
    }

    it("should create a family that matches all components") {
      val family = allOf(Texture::class, Transform::class).get()
      assertThat(family.matches(textureEntity)).isFalse()
      assertThat(family.matches(textureAndTransformEntity)).isTrue()
    }

    it("should create a family that matches any excluded components") {
      assertThat(exclude(Transform::class).get().matches(textureEntity)).isTrue()
      assertThat(exclude(Texture::class).get().matches(textureEntity)).isFalse()
    }

    describe("composite families") {
      it("should build a family chained with matching any of one component") {
        val family = exclude(Transform::class).oneOf(Texture::class, RigidBody::class)
        assertThat(family.get().matches(textureEntity)).isTrue()
        assertThat(family.get().matches(textureAndTransformEntity)).isFalse()
      }

      it("should build a family chained with matching all components") {
        val family = exclude(RigidBody::class).allOf(Texture::class, Transform::class)
        assertThat(family.get().matches(allComponentsEntity)).isFalse()
        assertThat(family.get().matches(textureAndTransformEntity)).isTrue()
      }

      it("should build a family chained with excluding components") {
        val family = oneOf(RigidBody::class).exclude(Texture::class, Transform::class)
        assertThat(family.get().matches(allComponentsEntity)).isFalse()
        assertThat(family.get().matches(rigidBodyEntity)).isTrue()
      }
    }
  }
})
