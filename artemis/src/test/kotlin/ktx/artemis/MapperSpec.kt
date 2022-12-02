package ktx.artemis

import com.artemis.ComponentMapper
import com.artemis.World
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object MapperSpec : Spek({
  describe("utilities for mappers") {
    val world by memoized { World() }
    val transform by memoized { Transform() }
    val entity by memoized {
      world.entity {
        add(transform)
      }
    }
    val transformMapper by memoized { world.getMapper(Transform::class.java) }
    val textureMapper by memoized { world.getMapper(Texture::class.java) }

    describe("contains component function") {
      it("should return true if component exists") {
        assertThat(transformMapper.contains(entity)).isTrue()
      }
      it("should return false if component does not exists") {
        assertThat(textureMapper.contains(entity)).isFalse()
      }
      it("should return true with operator if component exists") {
        assertThat(entity in transformMapper).isTrue()
      }
      it("should return false with operator if component does not exists") {
        assertThat(entity !in textureMapper).isTrue()
      }
    }
    describe("getMapper function") {
      it("should return a mapper") {
        val positionMapper = world.mapperFor<Position>()
        positionMapper.create(entity)

        assertThat(positionMapper.javaClass == ComponentMapper::class.java).isTrue()
        assertThat(positionMapper.has(entity)).isTrue()
      }
    }
  }
})
