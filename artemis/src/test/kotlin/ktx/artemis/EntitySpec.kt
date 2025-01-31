package ktx.artemis

import com.artemis.ArchetypeBuilder
import com.artemis.World
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object EntitySpec : Spek({
  describe("utilities for entities") {
    val transform by memoized { Transform() }
    val world by memoized { World() }
    val entity by memoized {
      world.entity {
        add(transform)
      }
    }
    val entityEdit by memoized { world.edit(entity) }
    val transformMapper by memoized { world.getMapper(Transform::class.java) }
    val textureMapper by memoized { world.getMapper(Texture::class.java) }
    val archetype by memoized {
      ArchetypeBuilder()
        .add(Transform::class.java)
        .build(world)
    }

    describe("create entity function") {
      it("should add an entity to the World") {
        val entityId =
          world.entity {
            with<Transform> {
              x = 2f
            }
          }

        assertThat(transformMapper.get(entityId).x == 2f).isTrue()
      }
      it("should add an entity with an archetype to the world") {
        val entityId = world.entity(archetype)

        assertThat(transformMapper.get(entityId).x == 0f).isTrue()
      }
    }

    describe("edit entity function") {
      it("should add a component to an entity") {
        textureMapper.remove(entity)

        world.edit(entity) {
          with<Texture>()
        }

        assertThat(textureMapper.has(entity)).isTrue()
      }
      it("should replace a component of an entity") {
        world.entity()

        world.edit(entity) {
          with<Transform> {
            x = 2f
          }
        }

        assertThat(transformMapper.has(entity)).isTrue()
        assertThat(transformMapper.get(entity).x).isEqualTo(2f)
      }
      it("should configure component exactly once") {
        val variable: Int
        entityEdit.with<Transform> {
          variable = 42
        }
        assertThat(variable).isEqualTo(42)
      }
    }

    describe("plus assignment operator") {
      it("should add a component to an entity") {
        val entityId = entityEdit.entityId
        transformMapper.remove(entityId)

        entityEdit += Transform(1f, 0f)

        assertThat(transformMapper.has(entityId)).isTrue()
        assertThat(transformMapper.get(entityId).x).isEqualTo(1f)
      }

      it("should replace a component of an entity") {
        val entityId = entityEdit.entityId

        transformMapper.remove(entityId)

        entityEdit += Transform(1f, 0f)

        assertThat(transformMapper.has(entityId)).isTrue()
        assertThat(transformMapper.get(entityId).x).isEqualTo(1f)
      }
    }

    describe("remove component function") {
      it("should remove component") {
        assertThat(transformMapper.has(entity)).isTrue()

        world.edit(entity) {
          remove<Transform>()
        }
        assertThat(!transformMapper.has(entity)).isTrue()
      }
    }
  }
})
