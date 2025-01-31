package ktx.artemis

import com.artemis.EntityTransmuterFactory
import com.artemis.World
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object TransmuterSpec : Spek({
  describe("utilities for transmuters") {
    val world by memoized { World() }
    val transmuterFactory by memoized { EntityTransmuterFactory(world) }
    val transformMapper by memoized { world.getMapper(Transform::class.java) }
    val textureMapper by memoized { world.getMapper(Texture::class.java) }
    val entity by memoized {
      world.entity {
        add(Transform())
      }
    }

    describe("add component function") {
      it("should add a component to an entity") {
        val transmuter =
          transmuterFactory
            .add<Texture>()
            .build()

        transmuter.transmute(entity)

        assertThat(textureMapper.has(entity)).isTrue()
      }
    }
    describe("remove component function") {
      it("should remove a component from an entity") {
        val transmuter =
          transmuterFactory
            .add<Texture>()
            .remove<Transform>()
            .build()

        transmuter.transmute(entity)

        assertThat(textureMapper.has(entity)).isTrue()
        assertThat(!transformMapper.has(entity)).isTrue()
      }
    }
  }
})
