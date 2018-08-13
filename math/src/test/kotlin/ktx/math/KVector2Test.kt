package ktx.math

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import io.kotlintest.forAtLeastOne
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

class KVector2Test {

    /** List of vector to use in tests */
    private val vectors = sequenceOf(

            // vector zero
            KVector2.ZERO,

            // axis
            KVector2.X,
            -KVector2.X,
            KVector2.Y,
            -KVector2.Y,

            // quadrants
            KVector2(3f, 4f),
            KVector2(3f, -4f),
            KVector2(-3f, 4f),
            KVector2(-3f, -4f)
    )

    /** List scalar values to use in tests */
    private val scalars = sequenceOf(0f, Float.MIN_VALUE, 0.42f, 1f, 42f)

    @Test
    fun `toVector2 should return the equivalent Vector2`() {
        vectors.forEach {
            val vector2 = it.toVector2()
            assertEquals(it.x, vector2.x)
            assertEquals(it.y, vector2.y)
            assertEquals(Vector2(it.x, it.y), vector2)
        }
    }

    @Test
    fun `toKVector should return equivalent the KVector2`() {
        vectors.forEach {
            val kvector2 = Vector2(it.x, it.y).toKVector2()
            assertEquals(it.x, kvector2.x)
            assertEquals(it.y, kvector2.y)
            assertEquals(it, kvector2)
        }
    }

    @Test
    fun `fromString should parse result toString`() {
        vectors.forEach {
            assertEquals(it, KVector2.fromString(it.toString()))
            assertEquals(it, KVector2.fromString(it.toVector2().toString()))
        }
    }

    @Test
    fun `len2 should returns same result than Vector2`() {
        vectors.forEach {
            assertEquals(it.toVector2().len2(), it.len2)
        }
    }

    @Test
    fun `isZero should returns same result than Vector2`() {
        vectors.forEach { vector ->
            assertEquals(vector.toVector2().isZero, vector.isZero())
            scalars.filter { it > 0f }.forEach { margin ->
                assertEquals(vector.toVector2().isZero(margin), vector.isZero(margin))
            }
        }
    }

    @Test
    fun `isZero should return true for vector zero`() {
        assertTrue(KVector2.ZERO.isZero())
    }

    @Test
    fun `isZero should return false for non-zero vectors`() {
        assertFalse(KVector2.X.isZero())
        assertFalse((-KVector2.X).isZero())
        assertFalse(KVector2.Y.isZero())
        assertFalse((-KVector2.Y).isZero())
        assertFalse(KVector2(3f, 4f).isZero())
    }

    @Test
    fun `isUnit with margin should return same result than Vector2`() {
        vectors.forEach { vector ->
            assertEquals(vector.toVector2().isUnit, vector.isUnit())
            scalars.filter { it > 0f }.forEach { margin ->
                assertEquals("$vector, $margin", vector.toVector2().isUnit(margin), vector.isUnit(margin))
            }
        }
    }

    @Test
    fun `isUnit should return true for axis`() {
        assertTrue(KVector2.X.isUnit())
        assertTrue((-KVector2.X).isUnit())
        assertTrue(KVector2.Y.isUnit())
        assertTrue((-KVector2.Y).isUnit())
    }

    @Test
    fun `isUnit should return false for zero`() {
        assertFalse(KVector2.ZERO.isUnit())
    }

    @Test
    fun `nor should return same result than Vector2`() {
        vectors.forEach {
            assertEquals(it.toVector2().nor().toKVector2(), it.nor)
        }
    }

    @Test
    fun `unaryMinus should return same result than Vector2 scaled by -1`() {
        vectors.forEach {
            assertEquals(it.toVector2().scl(-1f).toKVector2(), -it)
        }
    }

    @Test
    fun `minus should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().minus(v2.toVector2()).toKVector2(), v1 - v2)
            }
        }
    }

    @Test
    fun `plus should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().plus(v2.toVector2()).toKVector2(), v1 + v2)
            }
        }
    }

    @Test
    fun `times should return same result than Vector2 scale`() {
        scalars.forEach { factor ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().scl(factor).toKVector2(), vector * factor)
            }
        }
    }

    @Test
    fun `scl should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().scl(v2.toVector2()).toKVector2(), v1 * v2)
                assertEquals(v1.toVector2().scl(v2.x, v2.y).toKVector2(), v1.times(v2.x, v2.y))
            }
        }
    }

    @Test
    fun `dot should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().dot(v2.toVector2()), v1.dot(v2))
                assertEquals(v1.toVector2().dot(v2.x, v2.y), v1.dot(v2.x, v2.y))
            }
        }
    }

    @Test
    fun `dst2 should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().dst2(v2.toVector2()), v1.dst2(v2))
                assertEquals(v1.toVector2().dst2(v2.x, v2.y), v1.dst2(v2.x, v2.y))
            }
        }
    }

    @Test
    fun `dst should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().dst(v2.toVector2()), v1.dst(v2))
                assertEquals(v1.toVector2().dst(v2.x, v2.y), v1.dst(v2.x, v2.y))
            }
        }
    }

    @Test
    fun `limit should return same result than Vector2`() {
        scalars.forEach { l ->
            vectors.forEach { v ->
                assertEquals(v.toVector2().limit(l).toKVector2(), v.limit(l))
            }
        }
    }

    @Test
    fun `limit2 should return same result than Vector2`() {
        scalars.forEach { l ->
            vectors.forEach { v ->
                assertEquals(v.toVector2().limit2(l).toKVector2(), v.limit2(l))
            }
        }
    }

    @Test
    fun `withLength should return same result than Vector2`() {
        scalars.forEach { l ->
            vectors.forEach { v ->
                assertEquals(v.toVector2().setLength(l).toKVector2(), v.withLength(l))
            }
        }
    }

    @Test
    fun `withLength2 should return same result than Vector2`() {
        scalars.forEach { l ->
            vectors.forEach { v ->
                assertEquals(v.toVector2().setLength2(l).toKVector2(), v.withLength2(l))
            }
        }
    }

    @Test
    fun `clamp should return same result than Vector2`() {
        scalars.forEach { min ->
            scalars.filter { it >= min }.forEach { max ->
                vectors.forEach { v ->
                    assertEquals(v.toVector2().clamp(min, max).toKVector2(), v.clamp(min, max))
                }
            }
        }
    }

    @Test
    fun `times should return same result than Vector2 mul`() {
        val matrices = listOf(
                mat3(),
                mat3(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f)
        )

        matrices.forEach { matrix ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().mul(matrix).toKVector2(), vector * matrix)
            }
        }
    }

    @Test
    fun `crs should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().crs(v2.toVector2()), v1.crs(v2))
                assertEquals(v1.toVector2().crs(v2.x, v2.y), v1.crs(v2.x, v2.y))
            }
        }
    }

    @Test
    @Ignore("Vector2 has a bug for angles see https://github.com/libgdx/libgdx/issues/5385")
    fun `angle should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().angle(v2.toVector2()), v1.angle(v2))
                assertEquals(v1.toVector2().angle(v2.toVector2()), v1.angle(v2.x, v2.y))
            }
        }
    }

    @Test
    @Ignore("Vector2 has a bug for angles see https://github.com/libgdx/libgdx/issues/5385")
    fun `angleRad should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().angle(v2.toVector2()), v1.angle(v2))
                assertEquals(v1.toVector2().angle(v2.toVector2()), v1.angle(v2.x, v2.y))
            }
        }
    }

    @Test
    fun `angle should return correct angles between axises`() {
        assertEquals(90f, (KVector2.Y).angle(KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
        assertEquals(90f, (-KVector2.X).angle(KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
        assertEquals(90f, (-KVector2.Y).angle(-KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
        assertEquals(90f, (KVector2.X).angle(-KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

        assertEquals(-90f, (KVector2.Y).angle(-KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
        assertEquals(-90f, (-KVector2.X).angle(-KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
        assertEquals(-90f, (-KVector2.Y).angle(KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
        assertEquals(-90f, (KVector2.X).angle(KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

        assertEquals(180f, (KVector2.X).angle(-KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
        assertEquals(180f, (KVector2.Y).angle(-KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
    }

    @Test
    fun `angle should return value between -180 and 180`() {
        vectors.forEach { v1 ->
            assertTrue(v1.angle() in (-180f)..(180f))
            vectors.forEach { v2 ->
                assertTrue(v1.angle(v2) in (-180f)..(180f))
            }
        }
    }

    @Test
    fun `angle should return angle of applied rotation`() {
        vectors.filter { it.len2.isFinite() && it.len2 > 0.5 }.forEach { initialVector ->

            // rotate from positive x-axis to positive y-axis (typically counter-clockwise)
            var deltaAngle = 60f
            var previousVector = initialVector
            var currentVector = previousVector.rotate(deltaAngle)
            assertEquals(deltaAngle, currentVector.angle(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

            repeat(4) {
                previousVector = currentVector
                currentVector = previousVector.rotate(deltaAngle)
                assertEquals(deltaAngle, currentVector.angle(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
            }

            // rotate from positive x-axis to negative y-axis (typically clockwise)
            deltaAngle = -60f
            previousVector = initialVector
            currentVector = previousVector.rotate(deltaAngle)
            assertEquals(deltaAngle, currentVector.angle(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

            repeat(4) {
                previousVector = currentVector
                currentVector = previousVector.rotate(deltaAngle)
                assertEquals(deltaAngle, currentVector.angle(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
            }
        }
    }

    @Test
    fun `angleRad to x-axis should return same result than Vector2`() {
        vectors.forEach { v1 ->
            assertEquals("$v1", v1.toVector2().angleRad(), v1.angleRad())
        }
    }

    @Test
    fun `angleRad should return correct angles between axises`() {
        assertEquals(MathUtils.PI / 2f, (KVector2.Y).angleRad(KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(MathUtils.PI / 2f, (-KVector2.X).angleRad(KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(MathUtils.PI / 2f, (-KVector2.Y).angleRad(-KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(MathUtils.PI / 2f, (KVector2.X).angleRad(-KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)

        assertEquals(-MathUtils.PI / 2f, (KVector2.Y).angleRad(-KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(-MathUtils.PI / 2f, (-KVector2.X).angleRad(-KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(-MathUtils.PI / 2f, (-KVector2.Y).angleRad(KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(-MathUtils.PI / 2f, (KVector2.X).angleRad(KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)

        assertEquals(MathUtils.PI, (KVector2.X).angleRad(-KVector2.X), MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(MathUtils.PI, (KVector2.Y).angleRad(-KVector2.Y), MathUtils.FLOAT_ROUNDING_ERROR)
    }

    @Test
    fun `angleRad should returns value between -PI and PI`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
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
            var currentVector = previousVector.rotateRad(deltaAngle)
            assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)

            repeat(4) {
                previousVector = currentVector
                currentVector = previousVector.rotateRad(deltaAngle)
                assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)
            }

            // rotate from positive x-axis to negative y-axis (typically clockwise)
            deltaAngle = -MathUtils.PI / 3
            previousVector = initialVector
            currentVector = previousVector.rotateRad(deltaAngle)
            assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)

            repeat(4) {
                previousVector = currentVector
                currentVector = previousVector.rotateRad(deltaAngle)
                assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)
            }
        }
    }

    @Test
    fun `angleRad with x axis should return same result than Vector2`() {
        vectors.forEach {
            assertEquals(it.toVector2().angleRad(), it.angleRad())
        }
    }

    @Test
    fun `withAngle should return same result than Vector2`() {
        scalars.forEach { angle ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().setAngle(angle).toKVector2(), vector.withAngle(angle))
            }
        }
    }

    @Test
    fun `withAngleRad should return same result than Vector2`() {
        scalars.forEach { angle ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().setAngleRad(angle).toKVector2(), vector.withAngleRad(angle))
            }
        }
    }

    @Test
    fun `rotate with x axis should return same result than Vector2`() {
        scalars.forEach { angle ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().rotate(angle).toKVector2(), vector.rotate(angle))
            }
        }
    }

    @Test
    fun `rotateRad with x axis should return same result than Vector2`() {
        scalars.forEach { angle ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().rotateRad(angle).toKVector2(), vector.rotateRad(angle))
            }
        }
    }

    @Test
    fun `rotate90 with x axis should return same result than Vector2`() {
        vectors.forEach { vector ->
            assertEquals(vector.toVector2().rotate90(-1).toKVector2(), vector.rotate90(-1))
            assertEquals(vector.toVector2().rotate90(1).toKVector2(), vector.rotate90(1))
        }
    }

    @Test
    fun `lerp should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                scalars.forEach { ratio ->
                    assertEquals(v1.toVector2().lerp(v2.toVector2(), ratio).toKVector2(), v1.lerp(v2, ratio))
                }
            }
        }
    }

    @Test
    fun `interpolate should return same result than Vector2`() {
        val interpolations = sequenceOf(
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
                Interpolation.smooth2
        )

        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                scalars.forEach { alpha ->
                    interpolations.forEach { interpolation ->
                        assertEquals(
                                v1.toVector2().interpolate(v2.toVector2(), alpha, interpolation).toKVector2(),
                                v1.interpolate(v2, alpha, interpolation)
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `randomDirection should return a vector of same length`() {
        vectors.forEach { vector ->
            assertEquals(vector.withRandomDirection().len, vector.len, MathUtils.FLOAT_ROUNDING_ERROR)
        }
    }

    @Test
    fun `randomDirection should return a variety of random vectors`() {
        val vectors = HashSet<KVector2>()
        repeat(1000) {
            vectors += KVector2.X.withRandomDirection()
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
                    assertEquals(v1.toVector2().epsilonEquals(v2.toVector2(), e), v1.epsilonEquals(v2, e))
                    assertEquals(v1.toVector2().epsilonEquals(v2.x, v2.y, e), v1.epsilonEquals(v2.x, v2.y, e))
                }
            }
        }
    }

    @Test
    fun `isOnLine should return same than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().isOnLine(v2.toVector2()), v1.isOnLine(v2))
                scalars.forEach { e ->
                    assertEquals(v1.toVector2().isOnLine(v2.toVector2(), e), v1.isOnLine(v2, e))
                }
            }
        }
    }

    @Test
    fun `isCollinear should return same than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().isCollinear(v2.toVector2()), v1.isCollinear(v2))
                scalars.forEach { e ->
                    assertEquals(v1.toVector2().isCollinear(v2.toVector2(), e), v1.isCollinear(v2, e))
                }
            }
        }
    }

    @Test
    fun `isCollinearOpposite should return same than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().isCollinearOpposite(v2.toVector2()), v1.isCollinearOpposite(v2))
                scalars.forEach { e ->
                    assertEquals(v1.toVector2().isCollinearOpposite(v2.toVector2(), e), v1.isCollinearOpposite(v2, e))
                }
            }
        }
    }

    @Test
    fun `isPerpendicular should return same than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().isPerpendicular(v2.toVector2()), v1.isPerpendicular(v2))
                scalars.forEach { e ->
                    assertEquals(v1.toVector2().isPerpendicular(v2.toVector2(), e), v1.isPerpendicular(v2, e))
                }
            }
        }
    }

    @Test
    fun `hasSameDirection should return same than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().hasSameDirection(v2.toVector2()), v1.hasSameDirection(v2))
            }
        }
    }

    @Test
    fun `hasOppositeDirection should return same than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().hasOppositeDirection(v2.toVector2()), v1.hasOppositeDirection(v2))
            }
        }
    }
}
