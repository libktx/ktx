package ktx.reflect

import com.badlogic.gdx.utils.reflect.ReflectionException
import io.kotlintest.matchers.shouldThrow
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

interface Super

open class Tested : Super

annotation class Annotation(
  val value: String,
)

@Annotation("Test")
data class Extension(
  val a: String,
  val b: String,
) : Tested() {
  @Suppress("unused")
  fun test(c: String): String = a + b + c
}

@Suppress("unused")
class Container(
  @JvmField val field: String,
)

enum class Enum { RED, BLUE }

@OptIn(Reflection::class)
class ReflectTest {
  @Test
  fun `should wrap selected class`() {
    // When:
    val reflectedClass = reflect<Tested>()

    // Then:
    assertSame(Tested::class.java, reflectedClass.javaClass)
  }

  @Test
  fun `should wrap selected Kotlin class`() {
    // When:
    val reflectedClass = reflect(Tested::class)

    // Then:
    assertSame(Tested::class.java, reflectedClass.javaClass)
  }

  @Test
  fun `should wrap selected Java class`() {
    // When:
    val reflectedClass = reflect(Tested::class.java)

    // Then:
    assertSame(Tested::class.java, reflectedClass.javaClass)
  }

  @Test
  fun `should wrap selected class by name`() {
    // When:
    val reflectedClass = reflect("ktx.reflect.Tested")

    // Then:
    assertSame(Tested::class.java, reflectedClass.javaClass)
  }

  @Test
  fun `should wrap selected Kotlin class via constructor`() {
    // When:
    val reflectedClass = ReflectedClass(Tested::class)

    // Then:
    assertSame(Tested::class.java, reflectedClass.javaClass)
  }

  @Test
  fun `should return simple name`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val name = reflectedClass.simpleName

    // Then:
    assertEquals("Tested", name)
  }

  @Test
  fun `should test if an object is an instance of a class`() {
    // Given:
    val reflectedClass = reflect<Tested>()
    val instance = Tested()

    // When:
    val isInstance = reflectedClass.isInstance(instance)

    // Then:
    assertTrue(isInstance)
  }

  @Test
  fun `should test if an object is not an instance of a class`() {
    // Given:
    val reflectedClass = reflect<Tested>()
    val instance = "Tested"

    // When:
    val isInstance = reflectedClass.isInstance(instance)

    // Then:
    assertFalse(isInstance)
  }

  @Test
  fun `should test if null is an instance of a class`() {
    // Given:
    val reflectedClass = reflect<Tested>()
    val instance = null

    // When:
    val isInstance = reflectedClass.isInstance(instance)

    // Then:
    assertFalse(isInstance)
  }

  @Test
  fun `should test if is assignable from the same class`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isAssignable = reflectedClass.isAssignableFrom<Tested>()

    // Then:
    assertTrue(isAssignable)
  }

  @Test
  fun `should test if is assignable from a class extension`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isAssignable = reflectedClass.isAssignableFrom<Extension>()

    // Then:
    assertTrue(isAssignable)
  }

  @Test
  fun `should test if is assignable from a different class`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isAssignable = reflectedClass.isAssignableFrom<String>()

    // Then:
    assertFalse(isAssignable)
  }

  @Test
  fun `should check if is a member class`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isMemberClass = reflectedClass.isMemberClass

    // Then:
    assertFalse(isMemberClass)
  }

  @Test
  fun `should check if is a static class`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isStatic = reflectedClass.isStatic

    // Then:
    assertFalse(isStatic)
  }

  @Test
  fun `should check if is an array class`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isArray = reflectedClass.isArray

    // Then:
    assertFalse(isArray)
  }

  @Test
  fun `should check if is an array class given an array`() {
    // Given:
    val reflectedClass = reflect<Array<Tested>>()

    // When:
    val isArray = reflectedClass.isArray

    // Then:
    assertTrue(isArray)
  }

  @Test
  fun `should check if is a primitive class`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isPrimitive = reflectedClass.isPrimitive

    // Then:
    assertFalse(isPrimitive)
  }

  @Test
  fun `should check if is an enum`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isEnum = reflectedClass.isEnum

    // Then:
    assertFalse(isEnum)
  }

  @Test
  fun `should check if is an enum given an enum`() {
    // Given:
    val reflectedClass = reflect<Enum>()

    // When:
    val isEnum = reflectedClass.isEnum

    // Then:
    assertTrue(isEnum)
  }

  @Test
  fun `should check if is an annotation`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isAnnotation = reflectedClass.isAnnotation

    // Then:
    assertFalse(isAnnotation)
  }

  @Test
  fun `should check if is an annotation class given an annotation`() {
    // Given:
    val reflectedClass = reflect<Override>()

    // When:
    val isAnnotation = reflectedClass.isAnnotation

    // Then:
    assertTrue(isAnnotation)
  }

  @Test
  fun `should check if is an interface`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isInterface = reflectedClass.isInterface

    // Then:
    assertFalse(isInterface)
  }

  @Test
  fun `should check if is an interface class given an interface`() {
    // Given:
    val reflectedClass = reflect<Super>()

    // When:
    val isInterface = reflectedClass.isInterface

    // Then:
    assertTrue(isInterface)
  }

  @Test
  fun `should check if is an abstract class`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isAbstract = reflectedClass.isAbstract

    // Then:
    assertFalse(isAbstract)
  }

  @Test
  fun `should check if is an abstract class given an interface`() {
    // Given:
    val reflectedClass = reflect<Super>()

    // When:
    val isAbstract = reflectedClass.isAbstract

    // Then:
    assertTrue(isAbstract)
  }

  @Test
  fun `should return no component type if the class is not an array`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val componentType = reflectedClass.componentType

    // Then:
    assertNull(componentType)
  }

  @Test
  fun `should return no type if the class is an array`() {
    // Given:
    val reflectedClass = reflect<Array<Tested>>()

    // When:
    val componentType = reflectedClass.componentType

    // Then:
    assertSame(Tested::class.java, componentType)
  }

  @Test
  fun `should find constructors`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val constructors = reflectedClass.constructors

    // Then:
    assertEquals(1, constructors.size)
    assertEquals(0, constructors.first().parameterTypes.size)
  }

  @Test
  fun `should find the only constructor`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val constructor = reflectedClass.constructor

    // Then:
    assertEquals(0, constructor.parameterTypes.size)
    assertTrue(constructor.newInstance() is Tested)
  }

  @Test
  fun `should fail to find a single constructor if a class has multiple constructors`() {
    // Given:
    val reflectedClass = reflect<String>()

    // Expect:
    shouldThrow<ReflectionException> {
      reflectedClass.constructor
    }
  }

  @Test
  fun `should find methods`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val methods = reflectedClass.methods

    // Then:
    assertEquals(Extension::class.java.methods.size, methods.size)
  }

  @Test
  fun `should find declared methods`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val methods = reflectedClass.declaredMethods

    // Then:
    assertEquals(Extension::class.java.declaredMethods.size, methods.size)
  }

  @Test
  fun `should find fields`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val fields = reflectedClass.fields

    // Then: should not find private backing fields:
    val fieldNames = fields.map { it.name }.toSet()
    assertFalse("a" in fieldNames)
    assertFalse("b" in fieldNames)
  }

  @Test
  fun `should find declared fields`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val fields = reflectedClass.declaredFields

    // Then: should return custom fields plus internal field.
    val fieldNames = fields.map { it.name }.toSet()
    assertTrue("a" in fieldNames)
    assertTrue("b" in fieldNames)
  }

  @Test
  fun `should find annotations`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val annotations = reflectedClass.annotations.map { it.annotationType }.toSet()

    // Then:
    assertEquals(setOf(Annotation::class.java, Metadata::class.java), annotations)
  }

  @Test
  fun `should find declared annotations`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val annotations = reflectedClass.declaredAnnotations.map { it.annotationType }.toSet()

    // Then:
    assertEquals(setOf(Annotation::class.java, Metadata::class.java), annotations)
  }

  @Test
  fun `should find interfaces`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val interfaces = reflectedClass.interfaces

    // Then:
    assertArrayEquals(arrayOf(Super::class.java), interfaces)
  }

  @Test
  fun `should find enum constants`() {
    // Given:
    val reflectedClass = reflect<Enum>()

    // When:
    val constants = reflectedClass.enumConstants

    // Then:
    assertArrayEquals(arrayOf(Enum.RED, Enum.BLUE), constants)
  }

  @Test
  fun `should not find enum constants if the class is not an enum`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val constants = reflectedClass.enumConstants

    // Then:
    assertNull(constants)
  }

  @Test
  fun `should create a new instance`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val instance = reflectedClass.newInstance()

    // Then:
    assertNotNull(instance)
    assertNotSame(instance, reflectedClass.newInstance())
  }

  @Test
  fun `should create an array instance`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val array = reflectedClass.newArrayInstance(3)

    // Then:
    assertArrayEquals(arrayOfNulls(3), array)
  }

  @Test
  fun `should create an array instance with 0 elements`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val array = reflectedClass.newArrayInstance(0)

    // Then:
    assertArrayEquals(arrayOfNulls(0), array)
  }

  @Test
  fun `should not create an array instance with negative size `() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    shouldThrow<ReflectionException> {
      reflectedClass.newArrayInstance(-1)
    }
  }

  @Test
  fun `should fail to create a new instance if a class does not have a public no-arg constructor`() {
    // Given:
    val reflectedClass = reflect<Int>()

    // Expect:
    shouldThrow<ReflectionException> {
      reflectedClass.newInstance()
    }
  }

  @Test
  fun `should find constructor given parameter types`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val constructor = reflectedClass.getConstructor(String::class, String::class)

    // Then:
    assertArrayEquals(arrayOf(String::class.java, String::class.java), constructor.parameterTypes)
    assertEquals(Extension("a", "b"), constructor.newInstance("a", "b"))
  }

  @Test
  fun `should find declared constructor given parameter types`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val constructor = reflectedClass.getDeclaredConstructor(String::class, String::class)

    // Then:
    assertArrayEquals(arrayOf(String::class.java, String::class.java), constructor.parameterTypes)
    assertEquals(Extension("a", "b"), constructor.newInstance("a", "b"))
  }

  @Test
  fun `should find a method given name and parameter types`() {
    // Given:
    val reflectedClass = reflect<Extension>()
    val instance = Extension("a", "b")

    // When:
    val method = reflectedClass.getMethod("test", String::class)

    // Then:
    assertEquals("test", method.name)
    assertArrayEquals(arrayOf(String::class.java), method.parameterTypes)
    assertEquals("abc", method.invoke(instance, "c"))
  }

  @Test
  fun `should find a declared method given name and parameter types`() {
    // Given:
    val reflectedClass = reflect<Extension>()
    val instance = Extension("a", "b")

    // When:
    val method = reflectedClass.getDeclaredMethod("test", String::class)

    // Then:
    assertEquals("test", method.name)
    assertArrayEquals(arrayOf(String::class.java), method.parameterTypes)
    assertEquals("abc", method.invoke(instance, "c"))
  }

  @Test
  fun `should find a field by name`() {
    // Given:
    val reflectedClass = reflect<Container>()
    val instance = Container("test")

    // When:
    val field = reflectedClass.getField("field")

    // Then:
    assertEquals("field", field.name)
    assertSame(String::class.java, field.type)
    assertEquals("test", field.get(instance))
  }

  @Test
  fun `should find a declared field by name`() {
    // Given:
    val reflectedClass = reflect<Container>()
    val instance = Container("test")

    // When:
    val field = reflectedClass.getDeclaredField("field")

    // Then:
    assertEquals("field", field.name)
    assertSame(String::class.java, field.type)
    assertEquals("test", field.get(instance))
  }

  @Test
  fun `should check if annotation is present`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val isPresent = reflectedClass.isAnnotationPresent<Annotation>()

    // Then:
    assertTrue(isPresent)
  }

  @Test
  fun `should check if annotation is not present`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val isPresent = reflectedClass.isAnnotationPresent<Annotation>()

    // Then:
    assertFalse(isPresent)
  }

  @Test
  fun `should find an annotation by type`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val annotation = reflectedClass.getAnnotation<Annotation>()

    // Then:
    assertNotNull(annotation)
    assertEquals("Test", annotation?.get<Annotation>()?.value)
  }

  @Test
  fun `should not find an annotation by type`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val annotation = reflectedClass.getAnnotation<Annotation>()

    // Then:
    assertNull(annotation)
  }

  @Test
  fun `should find a declared annotation by type`() {
    // Given:
    val reflectedClass = reflect<Extension>()

    // When:
    val annotation = reflectedClass.getDeclaredAnnotation<Annotation>()

    // Then:
    assertNotNull(annotation)
    assertEquals("Test", annotation?.get<Annotation>()?.value)
  }

  @Test
  fun `should not find a declared annotation by type`() {
    // Given:
    val reflectedClass = reflect<Tested>()

    // When:
    val annotation = reflectedClass.getDeclaredAnnotation<Annotation>()

    // Then:
    assertNull(annotation)
  }
}
