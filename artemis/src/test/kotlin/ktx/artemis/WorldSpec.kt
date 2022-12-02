package ktx.artemis

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object WorldSpec : Spek({
  describe("utilities for world") {
    val worldConfig by memoized {
      WorldConfigurationBuilder().build()
    }

    describe("getSystem function") {
      it("should add a system and return it") {
        val system = TestSystem()
        worldConfig.setSystem(system)
        val world = World(worldConfig)

        assertThat(world.getSystem<TestSystem>()).isEqualTo(system)
      }
      it("should add a system and return it with operator function") {
        val system = TestSystem()
        worldConfig.setSystem(system)
        val world = World(worldConfig)

        assertThat(world[TestSystem::class]).isEqualTo(system)
      }
      it("should throw an exception if the system doesn't exist in the world") {
        assertThatExceptionOfType(MissingBaseSystemException::class.java).isThrownBy {
          val world = World(worldConfig)

          world.getSystem<TestSystem>()
        }
      }
      it("should return null if the system doesn't exist in the world") {
        val world = World(worldConfig)

        assertThat(world[TestSystem::class]).isNull()
      }
    }
  }
})
