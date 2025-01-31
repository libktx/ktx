package ktx.math

import com.badlogic.gdx.math.Affine2
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import io.kotlintest.forAtLeastOne
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.HashSet
import java.util.Random
import kotlin.math.sqrt

class ImmutableVector2Test {
  /** List of vector to use in tests */
  private val vectors =
    sequenceOf(
      // vector zero
      ImmutableVector2.ZERO,
      // axis
      ImmutableVector2.X,
      -ImmutableVector2.X,
      ImmutableVector2.Y,
      -ImmutableVector2.Y,
      // quadrants
      ImmutableVector2(3f, 4f),
      ImmutableVector2(3f, -4f),
      ImmutableVector2(-3f, 4f),
      ImmutableVector2(-3f, -4f),
      // small vectors
      ImmutableVector2(0.001f, 0f),
      ImmutableVector2(0f, 0.001f),
      ImmutableVector2(0f, -0.001f),
    )

  /** List scalar values to use in tests */
  private val scalars = sequenceOf(0f, Float.MIN_VALUE, 0.42f, 1f, 42f)

  private val interpolations =
    sequenceOf(
      Interpolation.bounce,
      Interpolation.bounceIn,
      Interpolation.bounceOut,
      Interpolation.circle,
      Interpolation.circleIn,
      Interpolation.circleOut,
      Interpolation.pow2,
      Interpolation.pow2In,
      Interpolation.pow2Out,
      Interpolation.smooth,
      Interpolation.smooth2,
    )

  @Test
  fun `equals should return true for equivalent vectors`() {
    assertEquals(ImmutableVector2.X, ImmutableVector2.X)
    assertEquals(ImmutableVector2.Y, ImmutableVector2.Y)
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.ZERO)
    assertEquals(ImmutableVector2(-3f, 42f), ImmutableVector2(-3f, 42f))

    vectors.forEach {
      assertEquals(it, it)
    }
  }

  @Test
  fun `hashCode should return same hashCode for equivalent vectors`() {
    assertEquals(ImmutableVector2.X.hashCode(), ImmutableVector2.X.hashCode())
    assertEquals(ImmutableVector2.Y.hashCode(), ImmutableVector2.Y.hashCode())
    assertEquals(ImmutableVector2.ZERO.hashCode(), ImmutableVector2.ZERO.hashCode())
    assertEquals(ImmutableVector2(-3f, 42f).hashCode(), ImmutableVector2(-3f, 42f).hashCode())

    vectors.forEach {
      assertEquals(it.hashCode(), it.hashCode())
    }
  }

  @Test
  fun `equals should return false for different vectors`() {
    assertNotEquals(ImmutableVector2.X, ImmutableVector2.Y)
    assertNotEquals(ImmutableVector2.Y, ImmutableVector2.X)
    assertNotEquals(ImmutableVector2.ZERO, ImmutableVector2.X)
    assertNotEquals(ImmutableVector2.ZERO, ImmutableVector2.Y)
    assertNotEquals(ImmutableVector2(42f, -3f), ImmutableVector2(-3f, 42f))

    vectors.forEach { v1 ->
      vectors.filterNot { v1 === it }.forEach { v2 ->
        assertNotEquals(v1, v2)
      }
    }
  }

  @Test
  fun `equals should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1 == v2, v2 == v1)
      }
    }
  }

  @Test
  fun `toMutable should return the equivalent Vector2`() {
    vectors.forEach {
      val vector2 = it.toMutable()
      assertEquals(it.x, vector2.x)
      assertEquals(it.y, vector2.y)
      assertEquals(Vector2(it.x, it.y), vector2)
    }
  }

  @Test
  fun `toImmutable should return an equivalent of the Vector2`() {
    vectors.forEach {
      val vector2 = Vector2(it.x, it.y).toImmutable()
      assertEquals(it.x, vector2.x)
      assertEquals(it.y, vector2.y)
      assertEquals(it, vector2)
    }
  }

  @Test
  fun `fromString should parse result toString`() {
    vectors.forEach {
      assertEquals(it, ImmutableVector2.fromString(it.toString()))
      assertEquals(it, ImmutableVector2.fromString(it.toMutable().toString()))
    }
  }

  @Test
  fun `len should return same value as Vector2`() {
    vectors.forEach {
      assertEquals(it.toMutable().len(), it.len)
    }
  }

  @Test
  fun `len should return length`() {
    assertEquals(1.0f, ImmutableVector2.X.len)
    assertEquals(1.0f, ImmutableVector2.Y.len)
    assertEquals(0.0f, ImmutableVector2.ZERO.len)
    assertEquals(5.0f, ImmutableVector2(-3.0f, 4.0f).len)
  }

  @Test
  fun `len2 should return same value as Vector2`() {
    vectors.forEach {
      assertEquals(it.toMutable().len2(), it.len2)
    }
  }

  @Test
  fun `len2 should return squared length`() {
    assertEquals(1.0f, ImmutableVector2.X.len2)
    assertEquals(1.0f, ImmutableVector2.Y.len2)
    assertEquals(0.0f, ImmutableVector2.ZERO.len2)
    assertEquals(25.0f, ImmutableVector2(-3.0f, 4.0f).len2)
  }

  @Test
  fun `isZero should returns same result than Vector2`() {
    vectors.forEach { vector ->
      assertEquals(vector.toMutable().isZero, vector.isZero())
      scalars.filter { it > 0f }.forEach { margin ->
        assertEquals(vector.toMutable().isZero(margin), vector.isZero(margin))
      }
    }
  }

  @Test
  fun `isZero should return true for vector zero`() {
    assertTrue(ImmutableVector2.ZERO.isZero())
  }

  @Test
  fun `isZero should return false for non-zero vectors`() {
    assertFalse(ImmutableVector2.X.isZero())
    assertFalse((-ImmutableVector2.X).isZero())
    assertFalse(ImmutableVector2.Y.isZero())
    assertFalse((-ImmutableVector2.Y).isZero())
    assertFalse(ImmutableVector2(3f, 4f).isZero())
  }

  @Test
  fun `isUnit with margin should return same value as Vector2`() {
    vectors.forEach { vector ->
      assertEquals(vector.toMutable().isUnit, vector.isUnit())
      scalars.filter { it > 0f }.forEach { margin ->
        assertEquals("$vector, $margin", vector.toMutable().isUnit(margin), vector.isUnit(margin))
      }
    }
  }

  @Test
  fun `isUnit should return true for axis`() {
    assertTrue(ImmutableVector2.X.isUnit())
    assertTrue((-ImmutableVector2.X).isUnit())
    assertTrue(ImmutableVector2.Y.isUnit())
    assertTrue((-ImmutableVector2.Y).isUnit())
  }

  @Test
  fun `isUnit should return false for non-unit vectors`() {
    assertFalse(ImmutableVector2.ZERO.isUnit())
    assertFalse(ImmutableVector2(-3.0f, 4.0f).isUnit())
    assertFalse(ImmutableVector2(0.5f, 0.5f).isUnit())
  }

  @Test
  fun `nor should return same value as Vector2`() {
    vectors.forEach {
      assertEquals(it.toMutable().nor().toImmutable(), it.nor)
    }
  }

  @Test
  fun `nor should return normalized vectors`() {
    assertEquals(ImmutableVector2.X, ImmutableVector2(0.4f, 0f).nor)
    assertEquals(ImmutableVector2.X, ImmutableVector2(3f, 0f).nor)
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.ZERO.nor)
    assertEquals(ImmutableVector2(3f / 5f, -4f / 5f), ImmutableVector2(3f, -4f).nor)
  }

  @Test
  fun `nor should return unit vectors`() {
    vectors.forEach {
      assertTrue(it.isZero() || it.nor.isUnit())
    }
  }

  @Test
  fun `unaryMinus should return same value as Vector2 scaled by -1`() {
    vectors.forEach {
      assertEquals(it.toMutable().scl(-1f).toImmutable(), -it)
    }
  }

  @Test
  fun `minus should return same value as Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().minus(v2.toMutable()).toImmutable(), v1 - v2)
      }
    }
  }

  @Test
  fun `minus should return difference between the two vectors`() {
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.X - ImmutableVector2.X)
    assertEquals(ImmutableVector2(-2f, -0f), -ImmutableVector2.X - ImmutableVector2.X)
    assertEquals(ImmutableVector2(-1f, 1f), ImmutableVector2.Y - ImmutableVector2.X)
    assertEquals(ImmutableVector2(1f, -1f), ImmutableVector2(3f, 4f) - ImmutableVector2(2f, 5f))
  }

  @Test
  fun `plus should return same value as Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().plus(v2.toMutable()).toImmutable(), v1 + v2)
      }
    }
  }

  @Test
  fun `plus should return sum of the two vectors`() {
    assertEquals(ImmutableVector2(2f, 0f), ImmutableVector2.X + ImmutableVector2.X)
    assertEquals(ImmutableVector2(2f, 0f), ImmutableVector2.X.plus(1f, 0f))

    assertEquals(ImmutableVector2.ZERO, (-ImmutableVector2.X) + ImmutableVector2.X)
    assertEquals(ImmutableVector2.ZERO, (-ImmutableVector2.X).plus(1f, 0f))

    assertEquals(ImmutableVector2(1f, 1f), ImmutableVector2.Y + ImmutableVector2.X)
    assertEquals(ImmutableVector2(1f, 1f), ImmutableVector2.Y.plus(1f, 0f))

    assertEquals(ImmutableVector2(5f, 9f), ImmutableVector2(3f, 4f) + ImmutableVector2(2f, 5f))
    assertEquals(ImmutableVector2(5f, 9f), ImmutableVector2(3f, 4f).plus(2f, 5f))
  }

  @Test
  fun `plus should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1 + v2, v2 + v1)
      }
    }
  }

  @Test
  fun `times should return same value as Vector2 scale`() {
    scalars.forEach { factor ->
      vectors.forEach { vector ->
        assertEquals(vector.toMutable().scl(factor).toImmutable(), vector * factor)
      }
    }
  }

  @Test
  fun `times should return scaled vector`() {
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.ZERO * 42f)
    assertEquals(ImmutableVector2(0f, 2f), ImmutableVector2.Y * 2f)
    assertEquals(ImmutableVector2(15f, -6f), ImmutableVector2(5f, -2f) * 3f)
    assertEquals(ImmutableVector2(2.5f, -1f), ImmutableVector2(5f, -2f) * 0.5f)
  }

  @Test
  fun `times should return same value as scl does for Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().scl(v2.toMutable()).toImmutable(), v1 * v2)
        assertEquals(v1.toMutable().scl(v2.x, v2.y).toImmutable(), v1.times(v2.x, v2.y))
      }
    }
  }

  @Test
  fun `times should return scaled vector on both axis`() {
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.X * ImmutableVector2.ZERO)
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.X.times(0f, 0f))

    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.X * ImmutableVector2.Y)
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.X.times(0f, 1f))

    assertEquals(ImmutableVector2(4f, -15f), ImmutableVector2(-2f, 3f) * ImmutableVector2(-2f, -5f))
    assertEquals(ImmutableVector2(4f, -15f), ImmutableVector2(-2f, 3f).times(-2f, -5f))

    assertEquals(ImmutableVector2(4f, 15f), ImmutableVector2(2f, 3f) * ImmutableVector2(2f, 5f))
    assertEquals(ImmutableVector2(4f, 15f), ImmutableVector2(2f, 3f).times(2f, 5f))
  }

  @Test
  fun `dot should return same value as Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().dot(v2.toMutable()), v1 dot v2)
        assertEquals(v1.toMutable().dot(v2.x, v2.y), v1.dot(v2.x, v2.y))
      }
    }
  }

  @Test
  fun `dot should return dot product`() {
    assertEquals(1f, ImmutableVector2.X dot ImmutableVector2.X)
    assertEquals(1f, ImmutableVector2.X.dot(1f, 0f))

    assertEquals(1f, ImmutableVector2.Y dot ImmutableVector2(0f, 1f))
    assertEquals(1f, ImmutableVector2.Y.dot(0f, 1f))

    assertEquals(0f, ImmutableVector2.X dot ImmutableVector2(0f, 1f))
    assertEquals(0f, ImmutableVector2.X.dot(0f, 1f))

    assertEquals(0f, ImmutableVector2.X dot ImmutableVector2(0f, 0f))
    assertEquals(0f, ImmutableVector2.X.dot(0f, 0f))

    assertEquals(2f, ImmutableVector2(1f, 1f) dot ImmutableVector2(1f, 1f))
    assertEquals(2f, ImmutableVector2(1f, 1f).dot(1f, 1f))

    assertEquals(7f, ImmutableVector2(-2f, 3f) dot ImmutableVector2(4f, 5f))
    assertEquals(7f, ImmutableVector2(-2f, 3f).dot(4f, 5f))
  }

  @Test
  fun `dot should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1 dot v2, v2 dot v1)
      }
    }
  }

  @Test
  fun `dst2 should return same value as Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().dst2(v2.toMutable()), v1.dst2(v2))
        assertEquals(v1.toMutable().dst2(v2.x, v2.y), v1.dst2(v2.x, v2.y))
      }
    }
  }

  @Test
  fun `dst2 should return squared distance`() {
    assertEquals(1f, ImmutableVector2.ZERO dst2 ImmutableVector2.X)
    assertEquals(1f, ImmutableVector2.ZERO.dst2(1f, 0f))

    assertEquals(2f, ImmutableVector2.X dst2 ImmutableVector2.Y)
    assertEquals(2f, ImmutableVector2.X.dst2(0f, 1f))

    assertEquals(25f, ImmutableVector2.ZERO dst2 ImmutableVector2(3f, 4f))
    assertEquals(25f, ImmutableVector2.ZERO.dst2(3f, 4f))

    assertEquals(50f, ImmutableVector2(2f, -3f) dst2 ImmutableVector2(3f, 4f))
    assertEquals(50f, ImmutableVector2(2f, -3f).dst2(3f, 4f))
  }

  @Test
  fun `dst2 should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1 dst2 v2, v2 dst2 v1)
      }
    }
  }

  @Test
  fun `dst should return same value as Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().dst(v2.toMutable()), v1.dst(v2))
        assertEquals(v1.toMutable().dst(v2.x, v2.y), v1.dst(v2.x, v2.y))
      }
    }
  }

  @Test
  fun `dst should return squared distance`() {
    assertEquals(1f, ImmutableVector2.ZERO dst ImmutableVector2.X)
    assertEquals(1f, ImmutableVector2.ZERO.dst(1f, 0f))

    assertEquals(sqrt(2f), ImmutableVector2.X dst ImmutableVector2.Y)
    assertEquals(sqrt(2f), ImmutableVector2.X.dst(0f, 1f))

    assertEquals(5f, ImmutableVector2.ZERO dst ImmutableVector2(3f, 4f))
    assertEquals(5f, ImmutableVector2.ZERO.dst(3f, 4f))

    assertEquals(sqrt(50f), ImmutableVector2(2f, -3f) dst ImmutableVector2(3f, 4f))
    assertEquals(sqrt(50f), ImmutableVector2(2f, -3f).dst(3f, 4f))
  }

  @Test
  fun `dst should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1 dst v2, v2 dst v1)
      }
    }
  }

  @Test
  fun `withLimit should return same value as Vector2`() {
    scalars.forEach { l ->
      vectors.forEach { v ->
        assertEquals(v.toMutable().limit(l).toImmutable(), v.withLimit(l))
      }
    }
  }

  @Test
  fun `withLimit should return a vector within the limit`() {
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.ZERO.withLimit(42f))
    assertEquals(ImmutableVector2.X, ImmutableVector2(42f, 0f).withLimit(1f))
    assertEquals(ImmutableVector2(0.5f, 0f), ImmutableVector2(0.5f, 0f).withLimit(1f))
    assertEquals(ImmutableVector2(3f, -4f), ImmutableVector2(6f, -8f).withLimit(5f))
    assertEquals(ImmutableVector2(2f, -3f), ImmutableVector2(2f, -3f).withLimit(5f))
  }

  @Test
  fun `withLimit2 should return same value as Vector2`() {
    scalars.forEach { l ->
      vectors.forEach { v ->
        assertEquals(v.toMutable().limit2(l).toImmutable(), v.withLimit2(l))
      }
    }
  }

  @Test
  fun `withLimit2 should return a vector within the limit`() {
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.ZERO.withLimit2(42f))
    assertEquals(ImmutableVector2.X, ImmutableVector2(42f, 0f).withLimit2(1f))
    assertEquals(ImmutableVector2(0.5f, 0f), ImmutableVector2(0.5f, 0f).withLimit2(1f))
    assertEquals(ImmutableVector2(3f, -4f), ImmutableVector2(6f, -8f).withLimit2(25f))
    assertEquals(ImmutableVector2(2f, -3f), ImmutableVector2(2f, -3f).withLimit2(25f))
  }

  @Test
  fun `withLength should return same value as Vector2`() {
    scalars.forEach { l ->
      vectors.forEach { v ->
        assertEquals(v.toMutable().setLength(l).toImmutable(), v.withLength(l))
      }
    }
  }

  @Test
  fun `withLength should return a vector of given length`() {
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.ZERO.withLength(5f))
    assertEquals(ImmutableVector2(3f, 0f), ImmutableVector2.X.withLength(3f))
    assertEquals(ImmutableVector2(0f, 0.5f), ImmutableVector2.Y.withLength(0.5f))
    assertEquals(ImmutableVector2(6f, -8f), ImmutableVector2(3f, -4f).withLength(10f))
    assertEquals(ImmutableVector2(-1.5f, 2f), ImmutableVector2(-3f, 4f).withLength(2.5f))
  }

  @Test
  fun `withLength2 should return same value as Vector2`() {
    scalars.forEach { l ->
      vectors.forEach { v ->
        assertEquals(v.toMutable().setLength2(l).toImmutable(), v.withLength2(l))
      }
    }
  }

  @Test
  fun `withLength2 should return a vector of given squared length`() {
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.ZERO.withLength2(25f))
    assertEquals(ImmutableVector2(3f, 0f), ImmutableVector2.X.withLength2(9f))
    assertEquals(ImmutableVector2(0f, 0.5f), ImmutableVector2.Y.withLength2(0.25f))
    assertEquals(ImmutableVector2(6f, -8f), ImmutableVector2(3f, -4f).withLength2(100f))
    assertEquals(ImmutableVector2(-1.5f, 2f), ImmutableVector2(-3f, 4f).withLength2(6.25f))
  }

  @Test
  fun `withClamp should return same value as Vector2`() {
    scalars.forEach { min ->
      scalars.filter { it >= min }.forEach { max ->
        vectors.forEach { v ->
          assertEquals(v.toMutable().clamp(min, max).toImmutable(), v.withClamp(min, max))
        }
      }
    }
  }

  @Test
  fun `withClamp should return a vector of length between min and max`() {
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.ZERO.withClamp(1f, 2f))
    assertEquals(ImmutableVector2(2f, 0f), ImmutableVector2.X.withClamp(2f, 3f))
    assertEquals(ImmutableVector2(0.8f, 0f), ImmutableVector2.X.withClamp(0.5f, 0.8f))
    assertEquals(ImmutableVector2.X, ImmutableVector2.X.withClamp(0.5f, 2f))

    assertEquals(ImmutableVector2(3f, -4f), ImmutableVector2(3f, -4f).withClamp(4f, 6f))
    assertEquals(ImmutableVector2(1.5f, -2f), ImmutableVector2(3f, -4f).withClamp(1f, 2.5f))
    assertEquals(ImmutableVector2(6f, -8f), ImmutableVector2(3f, -4f).withClamp(10f, 12f))
  }

  @Test
  fun `withClamp2 should return a vector of length between min2 and max2`() {
    assertEquals(ImmutableVector2.ZERO, ImmutableVector2.ZERO.withClamp2(1f, 4f))
    assertEquals(ImmutableVector2(2f, 0f), ImmutableVector2.X.withClamp2(4f, 9f))
    assertEquals(ImmutableVector2(0.8f, 0f), ImmutableVector2.X.withClamp2(0.5f, 0.64f))
    assertEquals(ImmutableVector2.X, ImmutableVector2.X.withClamp2(0.5f, 2f))

    assertEquals(ImmutableVector2(3f, -4f), ImmutableVector2(3f, -4f).withClamp2(16f, 36f))
    assertEquals(ImmutableVector2(1.5f, -2f), ImmutableVector2(3f, -4f).withClamp2(1f, 6.25f))
    assertEquals(ImmutableVector2(6f, -8f), ImmutableVector2(3f, -4f).withClamp2(100f, 110f))
  }

  @Test
  fun `times should return same value as Vector2 mul`() {
    val transformations =
      listOf(
        Affine2(),
        Affine2().apply {
          preTranslate(-2f, 3f)
          preRotate(18f)
          preScale(1.5f, 2f)
        },
      )

    transformations.forEach { transformation ->
      vectors.forEach { vector ->
        val matrix = Matrix3().set(transformation)
        assertEquals(vector.toMutable().mul(matrix).toImmutable(), vector * transformation)
      }
    }
  }

  @Test
  fun `applying affine transformation should be equivalent of doing the operations`() {
    val vector = ImmutableVector2(3f, 4f)

    val transformation =
      Affine2().apply {
        preTranslate(-2f, 3f) // (1f, 7f)
        preRotate(90f) // (-7f, 1f)
        preScale(0.5f, 2f) // (-3.5f, 2f)
      }

    assertTrue(ImmutableVector2(-3.5f, 2f).epsilonEquals(vector * transformation, MathUtils.FLOAT_ROUNDING_ERROR))
    assertTrue(
      vector
        .plus(-2f, 3f)
        .withRotationDeg(90f)
        .times(0.5f, 2f)
        .epsilonEquals(vector * transformation, MathUtils.FLOAT_ROUNDING_ERROR),
    )
  }

  @Test
  fun `crs should return same value as Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().crs(v2.toMutable()), v1 x v2)
        assertEquals(v1.toMutable().crs(v2.toMutable()), v1.crs(v2))
        assertEquals(v1.toMutable().crs(v2.x, v2.y), v1.crs(v2.x, v2.y))
      }
    }
  }

  @Test
  fun `crs should return cross product`() {
    assertEquals(-2f, ImmutableVector2(2f, 3f) x ImmutableVector2(4f, 5f))
    assertEquals(2f, ImmutableVector2(4f, 5f) x ImmutableVector2(2f, 3f))
    assertEquals(0f, ImmutableVector2(2f, 3f) x ImmutableVector2.ZERO)
    assertEquals(0f, ImmutableVector2.ZERO x ImmutableVector2(4f, 5f))
  }

  @Test
  fun `zero vector should have undefined direction`() {
    assertEquals(Float.NaN, ImmutableVector2.ZERO.angleDeg())
    assertEquals(Float.NaN, ImmutableVector2.ZERO.angleRad())
  }

  @Test
  fun `angleDeg should return correct angles between axes`() {
    assertEquals(90f, (ImmutableVector2.Y).angleDeg(ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    assertEquals(90f, (-ImmutableVector2.X).angleDeg(ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    assertEquals(90f, (-ImmutableVector2.Y).angleDeg(-ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    assertEquals(90f, (ImmutableVector2.X).angleDeg(-ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

    assertEquals(-90f, (ImmutableVector2.Y).angleDeg(-ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    assertEquals(-90f, (-ImmutableVector2.X).angleDeg(-ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    assertEquals(-90f, (-ImmutableVector2.Y).angleDeg(ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    assertEquals(-90f, (ImmutableVector2.X).angleDeg(ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

    assertEquals(180f, (ImmutableVector2.X).angleDeg(-ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    assertEquals(180f, (ImmutableVector2.Y).angleDeg(-ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
  }

  @Test
  fun `angleDeg should return expected angles`() {
    assertEquals(45f, ImmutableVector2(1f, 1f).angleDeg(ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    assertEquals(45f, ImmutableVector2.Y.angleDeg(ImmutableVector2(1f, 1f)), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    assertEquals(-45f, ImmutableVector2.X.angleDeg(ImmutableVector2(1f, 1f)), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
  }

  @Test
  fun `angleDeg should return value between -180 and 180`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v1 ->
      assertTrue(v1.angleDeg() in (-180f)..(180f))
      vectors.filterNot { it.isZero(0f) }.forEach { v2 ->
        assertTrue(v1.angleDeg(v2) in (-180f)..(180f))
      }
    }
  }

  @Test
  fun `angleDeg should return angle of applied rotation`() {
    vectors.filter { it.len2.isFinite() && it.len2 > 0.5 }.forEach { initialVector ->

      // rotate from positive x-axis to positive y-axis (typically counter-clockwise)
      var deltaAngle = 60f
      var previousVector = initialVector
      var currentVector = previousVector.withRotationDeg(deltaAngle)
      assertEquals(deltaAngle, currentVector.angleDeg(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

      repeat(4) {
        previousVector = currentVector
        currentVector = previousVector.withRotationDeg(deltaAngle)
        assertEquals(deltaAngle, currentVector.angleDeg(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
      }

      // rotate from positive x-axis to negative y-axis (typically clockwise)
      deltaAngle = -60f
      previousVector = initialVector
      currentVector = previousVector.withRotationDeg(deltaAngle)
      assertEquals(deltaAngle, currentVector.angleDeg(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

      repeat(4) {
        previousVector = currentVector
        currentVector = previousVector.withRotationDeg(deltaAngle)
        assertEquals(deltaAngle, currentVector.angleDeg(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
      }
    }
  }

  @Test
  fun `angleRad to x-axis should return same value as Vector2`() {
    // libGDX returns an arbitrary (incorrect) angle when using zero vector. (vector zero's angle is undefined)
    vectors.filterNot { it.isZero(0f) }.forEach { v1 ->
      assertEquals("$v1", v1.toMutable().angleRad(), v1.angleRad())
    }
  }

  @Test
  fun `angleRad should return correct angles between axises`() {
    assertEquals(MathUtils.PI / 2f, (ImmutableVector2.Y).angleRad(ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(MathUtils.PI / 2f, (-ImmutableVector2.X).angleRad(ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(MathUtils.PI / 2f, (-ImmutableVector2.Y).angleRad(-ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(MathUtils.PI / 2f, (ImmutableVector2.X).angleRad(-ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)

    assertEquals(-MathUtils.PI / 2f, (ImmutableVector2.Y).angleRad(-ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(-MathUtils.PI / 2f, (-ImmutableVector2.X).angleRad(-ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(-MathUtils.PI / 2f, (-ImmutableVector2.Y).angleRad(ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(-MathUtils.PI / 2f, (ImmutableVector2.X).angleRad(ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)

    assertEquals(MathUtils.PI, (ImmutableVector2.X).angleRad(-ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(MathUtils.PI, (ImmutableVector2.Y).angleRad(-ImmutableVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)
  }

  @Test
  fun `angleRad should return expected angles`() {
    assertEquals(MathUtils.PI / 4f, ImmutableVector2(1f, 1f).angleRad(ImmutableVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(MathUtils.PI / 4f, ImmutableVector2.Y.angleRad(ImmutableVector2(1f, 1f)), MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(-MathUtils.PI / 4f, ImmutableVector2.X.angleRad(ImmutableVector2(1f, 1f)), MathUtils.FLOAT_ROUNDING_ERROR)
  }

  @Test
  fun `angleRad should returns value between -PI and PI`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v1 ->
      vectors.filterNot { it.isZero(0f) }.forEach { v2 ->
        assertTrue(v1.angleRad(v2) in (-MathUtils.PI)..(MathUtils.PI))
      }
    }
  }

  @Test
  fun `angleRad should return angle of applied rotation`() {
    vectors.filter { it.len2.isFinite() && it.len2 > 0.5 }.forEach { initialVector ->

      // rotate from positive x-axis to positive y-axis (typically counter-clockwise)
      var deltaAngle = MathUtils.PI / 3
      var previousVector = initialVector
      var currentVector = previousVector.withRotationRad(deltaAngle)
      assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)
      assertEquals(deltaAngle, currentVector.angleRad(previousVector.x, previousVector.y), MathUtils.FLOAT_ROUNDING_ERROR)

      repeat(4) {
        previousVector = currentVector
        currentVector = previousVector.withRotationRad(deltaAngle)
        assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(deltaAngle, currentVector.angleRad(previousVector.x, previousVector.y), MathUtils.FLOAT_ROUNDING_ERROR)
      }

      // rotate from positive x-axis to negative y-axis (typically clockwise)
      deltaAngle = -MathUtils.PI / 3
      previousVector = initialVector
      currentVector = previousVector.withRotationRad(deltaAngle)
      assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)
      assertEquals(deltaAngle, currentVector.angleRad(previousVector.x, previousVector.y), MathUtils.FLOAT_ROUNDING_ERROR)

      repeat(4) {
        previousVector = currentVector
        currentVector = previousVector.withRotationRad(deltaAngle)
        assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(deltaAngle, currentVector.angleRad(previousVector.x, previousVector.y), MathUtils.FLOAT_ROUNDING_ERROR)
      }
    }
  }

  @Test
  fun `angleRad with x axis should return same value as Vector2`() {
    // libGDX returns an arbitrary (incorrect) angle when using zero vector. (vector zero's angle is undefined)
    vectors.filterNot { it.isZero(0f) }.forEach {
      assertEquals(it.toMutable().angleRad(), it.angleRad())
    }
  }

  @Test
  fun `withAngle should return same value as Vector2`() {
    scalars.forEach { angle ->
      vectors.forEach { vector ->
        assertEquals(vector.toMutable().setAngleDeg(angle).toImmutable(), vector.withAngleDeg(angle))
      }
    }
  }

  @Test
  fun `withAngleDeg should return rotated vector`() {
    assertTrue(
      ImmutableVector2(0.6f, 0.8f)
        .withAngleDeg(90f)
        .epsilonEquals(ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2(0.6f, 0.8f)
        .withAngleDeg(-90f)
        .epsilonEquals(-ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2(
        0.6f,
        0.8f,
      ).withAngleDeg(45f)
        .epsilonEquals(ImmutableVector2(sqrt(0.5f), sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2(
        0.6f,
        0.8f,
      ).withAngleDeg(-45f)
        .epsilonEquals(ImmutableVector2(sqrt(0.5f), -sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )
  }

  @Test
  fun `withAngleRad should return same value as Vector2`() {
    scalars.forEach { angle ->
      vectors.forEach { vector ->
        assertEquals(vector.toMutable().setAngleRad(angle).toImmutable(), vector.withAngleRad(angle))
      }
    }
  }

  @Test
  fun `withAngleRad should return rotated vector`() {
    assertTrue(ImmutableVector2(0.6f, 0.8f).withAngleDeg(90f).epsilonEquals(ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))
    assertTrue(ImmutableVector2(0.6f, 0.8f).withAngleDeg(-90f).epsilonEquals(-ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))
    assertTrue(
      ImmutableVector2(
        0.6f,
        0.8f,
      ).withAngleDeg(45f).epsilonEquals(ImmutableVector2(sqrt(0.5f), sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2(
        0.6f,
        0.8f,
      ).withAngleDeg(-45f).epsilonEquals(ImmutableVector2(sqrt(0.5f), -sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )
  }

  @Test
  fun `withRotationDeg should return same value as Vector2`() {
    scalars.forEach { angle ->
      vectors.forEach { vector ->
        assertEquals(vector.toMutable().rotateDeg(angle).toImmutable(), vector.withRotationDeg(angle))
      }
    }
  }

  @Test
  fun `withRotationDeg should return rotated vector`() {
    assertTrue(ImmutableVector2.X.withRotationDeg(90f).epsilonEquals(ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))
    assertTrue(ImmutableVector2.X.withRotationDeg(-90f).epsilonEquals(-ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))

    assertTrue(
      ImmutableVector2.X.withRotationDeg(45f).epsilonEquals(ImmutableVector2(sqrt(0.5f), sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2.X.withRotationDeg(-45f).epsilonEquals(ImmutableVector2(sqrt(0.5f), -sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )

    assertTrue(
      ImmutableVector2(sqrt(0.5f), sqrt(0.5f)).withRotationDeg(45f).epsilonEquals(ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2(sqrt(0.5f), sqrt(0.5f)).withRotationDeg(-45f).epsilonEquals(ImmutableVector2.X, MathUtils.FLOAT_ROUNDING_ERROR),
    )
  }

  @Test
  fun `withRotationRad should return same value as Vector2`() {
    scalars.forEach { angle ->
      vectors.forEach { vector ->
        assertEquals(vector.toMutable().rotateRad(angle).toImmutable(), vector.withRotationRad(angle))
      }
    }
  }

  @Test
  fun `withRotationRad should return rotated vector`() {
    assertTrue(ImmutableVector2.X.withRotationRad(MathUtils.PI / 2f).epsilonEquals(ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))
    assertTrue(ImmutableVector2.X.withRotationRad(-MathUtils.PI / 2f).epsilonEquals(-ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))

    assertTrue(
      ImmutableVector2.X
        .withRotationRad(
          MathUtils.PI / 4f,
        ).epsilonEquals(ImmutableVector2(sqrt(0.5f), sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2.X
        .withRotationRad(
          -MathUtils.PI / 4f,
        ).epsilonEquals(ImmutableVector2(sqrt(0.5f), -sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )

    assertTrue(
      ImmutableVector2(
        sqrt(0.5f),
        sqrt(0.5f),
      ).withRotationRad(MathUtils.PI / 4f).epsilonEquals(ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2(
        sqrt(0.5f),
        sqrt(0.5f),
      ).withRotationRad(-MathUtils.PI / 4f).epsilonEquals(ImmutableVector2.X, MathUtils.FLOAT_ROUNDING_ERROR),
    )
  }

  @Test
  fun `withRotation90 should return same value as Vector2`() {
    vectors.forEach { vector ->
      assertEquals(vector.toMutable().rotate90(-1).toImmutable(), vector.withRotation90(-1))
      assertEquals(vector.toMutable().rotate90(1).toImmutable(), vector.withRotation90(1))
    }
  }

  @Test
  fun `withRotation90 should return rotated vector`() {
    assertTrue(ImmutableVector2.X.withRotation90(1).epsilonEquals(ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))
    assertTrue(ImmutableVector2.X.withRotation90(-1).epsilonEquals(-ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))

    assertTrue(ImmutableVector2.Y.withRotation90(1).epsilonEquals(-ImmutableVector2.X, MathUtils.FLOAT_ROUNDING_ERROR))
    assertTrue(ImmutableVector2.Y.withRotation90(-1).epsilonEquals(ImmutableVector2.X, MathUtils.FLOAT_ROUNDING_ERROR))

    assertTrue(
      ImmutableVector2(
        sqrt(0.5f),
        sqrt(0.5f),
      ).withRotation90(1).epsilonEquals(ImmutableVector2(-sqrt(0.5f), sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2(
        sqrt(0.5f),
        sqrt(0.5f),
      ).withRotation90(-1).epsilonEquals(ImmutableVector2(sqrt(0.5f), -sqrt(0.5f)), MathUtils.FLOAT_ROUNDING_ERROR),
    )
  }

  @Test
  fun `withLerp should return same value as Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        scalars.forEach { ratio ->
          assertEquals(v1.toMutable().lerp(v2.toMutable(), ratio).toImmutable(), v1.withLerp(v2, ratio))
        }
      }
    }
  }

  @Test
  fun `withLerp should return interpolated vector`() {
    assertTrue(
      ImmutableVector2.X.withLerp(ImmutableVector2.Y, 0.5f).epsilonEquals(ImmutableVector2(0.5f, 0.5f), MathUtils.FLOAT_ROUNDING_ERROR),
    )
    assertTrue(
      ImmutableVector2.X.withLerp(ImmutableVector2.Y, 0.3f).epsilonEquals(ImmutableVector2(0.7f, 0.3f), MathUtils.FLOAT_ROUNDING_ERROR),
    )

    assertTrue(ImmutableVector2.X.withLerp(-ImmutableVector2.X, 0.5f).epsilonEquals(ImmutableVector2.ZERO, MathUtils.FLOAT_ROUNDING_ERROR))
    assertTrue(
      ImmutableVector2.X.withLerp(-ImmutableVector2.X, 0.3f).epsilonEquals(ImmutableVector2(0.4f, 0f), MathUtils.FLOAT_ROUNDING_ERROR),
    )
  }

  @Test
  fun `withLerp with a factor of 0 should return the origin vector`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertTrue(v1.withLerp(v2, 0f).epsilonEquals(v1, MathUtils.FLOAT_ROUNDING_ERROR))
      }
    }
  }

  @Test
  fun `withLerp with a factor of 1 should return the target vector`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertTrue(v1.withLerp(v2, 1f).epsilonEquals(v2, MathUtils.FLOAT_ROUNDING_ERROR))
      }
    }
  }

  @Test
  fun `withInterpolation should return same value as Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        scalars.forEach { alpha ->
          interpolations.forEach { interpolation ->
            assertEquals(
              v1.toMutable().interpolate(v2.toMutable(), alpha, interpolation).toImmutable(),
              v1.withInterpolation(v2, alpha, interpolation),
            )
          }
        }
      }
    }
  }

  @Test
  fun `withInterpolation with a factor of 0 should return the origin vector`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        interpolations.forEach { interpolation ->
          assertTrue(v1.withInterpolation(v2, 0f, interpolation).epsilonEquals(v1, MathUtils.FLOAT_ROUNDING_ERROR))
        }
      }
    }
  }

  @Test
  fun `withInterpolation with a factor of 1 should return the target vector`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        interpolations.forEach { interpolation ->
          assertTrue(v1.withInterpolation(v2, 1f, interpolation).epsilonEquals(v2, MathUtils.FLOAT_ROUNDING_ERROR))
        }
      }
    }
  }

  @Test
  fun `withRandomDirection should return a vector of same length`() {
    val rng = Random(42)

    vectors.forEach { vector ->
      assertEquals(vector.withRandomDirection(rng).len, vector.len, MathUtils.FLOAT_ROUNDING_ERROR)
    }
  }

  @Test
  fun `withRandomDirection should return a variety of random vectors`() {
    val rng = Random(42)

    val vectors = HashSet<ImmutableVector2>()
    repeat(1000) {
      vectors += ImmutableVector2.X.withRandomDirection(rng)
    }

    assertTrue(vectors.size > 900)
    forAtLeastOne(vectors) { it.x > 0 && it.y > 0 }
    forAtLeastOne(vectors) { it.x > 0 && it.y < 0 }
    forAtLeastOne(vectors) { it.x < 0 && it.y > 0 }
    forAtLeastOne(vectors) { it.x < 0 && it.y < 0 }
  }

  @Test
  fun `epsilonEquals should return same than Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        scalars.forEach { e ->
          assertEquals(v1.toMutable().epsilonEquals(v2.toMutable(), e), v1.epsilonEquals(v2, e))
          assertEquals(v1.toMutable().epsilonEquals(v2.x, v2.y, e), v1.epsilonEquals(v2.x, v2.y, e))
        }
      }
    }
  }

  @Test
  fun `epsilonEquals should return true for equals vectors`() {
    vectors.forEach { v ->
      assertTrue(v.epsilonEquals(v, 0f))
    }
  }

  @Test
  fun `epsilonEquals should consider epsilon argument`() {
    vectors.forEach { v1 ->
      assertTrue(v1.copy(x = v1.x + Float.MIN_VALUE, y = v1.y + Float.MIN_VALUE).epsilonEquals(v1, MathUtils.FLOAT_ROUNDING_ERROR))
      assertTrue(v1.copy(x = v1.x - Float.MIN_VALUE, y = v1.y - Float.MIN_VALUE).epsilonEquals(v1, MathUtils.FLOAT_ROUNDING_ERROR))

      vectors.forEach { v2 ->
        assertTrue(v1.epsilonEquals(v2, 10f))
      }
    }

    assertTrue(ImmutableVector2.X.epsilonEquals(ImmutableVector2.Y, 1f))
  }

  @Test
  fun `epsilonEquals should return false for different vectors`() {
    assertFalse(ImmutableVector2.ZERO.epsilonEquals(ImmutableVector2.X, MathUtils.FLOAT_ROUNDING_ERROR))
    assertFalse(ImmutableVector2.ZERO.epsilonEquals(ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))
    assertFalse(ImmutableVector2.X.epsilonEquals(ImmutableVector2.Y, MathUtils.FLOAT_ROUNDING_ERROR))
    assertFalse(ImmutableVector2.X.epsilonEquals(ImmutableVector2.Y, 0.8f))
    assertFalse(ImmutableVector2(2f, 3f).epsilonEquals(ImmutableVector2(2.1f, 3f), 0.09f))
  }

  @Test
  fun `epsilonEquals should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        scalars.forEach { s ->
          assertEquals(v1.epsilonEquals(v2, s), v2.epsilonEquals(v1, s))
        }
      }
    }
  }

  @Test
  fun `isOnLine should return same than Vector2`() {
    // libGDX returns false (incorrect) when compared with vector zero.
    vectors.filterNot { it.isZero(0f) }.forEach { v1 ->
      vectors.filterNot { it.isZero(0f) }.forEach { v2 ->
        assertEquals(v1.toMutable().isOnLine(v2.toMutable()), v1.isOnLine(v2))
        scalars.forEach { e ->
          assertEquals(v1.toMutable().isOnLine(v2.toMutable(), e), v1.isOnLine(v2, e))
        }
      }
    }
  }

  @Test
  fun `isOnLine should return false when compared with vector zero`() {
    vectors.forEach { v ->
      assertFalse(v.isOnLine(ImmutableVector2.ZERO))
      assertFalse(ImmutableVector2.ZERO.isOnLine(v))
    }
  }

  @Test
  fun `isOnLine should return true for same non-zero vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertTrue(v.isOnLine(v))
    }
  }

  @Test
  fun `isOnLine should return true for opposite vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertTrue(v.isOnLine(-v))
      assertTrue(v.isOnLine(v * -2f))
      assertTrue(v.isOnLine(v * -0.5f))
    }
  }

  @Test
  fun `isOnLine should return false for on-line vector which are not on line`() {
    assertFalse(ImmutableVector2.X.isOnLine(ImmutableVector2.Y))
    assertFalse(ImmutableVector2.Y.isOnLine(ImmutableVector2.X))
    assertFalse(ImmutableVector2(2f, 3f).isOnLine(ImmutableVector2(3f, 2f)))
  }

  @Test
  fun `isOnline should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.isOnLine(v2), v2.isOnLine(v1))
      }
    }
  }

  @Test
  fun `isCollinear should return same value as Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().isCollinear(v2.toMutable()), v1.isCollinear(v2))
        scalars.forEach { e ->
          assertEquals(v1.toMutable().isCollinear(v2.toMutable(), e), v1.isCollinear(v2, e))
        }
      }
    }
  }

  @Test
  fun `isCollinear should return false when compared with vector zero`() {
    vectors.forEach { v ->
      assertFalse(v.isCollinear(ImmutableVector2.ZERO))
      assertFalse(ImmutableVector2.ZERO.isCollinear(v))
    }
  }

  @Test
  fun `isCollinear should return true for same vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertTrue(v.isCollinear(v))
    }
  }

  @Test
  fun `isCollinear should return true for same scaled vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertTrue(v.isCollinear(v * 2f))
      assertTrue(v.isCollinear(v * 0.5f))
    }
  }

  @Test
  fun `isCollinear should return false for opposite vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertFalse(v.isCollinear(-v))
      assertFalse(v.isCollinear(v * -2f))
      assertFalse(v.isCollinear(v * -0.5f))
    }
  }

  @Test
  fun `isCollinear should return false for non collinear vectors`() {
    assertFalse(ImmutableVector2.X.isCollinear(-ImmutableVector2.X))
    assertFalse(ImmutableVector2.X.isCollinear(ImmutableVector2.Y))
    assertFalse(ImmutableVector2(2f, 3f).isCollinear(ImmutableVector2(3f, 2f)))
    assertFalse(ImmutableVector2(2f, 3f).isCollinear(ImmutableVector2(-2f, -3f)))
  }

  @Test
  fun `isCollinear should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.isCollinear(v2), v2.isCollinear(v1))
      }
    }
  }

  @Test
  fun `isCollinearOpposite should return same than Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().isCollinearOpposite(v2.toMutable()), v1.isCollinearOpposite(v2))
        scalars.forEach { e ->
          assertEquals(v1.toMutable().isCollinearOpposite(v2.toMutable(), e), v1.isCollinearOpposite(v2, e))
        }
      }
    }
  }

  @Test
  fun `isCollinearOpposite should return false when compared with vector zero`() {
    vectors.forEach { v ->
      assertFalse(v.isCollinearOpposite(ImmutableVector2.ZERO))
      assertFalse(ImmutableVector2.ZERO.isCollinearOpposite(v))
    }
  }

  @Test
  fun `isCollinearOpposite should return false for same vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertFalse(v.isCollinearOpposite(v))
    }
  }

  @Test
  fun `isCollinearOpposite should return false for same scaled vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertFalse(v.isCollinearOpposite(v * 2f))
      assertFalse(v.isCollinearOpposite(v * 0.5f))
    }
  }

  @Test
  fun `isCollinearOpposite should return true for opposite vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertTrue(v.isCollinearOpposite(-v))
      assertTrue(v.isCollinearOpposite(v * -2f))
      assertTrue(v.isCollinearOpposite(v * -0.5f))
    }
  }

  @Test
  fun `isCollinearOpposite should return false for non collinear vectors`() {
    assertFalse(ImmutableVector2.X.isCollinearOpposite(ImmutableVector2.Y))
    assertFalse(ImmutableVector2(2f, 3f).isCollinearOpposite(ImmutableVector2(3f, 2f)))
    assertFalse(ImmutableVector2(2f, 3f).isCollinearOpposite(ImmutableVector2(-3f, -3f)))
  }

  @Test
  fun `isCollinearOpposite should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.isCollinearOpposite(v2), v2.isCollinearOpposite(v1))
      }
    }
  }

  @Test
  fun `isPerpendicular should return same than Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().isPerpendicular(v2.toMutable()), v1.isPerpendicular(v2))
        scalars.forEach { e ->
          assertEquals(v1.toMutable().isPerpendicular(v2.toMutable(), e), v1.isPerpendicular(v2, e))
        }
      }
    }
  }

  @Test
  fun `isPerpendicular should return false for same vector`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertFalse(v.isPerpendicular(v))
    }
  }

  @Test
  fun `isPerpendicular should return false for opposite vector`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertFalse(v.isPerpendicular(-v))
      assertFalse(v.isPerpendicular(v * -2f))
    }
  }

  @Test
  fun `isPerpendicular should return true for axises`() {
    assertTrue(ImmutableVector2.X.isPerpendicular(ImmutableVector2.Y))
    assertTrue(ImmutableVector2.X.isPerpendicular(-ImmutableVector2.Y))
    assertTrue(ImmutableVector2.Y.isPerpendicular(ImmutableVector2.X))
    assertTrue(ImmutableVector2.Y.isPerpendicular(-ImmutableVector2.X))
  }

  @Test
  fun `isPerpendicular should return true for result of 90 degrees rotation`() {
    vectors.filterNot { it.isZero(0f) }.forEach { vector ->
      assertTrue(vector.isPerpendicular(vector.withRotation90(1)))
      assertTrue(vector.isPerpendicular(vector.withRotation90(-1)))
    }
  }

  @Test
  fun `isPerpendicular should return false for non-perpendicular vectors`() {
    assertFalse(ImmutableVector2.X.isPerpendicular(ImmutableVector2(1f, 2f)))
    assertFalse(ImmutableVector2.Y.isPerpendicular(ImmutableVector2(-3f, 2f)))
  }

  @Test
  fun `isPerpendicular should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.isPerpendicular(v2), v2.isPerpendicular(v1))
      }
    }
  }

  @Test
  fun `hasSameDirection should return same than Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().hasSameDirection(v2.toMutable()), v1.hasSameDirection(v2))
      }
    }
  }

  @Test
  fun `hasSameDirection should return false when compared with zero`() {
    vectors.forEach { v ->
      assertFalse(v.hasSameDirection(ImmutableVector2.ZERO))
      assertFalse(ImmutableVector2.ZERO.hasSameDirection(v))
    }
  }

  @Test
  fun `hasSameDirection should return true for same vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertTrue(v.hasSameDirection(v))
    }
  }

  @Test
  fun `hasSameDirection should return true for same scaled vector`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertTrue(v.hasSameDirection(v * 2f))
      assertTrue(v.hasSameDirection(v * 0.5f))
    }
  }

  @Test
  fun `hasSameDirection should return true for same vector rotated by less than 90 degrees`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertTrue(v.hasSameDirection(v.withRotationDeg(80f)))
      assertTrue(v.hasSameDirection(v.withRotationDeg(-80f)))
    }
  }

  @Test
  fun `hasSameDirection should return false for opposite vector`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertFalse(v.hasSameDirection(-v))
      assertFalse(v.hasSameDirection(v * -2f))
      assertFalse(v.hasSameDirection(v * -0.5f))
    }
  }

  @Test
  fun `hasSameDirection should return false for same vector rotated by more than 90 degrees`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertFalse(v.hasSameDirection(v.withRotationDeg(100f)))
      assertFalse(v.hasSameDirection(v.withRotationDeg(-100f)))
    }
  }

  @Test
  fun `hasSameDirection should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.hasSameDirection(v2), v2.hasSameDirection(v1))
      }
    }
  }

  @Test
  fun `hasOppositeDirection should return same than Vector2`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.toMutable().hasOppositeDirection(v2.toMutable()), v1.hasOppositeDirection(v2))
      }
    }
  }

  @Test
  fun `hasOppositeSameDirection should return false when compared with zero`() {
    vectors.forEach { v ->
      assertFalse(v.hasOppositeDirection(ImmutableVector2.ZERO))
      assertFalse(ImmutableVector2.ZERO.hasOppositeDirection(v))
    }
  }

  @Test
  fun `hasOppositeDirection should return false for same vector`() {
    vectors.filterNot { it.isZero() }.forEach { v ->
      assertFalse(v.hasOppositeDirection(v))
    }
  }

  @Test
  fun `hasOppositeDirection should return false for same scaled vector`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertFalse(v.hasOppositeDirection(v * 2f))
      assertFalse(v.hasOppositeDirection(v * 0.5f))
    }
  }

  @Test
  fun `hasOppositeDirection should return false for same vector rotated by less than 90 degrees`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertFalse(v.hasOppositeDirection(v.withRotationDeg(80f)))
      assertFalse(v.hasOppositeDirection(v.withRotationDeg(-80f)))
    }
  }

  @Test
  fun `hasOppositeDirection should return true for opposite vector`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertTrue(v.hasOppositeDirection(-v))
      assertTrue(v.hasOppositeDirection(v * -2f))
      assertTrue(v.hasOppositeDirection(v * -0.5f))
    }
  }

  @Test
  fun `hasOppositeDirection should return true for same vector rotated by more than 90 degrees`() {
    vectors.filterNot { it.isZero(0f) }.forEach { v ->
      assertTrue(v.hasOppositeDirection(v.withRotationDeg(100f)))
      assertTrue(v.hasOppositeDirection(v.withRotationDeg(-100f)))
    }
  }

  @Test
  fun `hasOppositeDirection should be commutative`() {
    vectors.forEach { v1 ->
      vectors.forEach { v2 ->
        assertEquals(v1.hasOppositeDirection(v2), v2.hasOppositeDirection(v1))
      }
    }
  }

  @Test
  fun `copy should return vector with same properties`() {
    vectors.forEach { vector ->
      assertEquals(vector.x, vector.copy(y = 42f).x)
      assertEquals(42f, vector.copy(y = 42f).y)
      assertEquals(42f, vector.copy(x = 42f).x)
      assertEquals(vector.y, vector.copy(x = 42f).y)
      assertEquals(ImmutableVector2(1f, 2f), vector.copy(x = 1f, y = 2f))
    }
  }

  @Test
  fun `should destruct vector into two floats`() {
    vectors.forEach { vector ->
      val (x, y) = vector

      assertEquals(vector.x, x)
      assertEquals(vector.y, y)
    }
  }

  @Test
  fun `should compare vectors by length`() {
    var previousLength = -1f
    var previousLength2 = -1f

    vectors.toList().shuffled().sorted().forEach { vector ->
      assertTrue(vector.len >= previousLength)
      assertTrue(vector.len2 >= previousLength2)
      previousLength = vector.len
      previousLength2 = vector.len2
    }
  }

  @Test
  fun `should increment vector values with ++ operator`() {
    var vector = ImmutableVector2(10f, 10f)

    vector++

    assertEquals(11f, vector.x, MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(11f, vector.y, MathUtils.FLOAT_ROUNDING_ERROR)
  }

  @Test
  fun `should decrement vector values with -- operator`() {
    var vector = ImmutableVector2(10f, 10f)

    vector--

    assertEquals(9f, vector.x, MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(9f, vector.y, MathUtils.FLOAT_ROUNDING_ERROR)
  }

  @Test
  fun `should divide vectors by float scalars with div operator`() {
    val vector = ImmutableVector2(10f, 10f)

    val result = vector / 2.5f

    assertEquals(4f, result.x, MathUtils.FLOAT_ROUNDING_ERROR)
    assertEquals(4f, result.y, MathUtils.FLOAT_ROUNDING_ERROR)
  }
}
