package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.GdxRuntimeException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

/** For [Mapper] testing. Must be a top-level class due to the companion object usage. */
class CustomComponent : Component {
  companion object : Mapper<CustomComponent>()
}

/** For [Mapper] testing. Must not be enclosed by another class for test purposes. */
class TopLevelMapper : Mapper<CustomComponent>()

object ComponentMappersSpec : Spek({
  describe("utilities for component mappers") {
    val entity =
      Entity().apply {
        add(Texture())
      }
    it("should return a component mapper for the provided a reified type") {
      val mapper = mapperFor<Texture>()
      assertThat(mapper.has(entity)).isTrue()
    }
  }

  describe("Mapper abstract class") {
    val entity =
      Entity().apply {
        add(CustomComponent())
      }
    it("should create a component mapper of the enclosing component class") {
      assertThat(CustomComponent.mapper.has(entity)).isTrue()
    }
    it("should fail to create a component mapper if there is no enclosing class") {
      assertThatThrownBy { TopLevelMapper() }
        .isInstanceOf(GdxRuntimeException::class.java)
        .hasMessageEndingWith("defined outside of the corresponding com.badlogic.ashley.core.Component.")
    }
    it("should fail to create a component mapper if the enclosing class is not a component") {
      class BrokenMapper : Mapper<Component>()
      assertThatThrownBy { BrokenMapper() }
        .isInstanceOf(GdxRuntimeException::class.java)
        .hasMessageEndingWith("does not implement com.badlogic.ashley.core.Component.")
    }
  }
})
