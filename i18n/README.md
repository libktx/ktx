# KTX: internationalization utilities

This tiny module is a thin wrapper over LibGDX `I18NBundle` with some "global" functions that ease the use of its API.

### Why?

As useful as `I18NBundle` is, it is often overlooked in pure Java applications due to sheer amount of work that it
requires comparing to plain strings usage. Passing `I18NBundle` instance around is tedious, and i18n reloading might be
complex enough to implement to require an application restart.

### Guide

#### Setting the default bundle

To start using `ktx-i18n`, you have to load `I18NBundle` instance and set it as default one in `I18n` singleton container
class. While a semi-global `I18NBundle` field might seem ugly (and it kind of *is*), it allows to access localized texts
throughout the application without the usual boilerplate thanks to utility functions and the fact that you no longer have
to worry how to pass `I18NBundle` into *X*. Setting the default bundle comes down to `I18n.defaultBundle = yourBundle`,
although you can also let the `I18n` handle loading for you and use `I18n.load("path/to/bundle", Locale.ENGLISH)`.

#### Basic usage

There are two basic functions in the `ktx-i18n` module that you'll most likely use throughout the application: `nls` with
formatting arguments and one without. Once the default bundle is set, getting a localized text is as easy as `nls("line")`
call. If you want to pass additional arguments, a similar syntax is used: `nls("line", someArgument, 1, "arg")`. Note that
both methods consume a `I18NBundle` instance that defaults to `I18n.defaultBundle`. While you can use these methods like
this: `nls("line", myBundle)`, this might obviously turn out to be more verbose than using your bundle directly.

#### Code completion

As you probably noticed, there is no code completion and compile-time validation of the bundle line IDs. Sadly, using
simple string IDs might turn out to be not much better than using plain strings altogether. That is why `BundleLine`
utility interface was created: it provides some default methods that allow you to *invoke* instances of the implementing
class as you would invoke any method. `BundleLine` assumes that `toString()` implementation returns a valid bundle line
ID and default bundle is set (although you can override it to use any `I18NBundle` instance if you really don't like
global variables).

For example, given this `nls.properties` file:

```
key1=Value.
key2=Value with {0} argument.
```

...you would usually load it as the default bundle and create an enum similar to this:

```Kotlin
package ktx.i18n.example
import ktx.i18n.BundleLine
enum class Nls : BundleLine {
  key1,
  key2
}
```

Listing all expected bundle lines (without making typos) is basically all you have to do to get an even less verbose
syntax with code completion and compile-time validation. To use this bundle, now you just have to call `Nls.key1()` or
`Nls.key2(someArgument, "text", 3)` if it has any arguments. You can use an import like `import your.package.Nls.*` to
make it possible to omit the `Nls.` part completely and use this absurdly simple syntax: `key1()`, `key2(argument)`.

#### Bundle reloading

Usually if you decide to use the i18n module in the first place, you plan on supporting multiple languages in your
application. You can attach a listener to the `I18n.defaultBundle` field - each time it is reassigned, all attached
listeners are invoked with the new `I18NBundle` instance. Attaching a listener that reloads the GUI widgets (for example)
is a good idea. The syntax is pretty simple: `I18n.addListener { myView.reload() }`. You can also remove any listener
with `removeListener` or `clearListeners` methods.


#### Automatic `BundleLine` enum generation

You can use the following Gradle script to generate a Kotlin enum implementing `BundleLine` according to an existing
`.properties` bundle:

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
with `gradle nls` or `./gradlew nls`. Be careful: current task implementation **replaces** the enum class at the selected
path. If you want to modify enum source, you should edit the task itself.

### Alternatives

- [LibGDX Markup Language](https://github.com/czyzby/gdx-lml/tree/master/lml) features simple and powerful support for
internationalization of `Scene2D` widgets (internally using LibGDX `I18NBundle` API). However, it requires you to create
views with HTML-like syntax rather than with Java (or Kotlin) code.
