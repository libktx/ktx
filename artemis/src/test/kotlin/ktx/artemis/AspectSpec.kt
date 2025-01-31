package ktx.artemis

import com.artemis.World
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object AspectSpec : Spek({
  describe("utilities for aspects") {
    val world by memoized { World() }
    val textureEntity by memoized {
      world.createEntity().apply {
        edit().add(Texture())
      }
    }
    val rigidBodyEntity by memoized {
      world.createEntity().apply {
        edit().add(RigidBody())
      }
    }

    val textureAndTransformEntity by memoized {
      world.createEntity().apply {
        edit()
          .add(Texture())
          .add(Transform())
      }
    }

    val threeComponentsEntity by memoized {
      world.createEntity().apply {
        edit()
          .add(Texture())
          .add(Transform())
          .add(RigidBody())
      }
    }

    it("should create an aspect that matches one of the components") {
      val aspect = oneOf(Texture::class, Transform::class).build(world)

      assertThat(aspect.isInterested(textureEntity)).isTrue()
      assertThat(aspect.isInterested(textureAndTransformEntity)).isTrue()
      assertThat(aspect.isInterested(rigidBodyEntity)).isFalse()
    }
    it("should create an aspect that matches all the components") {
      val aspect = allOf(Texture::class, Transform::class).build(world)

      assertThat(aspect.isInterested(textureEntity)).isFalse()
      assertThat(aspect.isInterested(textureAndTransformEntity)).isTrue()
    }
    it("should create an aspect that excludes components") {
      assertThat(exclude(Transform::class).build(world).isInterested(textureEntity)).isTrue()
      assertThat(exclude(Texture::class).build(world).isInterested(textureEntity)).isFalse()
    }

    it("should create an aspect that matches all, one of and excludes components") {
      val aspect =
        exclude(Remove::class)
          .oneOf(Position::class, RigidBody::class)
          .allOf(Texture::class, Transform::class)
          .exclude(Dead::class)
          .build(world)

      assertThat(aspect.isInterested(rigidBodyEntity)).isFalse()
      assertThat(aspect.isInterested(threeComponentsEntity)).isTrue()

      threeComponentsEntity.edit().add(Dead())

      assertThat(aspect.isInterested(threeComponentsEntity)).isFalse()
    }
  }
})
