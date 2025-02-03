package ktx.ashley

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

object EnginesSpec : Spek({
  describe("utilities for engines") {
    val engine by memoized {
      PooledEngine()
    }
    describe("creating a component") {
      val component = engine.create<Transform>()
      it("should create a pooled component with reified type") {
        assertThat(component.x).isEqualTo(0f)
        assertThat(component.y).isEqualTo(0f)
      }
    }
    describe("creating a component without a no-arg constructor") {
      @Suppress("UNUSED_PARAMETER")
      class MissingNoArgConstructorComponent(
        body: String,
      ) : Component

      it("should throw an exception if the non-pooled engine was unable to create the component") {
        val nonPooledEngine = Engine()
        assertThatExceptionOfType(CreateComponentException::class.java).isThrownBy {
          nonPooledEngine.create<MissingNoArgConstructorComponent>()
        }
      }

      it("should throw an exception if the pooled engine was unable to create the component") {
        assertThatExceptionOfType(CreateComponentException::class.java).isThrownBy {
          engine.create<MissingNoArgConstructorComponent>()
        }
      }
    }
    describe("creating a corrupted component that throws an exception") {
      class CorruptedComponent : Component {
        init {
          throw IllegalStateException()
        }
      }
      it("should throw an exception if the non-pooled engine was unable to create the corrupted component") {
        val nonPooledEngine = Engine()
        assertThatExceptionOfType(CreateComponentException::class.java)
          .isThrownBy {
            nonPooledEngine.create<CorruptedComponent>()
          }.withRootCauseInstanceOf(IllegalStateException::class.java)
      }

      it("should throw an exception if the pooled engine was unable to create the corrupted component") {
        assertThatExceptionOfType(CreateComponentException::class.java)
          .isThrownBy {
            engine.create<CorruptedComponent>()
          }.withRootCauseInstanceOf(IllegalStateException::class.java)
      }
    }
    describe("creating a component with configuration") {
      val component =
        engine.create<Transform> {
          x = 1f
          y = 2f
        }
      it("should create a pooled component with reified type") {
        assertThat(component.x).isEqualTo(1f)
        assertThat(component.y).isEqualTo(2f)
      }
    }
    describe("multiple entity creation DSL") {
      it("should add an entity") {
        engine.add {
          entity {}
        }
        assertThat(engine.entities.size()).isEqualTo(1)
      }

      it("should add multiple entities") {
        engine.add {
          entity {}
          entity {}
        }
        assertThat(engine.entities.size()).isEqualTo(2)
      }
    }

    describe("single entity creation DSL") {
      it("should add an entity and return it") {
        val entity = engine.entity()
        assertThat(engine.entities.size()).isEqualTo(1)
        assertThat(engine.entities[0]).isEqualTo(entity)
      }
      it("should add an entity with a component with default configuration") {
        engine.entity {
          with<Transform>()
        }
        val transformEntities = engine.getEntitiesFor(oneOf(Transform::class).get())
        val component = transformEntities.single().getComponent(Transform::class.java)
        assertThat(component.x).isEqualTo(0f)
        assertThat(component.y).isEqualTo(0f)
      }
      it("should add an entity with a component with configuration") {
        engine.entity {
          with<Transform> {
            x = 1f
            y = 2f
          }
        }
        val transformEntities = engine.getEntitiesFor(oneOf(Transform::class).get())
        val component = transformEntities.single().getComponent(Transform::class.java)
        assertThat(component.x).isEqualTo(1f)
        assertThat(component.y).isEqualTo(2f)
      }
      it("should add multiple different components") {
        engine.entity {
          with<Transform>()
          with<Texture>()
        }
        val transformEntities = engine.getEntitiesFor(allOf(Transform::class, Texture::class).get())
        assertThat(transformEntities.size()).isEqualTo(1)

        val entity = transformEntities.single()
        assertThat(entity.getComponent(Transform::class.java)).isNotNull()
        assertThat(entity.getComponent(Texture::class.java)).isNotNull()
      }
    }

    describe("contracts API") {
      it("should create entity exactly once") {
        val variable: Int
        engine.entity {
          with<Transform> {
            variable = 42
          }
        }
        assertThat(variable).isEqualTo(42)
      }
      it("should create component exactly once") {
        val variable: Int
        engine.create<Transform> {
          variable = 42
        }
        assertThat(variable).isEqualTo(42)
      }
      it("should add entities exactly once") {
        val variable: Int
        engine.add {
          variable = 42
        }
        assertThat(variable).isEqualTo(42)
      }
      it("should configure entities exactly once") {
        val variable: Int
        engine.configureEntity(Entity()) {
          variable = 42
        }
        assertThat(variable).isEqualTo(42)
      }
    }

    describe("entity configuration with configureEntity") {
      it("should capture entity and engine") {
        val assignedEngine: Engine
        val assignedEntity: Entity
        val entity = Entity()

        engine.configureEntity(entity) {
          assignedEngine = this.engine
          assignedEntity = this.entity
        }

        assertThat(assignedEngine).isSameAs(engine)
        assertThat(assignedEntity).isSameAs(entity)
      }
      it("should add a component with configuration") {
        val entity = Entity()

        engine.configureEntity(entity) {
          with<Transform> {
            x = 1f
            y = 2f
          }
        }

        val component = entity.getComponent(Transform::class.java)
        assertThat(component.x).isEqualTo(1f)
        assertThat(component.y).isEqualTo(2f)
      }
    }

    describe("getSystem function") {
      it("should add a system and return it") {
        val system = TestSystem()
        engine.addSystem(system)
        assertThat(engine.getSystem<TestSystem>()).isEqualTo(system)
      }
      it("should add a system and return it as operator") {
        val system = TestSystem()
        engine.addSystem(system)
        assertThat(engine[TestSystem::class]).isEqualTo(system)
      }
      it("should throw an exception if the system is missing in the engine") {
        assertThatExceptionOfType(MissingEntitySystemException::class.java).isThrownBy {
          engine.getSystem<TestSystem>()
        }
      }
      it("should return null if the system is missing in the engine") {
        assertThat(engine[TestSystem::class]).isNull()
      }
    }
  }
})
