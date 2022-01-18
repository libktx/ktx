[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-reflect.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-reflect)

# KTX: Reflection utilities

Reflection utilities for Kotlin applications.

### Why?

libGDX provides its own cross-platform reflection API via static `ClassReflection` and `ArrayReflection`.
While standard Java and Kotlin reflection APIs work on most platforms, libGDX API ensures compatibility
with backends such as HTML/WebGL and other third-party backends. However, with their frequent `Class`
parameter usage, the provided static methods are inconvenient to use from within Kotlin. Additionally, due to
Kotlin's unchecked exceptions, libGDX by itself does not warn the users about reflection API usage, which can
cause various runtime issues.

### Guide

Instead of relying on static methods, `ktx-reflect` provides an inlined `ReflectedClass` wrapper that
allows to invoke `ClassReflection` and `ArrayReflection` methods with improved API. Since `ReflectedClass`
is an inlined value class, the overhead is very similar to using libGDX reflection APIs directly.

`ReflectedClass` can be created either by using one of its constructors with `Class` or `KClass` parameters,
or by `reflect<Type>` and `reflect(String)` top-level functions. 

`ReflectedClass` exposes the following `ClassReflection` methods as read-only properties:
- `simpleName`
- `isMemberClass`
- `isStatic`
- `isArray`
- `isPrimitive`
- `isEnum`
- `isAnnotation`
- `isInterface`
- `isAbstract`
- `componentType`
- `constructors`
- `methods`
- `declaredMethods`
- `fields`
- `declaredFields`
- `annotations`
- `declaredAnnotations`
- `interfaces`
- `enumConstants`

`ReflectedClass` exposes the following `ClassReflection` methods with improved API including reified types and
`KClass` parameters:
- `isInstance`
- `isAssignableFrom`
- `newInstance`
- `getConstructor`
- `getDeclaredConstructor`
- `getMethod`
- `getDeclaredMethod`
- `getField`
- `getDeclaredField`
- `isAnnotationPresent`
- `getAnnotation`
- `getDeclaredAnnotation`

Additional `ReflectedClass` utilities include:
- `constructor` property allows to extract the only class constructor if it is available.
- `newArrayInstance` method allows to create a type array of the given size using `ArrayReflection`.

`ReflectedClass` factory methods:
- `reflect<Type>()` top-level function allows to wrap a selected class with a `ReflectedClass`.
- `reflect(KClass<Type>)` top-level function allows to wrap a selected Kotlin class with a `ReflectedClass`.
- `reflect(Class<Type>)` top-level function allows to wrap a selected Java class with a `ReflectedClass`.
- `reflect(String)` top-level function allows to find a selected class by qualified name and wrap it with
a `ReflectedClass`.

Other utilities include:
- `Reflection` annotation, which requires opt-in, allows marking functionalities that rely on reflection.
- `Annotation.get<Type>` extension method with a reified type allowing to get an instance of the annotation.

### Usage examples

Obtaining a class wrapper:

```kotlin
import ktx.reflect.ReflectedClass
import ktx.reflect.Reflection
import ktx.reflect.reflect

class MyClass

@OptIn(Reflection::class)
fun getWrappedClass(): ReflectedClass<MyClass> {
  return reflect<MyClass>()
}
```

Obtaining a class wrapper for a Java `Class`:

```kotlin
import ktx.reflect.ReflectedClass
import ktx.reflect.Reflection
import ktx.reflect.reflect

class MyClass

@OptIn(Reflection::class)
fun getWrappedClass(): ReflectedClass<MyClass> {
  return reflect(MyClass::class.java) // or ReflectedClass(MyClass::class.java)
}
```

Obtaining a class wrapper by name:

```kotlin
import ktx.reflect.ReflectedClass
import ktx.reflect.Reflection
import ktx.reflect.reflect

class MyClass

@OptIn(Reflection::class)
fun getWrappedClass(): ReflectedClass<*> {
  return reflect("my.package.MyClass")
}
```

Checking class properties:

```kotlin
import ktx.reflect.Reflection
import ktx.reflect.reflect

class MyClass

@OptIn(Reflection::class)
fun getClassProperties() {
  val myClass = reflect<MyClass>()
  myClass.isAbstract
  myClass.isStatic
  myClass.simpleName
  myClass.methods
  myClass.fields
  myClass.annotations
}
```

Creating a new instance of class with a no-argument constructor with reflection:

```kotlin
import ktx.reflect.Reflection
import ktx.reflect.reflect

class MyClass

@OptIn(Reflection::class)
fun createNewInstance(): MyClass {
  return reflect<MyClass>().newInstance()
}
```

Creating a new instance of class with a specific constructor selected by parameter types:

```kotlin
import ktx.reflect.Reflection
import ktx.reflect.reflect

class MyClass(val a: String)

@OptIn(Reflection::class)
fun createNewInstance(): MyClass {
  // Note that a class cast is necessary, as libGDX wrappers strip types:
  return reflect<MyClass>().getConstructor(String::class).newInstance("a") as MyClass
}
```

Calling a method via reflection:

```kotlin
import ktx.reflect.Reflection
import ktx.reflect.reflect

class MyClass {
  fun myMethod(parameter: String): String = parameter.uppercase()
}

@OptIn(Reflection::class)
fun invokeMethod(instance: MyClass): String {
  val method = reflect<MyClass>().getDeclaredMethod("myMethod", String::class)
  return method.invoke(instance, "Argument") as String
}
```

Getting an annotation instance from a class:

```kotlin
import ktx.reflect.Reflection
import ktx.reflect.get
import ktx.reflect.reflect

annotation class MyAnnotation(val value: String)
@MyAnnotation("Hello!")
class MyClass

@OptIn(Reflection::class)
fun getAnnotation(): MyAnnotation? {
  // The `get()` call unwraps the annotation from the libGDX wrapper.
  return reflect<MyClass>().getAnnotation<MyAnnotation>()?.get()
}
```

Propagating the reflection usage warning via the `@Reflection` annotation:

```kotlin
import ktx.reflect.Reflection
import ktx.reflect.reflect

interface MyInterface

/** This method uses reflection and requires opt-in! */
@Reflection
fun isAbstract(): Boolean {
  // Note that inside @Reflection-annotated methods and classes
  // you can freely use reflection API:
  return reflect<MyInterface>().isAbstract
  // However, any other code using this function would either
  // need opt-in or use the same annotation.
}
```

Refer to the libGDX documentation for more reflection usage examples.

### Alternatives

If cross-platform compatibility is not a concern, Kotlin and Java reflection APIs can be used directly.

#### Additional documentation

- [Official reflection article.](https://libgdx.com/wiki/utils/reflection)
