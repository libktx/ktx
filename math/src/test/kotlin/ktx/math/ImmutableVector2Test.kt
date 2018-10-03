package ktx.math

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import io.kotlintest.forAtLeastOne
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

class ImmutableVector2Test {

    /** List of vector to use in tests */
    private val vectors = sequenceOf(

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
            ImmutableVector2(0f, -0.001f)
    )

    /** List scalar values to use in tests */
    private val scalars = sequenceOf(0f, Float.MIN_VALUE, 0.42f, 1f, 42f)

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
    fun `equals should return true for equivalent vector`() {
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
    fun `toVector2 should return the equivalent Vector2`() {
        vectors.forEach {
            val vector2 = it.toVector2()
            assertEquals(it.x, vector2.x)
            assertEquals(it.y, vector2.y)
            assertEquals(Vector2(it.x, it.y), vector2)
        }
    }

    @Test
    fun `toImmutableVector should return an equivalent of the Vector2`() {
        vectors.forEach {
            val vector2 = Vector2(it.x, it.y).toImmutableVector2()
            assertEquals(it.x, vector2.x)
            assertEquals(it.y, vector2.y)
            assertEquals(it, vector2)
        }
    }

    @Test
    fun `fromString should parse result toString`() {
        vectors.forEach {
            assertEquals(it, ImmutableVector2.fromString(it.toString()))
            assertEquals(it, ImmutableVector2.fromString(it.toVector2().toString()))
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
        assertTrue(ImmutableVector2.X.isUnit())
        assertTrue((-ImmutableVector2.X).isUnit())
        assertTrue(ImmutableVector2.Y.isUnit())
        assertTrue((-ImmutableVector2.Y).isUnit())
    }

    @Test
    fun `isUnit should return false for zero`() {
        assertFalse(ImmutableVector2.ZERO.isUnit())
    }

    @Test
    fun `nor should return same result than Vector2`() {
        vectors.forEach {
            assertEquals(it.toVector2().nor().toImmutableVector2(), it.nor)
        }
    }

    @Test
    fun `unaryMinus should return same result than Vector2 scaled by -1`() {
        vectors.forEach {
            assertEquals(it.toVector2().scl(-1f).toImmutableVector2(), -it)
        }
    }

    @Test
    fun `minus should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().minus(v2.toVector2()).toImmutableVector2(), v1 - v2)
            }
        }
    }

    @Test
    fun `plus should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().plus(v2.toVector2()).toImmutableVector2(), v1 + v2)
            }
        }
    }

    @Test
    fun `times should return same result than Vector2 scale`() {
        scalars.forEach { factor ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().scl(factor).toImmutableVector2(), vector * factor)
            }
        }
    }

    @Test
    fun `scl should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().scl(v2.toVector2()).toImmutableVector2(), v1 * v2)
                assertEquals(v1.toVector2().scl(v2.x, v2.y).toImmutableVector2(), v1.times(v2.x, v2.y))
            }
        }
    }

    @Test
    fun `dot should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().dot(v2.toVector2()), v1 dot v2)
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
                assertEquals(v.toVector2().limit(l).toImmutableVector2(), v.limit(l))
            }
        }
    }

    @Test
    fun `limit2 should return same result than Vector2`() {
        scalars.forEach { l ->
            vectors.forEach { v ->
                assertEquals(v.toVector2().limit2(l).toImmutableVector2(), v.limit2(l))
            }
        }
    }

    @Test
    fun `withLength should return same result than Vector2`() {
        scalars.forEach { l ->
            vectors.forEach { v ->
                assertEquals(v.toVector2().setLength(l).toImmutableVector2(), v.withLength(l))
            }
        }
    }

    @Test
    fun `withLength2 should return same result than Vector2`() {
        scalars.forEach { l ->
            vectors.forEach { v ->
                assertEquals(v.toVector2().setLength2(l).toImmutableVector2(), v.withLength2(l))
            }
        }
    }

    @Test
    fun `clamp should return same result than Vector2`() {
        scalars.forEach { min ->
            scalars.filter { it >= min }.forEach { max ->
                vectors.forEach { v ->
                    assertEquals(v.toVector2().clamp(min, max).toImmutableVector2(), v.clamp(min, max))
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
                assertEquals(vector.toVector2().mul(matrix).toImmutableVector2(), vector * matrix)
            }
        }
    }

    @Test
    fun `crs should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().crs(v2.toVector2()), v1 x v2)
                assertEquals(v1.toVector2().crs(v2.toVector2()), v1.crs(v2))
                assertEquals(v1.toVector2().crs(v2.x, v2.y), v1.crs(v2.x, v2.y))
            }
        }
    }

    @Test
    @Ignore("Vector2 has a bug for angles see https://github.com/libgdx/libgdx/issues/5385")
    fun `angleDeg should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().angle(v2.toVector2()), v1.angleDeg(v2))
                assertEquals(v1.toVector2().angle(v2.toVector2()), v1.angleDeg(v2.x, v2.y))
            }
        }
    }

    @Test
    @Ignore("Vector2 has a bug for angles see https://github.com/libgdx/libgdx/issues/5385")
    fun `angleRad should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                assertEquals(v1.toVector2().angle(v2.toVector2()), v1.angleDeg(v2))
                assertEquals(v1.toVector2().angle(v2.toVector2()), v1.angleDeg(v2.x, v2.y))
            }
        }
    }

    @Test
    fun `angleDeg should return correct angles between axises`() {
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
    fun `angleDeg should return value between -180 and 180`() {
        vectors.forEach { v1 ->
            assertTrue(v1.angleDeg() in (-180f)..(180f))
            vectors.forEach { v2 ->
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
            var currentVector = previousVector.rotateDeg(deltaAngle)
            assertEquals(deltaAngle, currentVector.angleDeg(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

            repeat(4) {
                previousVector = currentVector
                currentVector = previousVector.rotateDeg(deltaAngle)
                assertEquals(deltaAngle, currentVector.angleDeg(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
            }

            // rotate from positive x-axis to negative y-axis (typically clockwise)
            deltaAngle = -60f
            previousVector = initialVector
            currentVector = previousVector.rotateDeg(deltaAngle)
            assertEquals(deltaAngle, currentVector.angleDeg(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)

            repeat(4) {
                previousVector = currentVector
                currentVector = previousVector.rotateDeg(deltaAngle)
                assertEquals(deltaAngle, currentVector.angleDeg(previousVector), MathUtils.FLOAT_ROUNDING_ERROR * MathUtils.radiansToDegrees)
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
            assertEquals(deltaAngle, currentVector.angleRad(previousVector.x, previousVector.y), MathUtils.FLOAT_ROUNDING_ERROR)

            repeat(4) {
                previousVector = currentVector
                currentVector = previousVector.rotateRad(deltaAngle)
                assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)
                assertEquals(deltaAngle, currentVector.angleRad(previousVector.x, previousVector.y), MathUtils.FLOAT_ROUNDING_ERROR)
            }

            // rotate from positive x-axis to negative y-axis (typically clockwise)
            deltaAngle = -MathUtils.PI / 3
            previousVector = initialVector
            currentVector = previousVector.rotateRad(deltaAngle)
            assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)
            assertEquals(deltaAngle, currentVector.angleRad(previousVector.x, previousVector.y), MathUtils.FLOAT_ROUNDING_ERROR)

            repeat(4) {
                previousVector = currentVector
                currentVector = previousVector.rotateRad(deltaAngle)
                assertEquals(deltaAngle, currentVector.angleRad(previousVector), MathUtils.FLOAT_ROUNDING_ERROR)
                assertEquals(deltaAngle, currentVector.angleRad(previousVector.x, previousVector.y), MathUtils.FLOAT_ROUNDING_ERROR)
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
                assertEquals(vector.toVector2().setAngle(angle).toImmutableVector2(), vector.withAngleDeg(angle))
            }
        }
    }

    @Test
    fun `withAngleRad should return same result than Vector2`() {
        scalars.forEach { angle ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().setAngleRad(angle).toImmutableVector2(), vector.withAngleRad(angle))
            }
        }
    }

    @Test
    fun `rotateDeg should return same result than Vector2`() {
        scalars.forEach { angle ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().rotate(angle).toImmutableVector2(), vector.rotateDeg(angle))
            }
        }
    }

    @Test
    fun `rotateRad should return same result than Vector2`() {
        scalars.forEach { angle ->
            vectors.forEach { vector ->
                assertEquals(vector.toVector2().rotateRad(angle).toImmutableVector2(), vector.rotateRad(angle))
            }
        }
    }

    @Test
    fun `rotate90 should return same result than Vector2`() {
        vectors.forEach { vector ->
            assertEquals(vector.toVector2().rotate90(-1).toImmutableVector2(), vector.rotate90(-1))
            assertEquals(vector.toVector2().rotate90(1).toImmutableVector2(), vector.rotate90(1))
        }
    }

    @Test
    fun `lerp should return same result than Vector2`() {
        vectors.forEach { v1 ->
            vectors.forEach { v2 ->
                scalars.forEach { ratio ->
                    assertEquals(v1.toVector2().lerp(v2.toVector2(), ratio).toImmutableVector2(), v1.lerp(v2, ratio))
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
                                v1.toVector2().interpolate(v2.toVector2(), alpha, interpolation).toImmutableVector2(),
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
        val vectors = HashSet<ImmutableVector2>()
        repeat(1000) {
            vectors += ImmutableVector2.X.withRandomDirection()
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
        val vector = Vector2(10f, 10f)

        val result = vector / 2.5f

        assertEquals(4f, result.x, MathUtils.FLOAT_ROUNDING_ERROR)
        assertEquals(4f, result.y, MathUtils.FLOAT_ROUNDING_ERROR)
    }
}
