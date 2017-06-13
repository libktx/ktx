package ktx.ashley

import com.badlogic.ashley.core.Entity
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object ComponentMappersSpec: Spek({
  describe("utilities for component mappers") {
    val entity = Entity().apply {
      add(Texture())
    }
    it("should return a component mapper for the provided a reified type") {
      val mapper = mapperFor<Texture>()
      assertThat(mapper.has(entity)).isTrue()
    }
  }
})
