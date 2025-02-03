package ktx.box2d

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests joint builder methods.
 */
class JointsTest {
  @Test
  fun `should create Joint with a custom JointDef`() {
    val (bodyA, bodyB) = getBodies()
    val jointDefinition = DistanceJointDef()
    val variable: Int

    val joint =
      bodyA.jointWith(bodyB, jointDefinition) {
        length = 2f
        assertSame(jointDefinition, this)
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertTrue(joint is DistanceJoint)
    assertEquals(2f, (joint as DistanceJoint).length)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create RevoluteJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.revoluteJointWith(bodyB) {
        motorSpeed = 2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertEquals(2f, joint.motorSpeed)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create PrismaticJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.prismaticJointWith(bodyB) {
        motorSpeed = 2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertEquals(2f, joint.motorSpeed)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create DistanceJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.distanceJointWith(bodyB) {
        length = 2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertEquals(2f, joint.length)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create PulleyJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.pulleyJointWith(bodyB) {
        ratio = 2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertEquals(2f, joint.ratio)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create MouseJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.mouseJointWith(bodyB) {
        dampingRatio = 0.2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    // Anchors are not checked, as initial joint's anchor positions do not match bodies' positions.
    assertEquals(0.2f, joint.dampingRatio)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create GearJoint`() {
    val (bodyA, bodyB) = getBodies()
    val jointA = bodyB.revoluteJointWith(bodyA)
    val jointB = bodyA.revoluteJointWith(bodyB)
    val variable: Int

    val joint =
      bodyA.gearJointWith(bodyB) {
        joint1 = jointA
        joint2 = jointB
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertSame(jointA, joint.joint1)
    assertSame(jointB, joint.joint2)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create WheelJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.wheelJointWith(bodyB) {
        motorSpeed = 2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertEquals(2f, joint.motorSpeed)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create WeldJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.weldJointWith(bodyB) {
        dampingRatio = 0.2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertEquals(0.2f, joint.dampingRatio)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create FrictionJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.frictionJointWith(bodyB) {
        maxForce = 2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertEquals(2f, joint.maxForce)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create RopeJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.ropeJointWith(bodyB) {
        maxLength = 2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertEquals(2f, joint.maxLength)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  @Test
  fun `should create MotorJoint`() {
    val (bodyA, bodyB) = getBodies()
    val variable: Int

    val joint =
      bodyA.motorJointWith(bodyB) {
        maxForce = 2f
        variable = 42
      }

    assertSame(bodyA, joint.bodyA)
    assertSame(bodyB, joint.bodyB)
    assertEquals(bodyA.position, joint.anchorA)
    assertEquals(bodyB.position, joint.anchorB)
    assertEquals(2f, joint.maxForce)
    assertEquals(42, variable)
    bodyA.world.dispose()
  }

  private fun getBodies(): Pair<Body, Body> {
    val world = createWorld()
    val bodyA =
      world.body {
        position.set(-1f, 0f)
        box(1f, 1f)
      }
    val bodyB =
      world.body {
        position.set(1f, 0f)
        box(1f, 1f)
      }
    return bodyA to bodyB
  }
}
