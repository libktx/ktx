package ktx.reflect

import com.badlogic.gdx.utils.reflect.Annotation
import com.badlogic.gdx.utils.reflect.ArrayReflection
import com.badlogic.gdx.utils.reflect.ClassReflection
import com.badlogic.gdx.utils.reflect.Constructor
import com.badlogic.gdx.utils.reflect.Field
import com.badlogic.gdx.utils.reflect.Method
import com.badlogic.gdx.utils.reflect.ReflectionException
import kotlin.reflect.KClass
import kotlin.Annotation as JavaAnnotation

@RequiresOptIn(
  message = """This functionality uses reflection.
    
APIs annotated with @Reflection rely on reflection and can throw 
com.badlogic.gdx.utils.reflect.ReflectionException when unable to
complete an action. Reflection usage requires opt-in with
@OptIn(Reflection::class) or propagating the warnings further
with a @Reflection annotation.

Note that reflection might not be fully supported by all platforms,
especially third-party libGDX backends. Some platforms might require
additional setup. For example, all classes that are instantiated
solely by reflection must be listed in the robovm.xml file within
the ForceLinkClasses section in order to work on iOS.

Further reading:
* https://libgdx.com/wiki/utils/reflection
* https://kotlinlang.org/docs/opt-in-requirements.html
""",
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Reflection

/**
 * Inlined wrapper for a [Class] providing reflection API using libGDX reflection utilities.
 *
 * To ensure cross-platform compatibility, libGDX reflection API should be used over direct
 * [Class] and [KClass] usage.
 */
@JvmInline
@Reflection
value class ReflectedClass<T : Any>(
  val javaClass: Class<T>,
) {
  /** @see Class.getSimpleName */
  val simpleName: String
    get() = ClassReflection.getSimpleName(javaClass)

  /** @see Class.isMemberClass */
  val isMemberClass: Boolean
    get() = ClassReflection.isMemberClass(javaClass)

  /** @see java.lang.reflect.Modifier.isStatic */
  val isStatic: Boolean
    get() = ClassReflection.isStaticClass(javaClass)

  /** @see Class.isArray */
  val isArray: Boolean
    get() = ClassReflection.isArray(javaClass)

  /** @see Class.isPrimitive */
  val isPrimitive: Boolean
    get() = ClassReflection.isPrimitive(javaClass)

  /** @see Class.isEnum */
  val isEnum: Boolean
    get() = ClassReflection.isEnum(javaClass)

  /** @see Class.isAnnotation */
  val isAnnotation: Boolean
    get() = ClassReflection.isAnnotation(javaClass)

  /** @see Class.isInterface */
  val isInterface: Boolean
    get() = ClassReflection.isInterface(javaClass)

  /** @see java.lang.reflect.Modifier.isAbstract */
  val isAbstract: Boolean
    get() = ClassReflection.isAbstract(javaClass)

  /** @see Class.getComponentType */
  val componentType: Class<*>?
    get() = ClassReflection.getComponentType(javaClass)

  /** @see Class.getConstructors */
  val constructors: Array<Constructor>
    get() = ClassReflection.getConstructors(javaClass)

  /**
   * Accesses constructor of the class using libGDX reflection API.
   * @throws ReflectionException when unable to read constructors, when no constructors are
   * found, or when more than 1 constructor is found.
   */
  val constructor: Constructor
    get() {
      val constructors = ClassReflection.getConstructors(javaClass)
      return if (constructors.size == 1) {
        constructors.first()
      } else {
        throw ReflectionException(
          "Expected constructors for class $simpleName: 1, found: ${constructors.size}.",
        )
      }
    }

  /** @see Class.getMethods */
  val methods: Array<Method>
    get() = ClassReflection.getMethods(javaClass)

  /** @see Class.getDeclaredMethods */
  val declaredMethods: Array<Method>
    get() = ClassReflection.getDeclaredMethods(javaClass)

  /** @see Class.getFields */
  val fields: Array<Field>
    get() = ClassReflection.getFields(javaClass)

  /** @see Class.getDeclaredFields */
  val declaredFields: Array<Field>
    get() = ClassReflection.getDeclaredFields(javaClass)

  /** @see Class.getAnnotations */
  val annotations: Array<Annotation>
    get() = ClassReflection.getAnnotations(javaClass)

  /** @see Class.getDeclaredAnnotations */
  val declaredAnnotations: Array<Annotation>
    get() = ClassReflection.getDeclaredAnnotations(javaClass)

  /** @see Class.getInterfaces */
  val interfaces: Array<Class<*>>
    get() = ClassReflection.getInterfaces(javaClass)

  /** @see Class.getEnumConstants */
  val enumConstants: Array<Any>?
    get() = ClassReflection.getEnumConstants(javaClass)

  /**
   * Extracts [javaClass] from the given [kotlinClass].
   */
  constructor(kotlinClass: KClass<T>) : this(kotlinClass.java)

  /**
   * @param value an object to be tested.
   * @return true if the [value] object is an instance of the wrapped [javaClass].
   * @see KClass.isInstance
   */
  fun isInstance(value: Any?): Boolean = ClassReflection.isInstance(javaClass, value)

  /**
   * @param T class to test.
   * @return true if the wrapped [javaClass] is either the same as, or is a superclass or superinterface of [T].
   */
  inline fun <reified T> isAssignableFrom(): Boolean = isAssignableFrom(T::class)

  /**
   * @param fromClass class to test.
   * @return true if the wrapped [javaClass] is either the same as, or is a superclass or superinterface of [fromClass].
   */
  fun isAssignableFrom(fromClass: KClass<*>): Boolean = isAssignableFrom(fromClass.java)

  /**
   * @param fromClass class to test.
   * @return true if the wrapped [javaClass] is either the same as, or is a superclass or superinterface of [fromClass].
   */
  fun isAssignableFrom(fromClass: Class<*>): Boolean = ClassReflection.isAssignableFrom(javaClass, fromClass)

  /**
   * Attempts to create a new instance of the [javaClass] with the no-arg constructor.
   * @return a new instance of [T].
   * @throws ReflectionException when unable to create an instance.
   */
  fun newInstance(): T = ClassReflection.newInstance(javaClass)

  /**
   * Creates a new array instance with the given [size] with the [javaClass] as component type.
   * @param size [size] of the array to create. Must not be negative.
   * @return a new array of [T] instance with the given [size].
   * @throws ReflectionException when unable to create an array instance.
   */
  @Suppress("UNCHECKED_CAST")
  fun newArrayInstance(size: Int): Array<T> =
    try {
      ArrayReflection.newInstance(javaClass, size) as Array<T>
    } catch (exception: Exception) {
      throw ReflectionException("Unable to create an array of $javaClass with size $size.", exception)
    }

  /**
   * Finds a constructor given the selected [parameterTypes].
   * @param parameterTypes classes of the constructor parameters.
   * @return a [Constructor] matching the criteria.
   * @see Class.getConstructor
   * @throws ReflectionException if unable to find the constructor.
   */
  fun getConstructor(vararg parameterTypes: KClass<*>): Constructor = getConstructor(*parameterTypes.map { it.java }.toTypedArray())

  /**
   * Finds a constructor given the selected [parameterTypes].
   * @param parameterTypes classes of the constructor parameters.
   * @return a [Constructor] matching the criteria.
   * @see Class.getConstructor
   * @throws ReflectionException if unable to find the constructor.
   */
  fun getConstructor(vararg parameterTypes: Class<*>): Constructor = ClassReflection.getConstructor(javaClass, *parameterTypes)

  /**
   * Finds a declared constructor given the selected [parameterTypes].
   * @param parameterTypes classes of the constructor parameters.
   * @return a [Constructor] matching the criteria.
   * @see Class.getDeclaredConstructor
   * @throws ReflectionException if unable to find the constructor.
   */
  fun getDeclaredConstructor(vararg parameterTypes: KClass<*>): Constructor =
    getDeclaredConstructor(*parameterTypes.map { it.java }.toTypedArray())

  /**
   * Finds a declared constructor given the selected [parameterTypes].
   * @param parameterTypes classes of the constructor parameters.
   * @return a [Constructor] matching the criteria.
   * @see Class.getDeclaredConstructor
   * @throws ReflectionException if unable to find the constructor.
   */
  fun getDeclaredConstructor(vararg parameterTypes: Class<*>): Constructor =
    ClassReflection.getDeclaredConstructor(javaClass, *parameterTypes)

  /**
   * Finds a method with the given [name] consuming given [parameterTypes].
   * @param name name of the method.
   * @param parameterTypes types of the method parameters.
   * @return a [Method] matching the criteria.
   * @throws ReflectionException if unable to find the method.
   */
  fun getMethod(
    name: String,
    vararg parameterTypes: KClass<*>,
  ): Method = getMethod(name, *parameterTypes.map { it.java }.toTypedArray())

  /**
   * Finds a method with the given [name] consuming given [parameterTypes].
   * @param name name of the method.
   * @param parameterTypes types of the method parameters.
   * @return a [Method] matching the criteria.
   * @throws ReflectionException if unable to find the method.
   */
  fun getMethod(
    name: String,
    vararg parameterTypes: Class<*>,
  ): Method = ClassReflection.getMethod(javaClass, name, *parameterTypes)

  /**
   * Finds a declared method with the given [name] consuming given [parameterTypes].
   * @param name name of the method.
   * @param parameterTypes types of the method parameters.
   * @return a [Method] matching the criteria.
   * @throws ReflectionException if unable to find the method.
   */
  fun getDeclaredMethod(
    name: String,
    vararg parameterTypes: KClass<*>,
  ): Method = getDeclaredMethod(name, *parameterTypes.map { it.java }.toTypedArray())

  /**
   * Finds a declared method with the given [name] consuming given [parameterTypes].
   * @param name name of the method.
   * @param parameterTypes types of the method parameters.
   * @return a [Method] matching the criteria.
   * @throws ReflectionException if unable to find the method.
   */
  fun getDeclaredMethod(
    name: String,
    vararg parameterTypes: Class<*>,
  ): Method = ClassReflection.getDeclaredMethod(javaClass, name, *parameterTypes)

  /**
   * Finds a field with the given [name].
   * @param name name of the field to search for.
   * @return a [Field] matching the criteria.
   * @throws ReflectionException if unable to find the field.
   */
  fun getField(name: String): Field = ClassReflection.getField(javaClass, name)

  /**
   * Finds a declared field with the given [name].
   * @param name name of the field to search for.
   * @return a [Field] matching the criteria.
   * @throws ReflectionException if unable to find the field.
   */
  fun getDeclaredField(name: String): Field = ClassReflection.getField(javaClass, name)

  /**
   * Checks if [javaClass] is annotated with [T] annotation.
   * @return true if [javaClass] is annotated with [T].
   */
  inline fun <reified T : JavaAnnotation> isAnnotationPresent(): Boolean = isAnnotationPresent(T::class)

  /**
   * Checks if [javaClass] is annotated with [T] annotation.
   * @param annotationType class of the annotation to check.
   * @return true if [javaClass] is annotated with [T].
   */
  fun <T : JavaAnnotation> isAnnotationPresent(annotationType: KClass<T>): Boolean = isAnnotationPresent(annotationType.java)

  /**
   * Checks if [javaClass] is annotated with [T] annotation.
   * @param annotationType class of the annotation to check.
   * @return true if [javaClass] is annotated with [T].
   */
  fun <T : JavaAnnotation> isAnnotationPresent(annotationType: Class<T>): Boolean =
    ClassReflection.isAnnotationPresent(javaClass, annotationType)

  /**
   * Finds an [Annotation] of the selected [T] type.
   * @return an instance of [T] wrapped in [Annotation] or null if the [javaClass] is not annotated.
   */
  inline fun <reified T : JavaAnnotation> getAnnotation(): Annotation? = getAnnotation(T::class)

  /**
   * Finds an [Annotation] of the selected [T] type.
   * @param annotationClass type of the annotation to search for.
   * @return an instance of [T] wrapped in [Annotation] or null if the [javaClass] is not annotated.
   */
  fun <T : JavaAnnotation> getAnnotation(annotationClass: KClass<T>): Annotation? = getAnnotation(annotationClass.java)

  /**
   * Finds an [Annotation] of the selected [T] type.
   * @param annotationClass type of the annotation to search for.
   * @return an instance of [T] wrapped in [Annotation] or null if the [javaClass] is not annotated.
   */
  fun <T : JavaAnnotation> getAnnotation(annotationClass: Class<T>): Annotation? = ClassReflection.getAnnotation(javaClass, annotationClass)

  /**
   * Finds an [Annotation] of the selected [T] type.
   * @return an instance of [T] wrapped in [Annotation] or null if the [javaClass] is not annotated.
   */
  inline fun <reified T : JavaAnnotation> getDeclaredAnnotation(): Annotation? = getDeclaredAnnotation(T::class)

  /**
   * Finds an [Annotation] of the selected [T] type.
   * @param annotationClass type of the annotation to search for.
   * @return an instance of [T] wrapped in [Annotation] or null if the [javaClass] is not annotated.
   */
  fun <T : JavaAnnotation> getDeclaredAnnotation(annotationClass: KClass<T>): Annotation? = getDeclaredAnnotation(annotationClass.java)

  /**
   * Finds an [Annotation] of the selected [T] type.
   * @param annotationClass type of the annotation to search for.
   * @return an instance of [T] wrapped in [Annotation] or null if the [javaClass] is not annotated.
   */
  fun <T : JavaAnnotation> getDeclaredAnnotation(annotationClass: Class<T>): Annotation? =
    ClassReflection.getDeclaredAnnotation(javaClass, annotationClass)
}

/**
 * Wraps the selected [T] class with inlined [ReflectedClass] exposing libGDX reflection API.
 * @return [ReflectedClass] wrapping [T].
 */
@Reflection
inline fun <reified T : Any> reflect(): ReflectedClass<T> = ReflectedClass(T::class.java)

/**
 * Extracts [Class] from the given [KClass] and wraps it with inlined [ReflectedClass] exposing libGDX reflection API.
 * @param kotlinClass [KClass] representing [Class] to be wrapped.
 * @return [ReflectedClass] wrapping the selected [kotlinClass].
 */
@Reflection
fun <T : Any> reflect(kotlinClass: KClass<T>): ReflectedClass<T> = ReflectedClass(kotlinClass.java)

/**
 * Wraps the selected [Class] with inlined [ReflectedClass] exposing libGDX reflection API.
 * @param javaClass [Class] to be wrapped.
 * @return [ReflectedClass] wrapping the selected [javaClass].
 */
@Reflection
fun <T : Any> reflect(javaClass: Class<T>): ReflectedClass<T> = ReflectedClass(javaClass)

/**
 * Wraps the selected class with inlined [ReflectedClass] exposing libGDX reflection API.
 * @param name qualified name of the class to wrap.
 * @return [ReflectedClass] wrapping the selected class.
 * @throws ReflectionException when unable to find the class.
 */
@Reflection
fun reflect(name: String): ReflectedClass<*> = ReflectedClass(ClassReflection.forName(name))

/**
 * Gets the instance of a declared annotation.
 * @param T the exact type of the annotation.
 * @return an instance of the [T] annotation.
 * @throws ReflectionException if unable to get the instance due to a type mismatch.
 */
@Reflection
inline fun <reified T : JavaAnnotation> Annotation.get(): T =
  getAnnotation(T::class.java)
    ?: throw ReflectionException("Unable to get instance of ${T::class.java} from annotation with type $annotationType")
