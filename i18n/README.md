[![Maven Central](https://img.shields.io/maven-central/v/io.github.libktx/ktx-i18n.svg)](https://search.maven.org/artifact/io.github.libktx/ktx-i18n)

# KTX: internationalization utilities

`I18NBundle` utilities.

### Why?

As useful as `I18NBundle` is, it is often overlooked in pure Java applications due to sheer amount of work that it
requires when compared to the "lazy" approach of plain strings usage. This module provides a thin layer of syntax sugar
that makes it easier to work with `I18NBundle` instances and provide some useful code completion.

### Guide

#### `BundleLine`

Direct `I18NBundle` usage with strings as line IDs suffers from no code completion and compile-time validation. Sadly,
using simple string IDs as bundle lines might turn out to be not much better than using plain strings altogether. This
is why `BundleLine` interface was created: it allows you to turn enums into `I18NBundle` representations with minimal
effort.

For example, given this `nls.properties` file:

```properties
key1=Value.
key2=Value with {0} argument.
```

...you can create an enum similar to this:

```Kotlin
package ktx.i18n.example

import ktx.i18n.BundleLine

enum class Nls : BundleLine {
  key1,
  key2
}
```

This allows you to access bundle lines via enum instances:

```Kotlin
import ktx.i18n.get
import ktx.i18n.example.Nls

bundle[Nls.key1]
```

Listing - or generating - all expected bundle lines is basically all you have to do to enjoy less verbose and safer
syntax with code completion and compile-time validation. See usage examples below to explore `BundleLine` API.

Note that `BundleLine` assumes that `toString()` implementation returns a valid bundle line ID. If you want to change
the way `BundleLine` implementations extract lines from `I18NBundle`, override `toString()` method.

#### Automatic `BundleLine` enum generation

You can use the following Gradle Groovy script to generate a Kotlin enum implementing `BundleLine` according to an
existing `.properties` bundle:

```Groovy
task nls << {
  def project = 'core'             // Will contain generated enum class. 
  def source = 'src/main/kotlin'   // Kotlin source path of the project.
  def pack = 'com.your.company'    // Enum target package.
  def name = 'Nls'                 // Enum class name.
  def fileName = 'nls.kt'          // Name of Kotlin file containing the enum.
  def bundle = 'core/assets/i18n/nls.properties' // Path to i18n bundle file.

  println("Processing i18n bundle file at ${bundle}...")
  def builder = new StringBuilder()
  builder.append("""package ${pack}
import ktx.i18n.BundleLine
/** Generated from ${bundle} file. */
enum class ${name} : BundleLine {
""")
  def newLine = System.getProperty("line.separator")
  file(bundle).eachLine {
    def data = it.trim()
    def separator = data.indexOf('=')
    if (!data.isEmpty() && separator > 0 && !data.startsWith('#')) {
      def id = data.substring(0, separator)
      builder.append('    ').append(id).append(',').append(newLine)
    }
  }
  // If you want a custom enum body, replace the following append:
  builder.append('    ;').append(newLine).append('}').append(newLine)

  source = source.replace('/', File.separator)
  pack = pack.replace('.', File.separator)
  def path = project + File.separator + source + File.separator + pack +
      File.separator + fileName
  println("Saving i18n bundle enum at ${path}...")
  def enumFile = file(path)
  delete enumFile
  enumFile.getParentFile().mkdirs()
  enumFile.createNewFile()
  enumFile << builder << newLine
  println("Done. I18n bundle enum generated.")
}
```

The first few lines contain task configuration, so make sure to pass the correct paths and names before running the task
with `gradle nls` or `./gradlew nls`. Feel free to modify generated enum source to your needs. Be careful: current task
implementation **replaces** the enum class at the selected path.

#### Direct `I18NBundle` usage utilities

You can access any bundle line with `bundle["key"]` an `bundle["key", arguments]` syntax. These methods also accept
`BundleLine` instances, so if you would prefer not to assign static `I18NBundle` instance in an enum, you can still
benefit from type-safe i18n with a pleasant syntax: `bundle[key]`.

It is recommended to use `import ktx.i18n.*` import when working directly with `I18NBundle` instances.

### Usage examples

Examples below assume the following bundle `.properties` file content:

```properties
key=Value.
keyWithArguments=Accepts {0} arguments. {1}!
```

Using `I18NBundle` with strings as IDs:

```Kotlin
import ktx.i18n.*
import com.badlogic.gdx.utils.I18NBundle

val bundle = I18NBundle.createBundle(file)
bundle["key"] // Value.
bundle["keyWithArguments", 2, "Hello"] // Accepts 2 arguments. Hello!
```

Using `I18NBundle` with `BundleLine` enum instances as IDs:

```Kotlin
package example

import example.Nls.*
import ktx.i18n.*
import com.badlogic.gdx.utils.I18NBundle

enum class Nls : BundleLine {
  key,
  keyWithArguments
}

// Usage:
val bundle = I18NBundle.createBundle(file)
bundle[key] // Value.
bundle[keyWithArguments, 2, "Hello"] // Accepts 2 arguments. Hello!
```

Using `BundleLine` enum with assigned `I18NBundle` instance:

```Kotlin
package example

import example.Nls.*
import ktx.i18n.*
import com.badlogic.gdx.utils.I18NBundle

enum class Nls : BundleLine {
  key,
  keyWithArguments;

  override val bundle: I18NBundle
    get() = i18nBundle

  companion object {
    lateinit var i18nBundle: I18NBundle
  }
}

// Assigning I18NBundle instance:
Nls.i18nBundle = I18NBundle.createBundle(FileHandle(File("i18n/gradle")))

// Reading lines from the bundle:
key.nls // Value.
keyWithArguments.nls(2, "Hello") // Accepts 2 arguments. Hello!

// More concise, but somewhat less obvious syntax:
key() // Value.
keyWithArguments(2, "Hello") // Accepts 2 arguments. Hello!
```

### Alternatives

- [LibGDX Markup Language](https://github.com/czyzby/gdx-lml/tree/master/lml) features simple and powerful support for
internationalization of `Scene2D` widgets (internally using LibGDX `I18NBundle` API). However, it requires you to create
views with HTML-like syntax rather than with Java (or Kotlin) code.

#### Additional documentation

- [`I18NBundle` article.](https://github.com/libgdx/libgdx/wiki/Internationalization-and-Localization)
