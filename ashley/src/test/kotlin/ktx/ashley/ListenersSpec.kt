package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.systems.SortedIteratingSystem
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import java.util.Comparator

object ListenersSpec : Spek({
  describe("utilities for entity listeners") {
    val engine = Engine()

    it("should invoke an addition entity listener") {
      var invoked = false
      val listener =
        object : EntityAdditionListener {
          override fun entityAdded(entity: Entity) {
            invoked = true
          }
        }
      engine.addEntityListener(listener)
      val entity = engine.entity()
      engine.removeEntity(entity)
      assertThat(invoked).isTrue()
      engine.removeEntityListener(listener)
    }

    it("should invoke a removal entity listener") {
      var invoked = false
      val listener =
        object : EntityRemovalListener {
          override fun entityRemoved(entity: Entity) {
            invoked = true
          }
        }
      val entity = engine.entity()
      engine.addEntityListener(listener)
      engine.removeEntity(entity)
      assertThat(invoked).isTrue()
      engine.removeEntityListener(listener)
    }

    it("should invoke an addition entity listener made with a lambda") {
      var invoked = false
      val listener =
        engine.onEntityAdded {
          invoked = true
        }
      val entity = engine.entity()
      assertThat(invoked).isTrue()
      engine.removeEntity(entity)
      engine.removeEntityListener(listener)
    }

    it("should invoke a removal entity listener made with a lambda") {
      var invoked = false
      val entity = engine.entity()
      val listener =
        engine.onEntityRemoved {
          invoked = true
        }
      engine.removeEntity(entity)
      assertThat(invoked).isTrue()
      engine.removeEntityListener(listener)
    }

    it("should run listeners bound to an iterating system") {
      var addAssertion = false
      var removeAssertion = false

      val system =
        object : IteratingSystem(allOf(TestComponent::class).get()) {
          private lateinit var additionListener: EntityAdditionListener
          private lateinit var removalListener: EntityRemovalListener

          override fun processEntity(
            entity: Entity,
            deltaTime: Float,
          ) = Unit

          override fun addedToEngine(engine: Engine) {
            super.addedToEngine(engine)

            additionListener =
              onEntityAdded { entity ->
                if (entity[TestComponent.mapper] != null) {
                  addAssertion = true
                }
              }

            removalListener =
              onEntityRemoved { entity ->
                if (entity[TestComponent.mapper] != null) {
                  removeAssertion = true
                }
              }
          }

          override fun removedFromEngine(engine: Engine) {
            super.removedFromEngine(engine)

            engine.removeEntityListener(additionListener)
            engine.removeEntityListener(removalListener)
          }
        }

      engine.addSystem(system)

      engine.entity().apply(engine::removeEntity)
      assertThat(addAssertion).isFalse
      assertThat(removeAssertion).isFalse

      val entity =
        engine.entity {
          with<TestComponent>()
        }
      engine.removeEntity(entity)
      engine.removeSystem(system)

      assertThat(addAssertion).isTrue
      assertThat(removeAssertion).isTrue
    }

    it("should run listeners bound to an interval iterating system") {
      var addAssertion = false
      var removeAssertion = false

      val system =
        object : IntervalIteratingSystem(allOf(TestComponent::class).get(), 0f) {
          private lateinit var additionListener: EntityAdditionListener
          private lateinit var removalListener: EntityRemovalListener

          override fun processEntity(entity: Entity) = Unit

          override fun addedToEngine(engine: Engine) {
            super.addedToEngine(engine)

            additionListener =
              onEntityAdded { entity ->
                if (entity[TestComponent.mapper] != null) {
                  addAssertion = true
                }
              }

            removalListener =
              onEntityRemoved { entity ->
                if (entity[TestComponent.mapper] != null) {
                  removeAssertion = true
                }
              }
          }

          override fun removedFromEngine(engine: Engine) {
            super.removedFromEngine(engine)

            engine.removeEntityListener(additionListener)
            engine.removeEntityListener(removalListener)
          }
        }

      engine.addSystem(system)

      engine.entity().apply(engine::removeEntity)
      assertThat(addAssertion).isFalse
      assertThat(removeAssertion).isFalse

      val entity =
        engine.entity {
          with<TestComponent>()
        }
      engine.removeEntity(entity)
      engine.removeSystem(system)

      assertThat(addAssertion).isTrue
      assertThat(removeAssertion).isTrue
    }

    it("should run listeners bound to a sorted iterating system") {
      var addAssertion = false
      var removeAssertion = false

      val system =
        object : SortedIteratingSystem(allOf(TestComponent::class).get(), Comparator.comparing(Any::hashCode)) {
          private lateinit var additionListener: EntityAdditionListener
          private lateinit var removalListener: EntityRemovalListener

          override fun processEntity(
            entity: Entity,
            deltaTime: Float,
          ) = Unit

          override fun addedToEngine(engine: Engine) {
            super.addedToEngine(engine)

            additionListener =
              onEntityAdded { entity ->
                if (entity[TestComponent.mapper] != null) {
                  addAssertion = true
                }
              }

            removalListener =
              onEntityRemoved { entity ->
                if (entity[TestComponent.mapper] != null) {
                  removeAssertion = true
                }
              }
          }

          override fun removedFromEngine(engine: Engine) {
            super.removedFromEngine(engine)

            engine.removeEntityListener(additionListener)
            engine.removeEntityListener(removalListener)
          }
        }

      engine.addSystem(system)

      engine.entity().apply(engine::removeEntity)
      assertThat(addAssertion).isFalse
      assertThat(removeAssertion).isFalse

      val entity =
        engine.entity {
          with<TestComponent>()
        }
      engine.removeEntity(entity)
      engine.removeSystem(system)

      assertThat(addAssertion).isTrue
      assertThat(removeAssertion).isTrue
    }
  }
})

internal class TestComponent : Component {
  companion object : Mapper<TestComponent>()
}
