package ktx.artemis

import com.artemis.ArchetypeBuilder
import com.artemis.World
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object ArchetypeSpec : Spek({
  describe("utilities for archetypes") {
    val world by memoized { World() }
    val archetypeBuilder by memoized { ArchetypeBuilder() }
    val transformMapper by memoized { world.getMapper(Transform::class.java) }
    val textureMapper by memoized { world.getMapper(Texture::class.java) }
    val positionMapper by memoized { world.getMapper(Position::class.java) }

    describe("add component function") {
      it("should add a component") {
        val archetype = archetypeBuilder
          .add<Transform>()
          .build(world)

        val entity = world.entity(archetype)

        assertThat(entity in transformMapper).isTrue()
      }
      it("should add multiple components") {
        val archetype = archetypeBuilder
          .add(
            Transform::class,
            Texture::class
          )
          .build(world)

        val entity = world.entity(archetype)

        assertThat(entity in transformMapper).isTrue()
        assertThat(entity in textureMapper).isTrue()
      }
    }

    describe("remove component function") {
      it("should remove a component") {
        val archetype = archetypeBuilder
          .add(
            Transform::class,
            Texture::class,
          )
          .remove<Texture>()
          .build(world)

        val entity = world.entity(archetype)

        assertThat(entity in transformMapper).isTrue()
        assertThat(entity !in textureMapper).isTrue()
      }
      it("should remove multiple components") {
        val archetype = archetypeBuilder
          .add(
            Position::class,
            Transform::class,
            Texture::class,
          )
          .remove(
            Transform::class,
            Texture::class
          )
          .build(world)

        val entity = world.entity(archetype)
        assertThat(entity in positionMapper).isTrue()
        assertThat(entity !in transformMapper).isTrue()
        assertThat(entity !in textureMapper).isTrue()
      }
    }
  }
})
