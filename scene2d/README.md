# KTX: `Scene2D` type-safe builders

Utilities for creating `Scene2D` widgets using Kotlin type-safe builders.

### Why?

Building Scene2D GUI in Java usually leads to overly verbose, complicated code. It is hard to reason about how it will
actually look on the screen. As opposed to HTML, XML or other easily readable markup languages, Java rarely forms
visually appealing GUI templates. Kotlin type-safe builders DSL is a great compromise between the two: readability meets
flexibility and the expressiveness of a programming language.

### Guide

`ktx-scene2d` provides a number of factory and extension methods that slightly modify the original `Scene2D` API,
allowing you to use Kotlin type-safe builder DSL. Methods were designed to match the original API as closely as possible:
all methods' names match lower camel case names of widget classes and contain similar parameters to widgets' constructors.
For example, `label` is the name of `com.badlogic.gdx.scenes.scene2d.ui.Label` factory methods, which consume
`CharSequence` (text of the label) and optional `Skin` and `LabelStyle` name. If you are familiar with `Scene2D`,
`ktx-scene2d` should feel like natural.

Supported actors include:

- *Root* actors. These actors can have children and are commonly added directly to the `Stage` without any problems.
These include `buttonTable`, `container`, `dialog`, `horizontalGroup`, `scrollPane`, `splitPane`, `stack`, `table`,
`tree`, `verticalGroup` and `window`. There are factory methods for root actors that are both directly added to stages,
as well as in form of children of other actors.
- Other *parental* actors. These actors can have children (mostly due their class hierarchy), but are usually specialized
and rarely added directly to stages. These include `button`, `checkBox`, `imageButton`, `imageTextButton` and `textButton`.
There are factory methods of other parental actors only in form of children of other actors.
- *Child* actors. These actors cannot have children at all. `image`, `label`, `list`, `progressBar`, `selectBox`,
`slider`, `textArea`, `textField` and `touchpad` are considered child actors.

You usually start with a *root* actor and - using its factory methods - fill it with children:

```Kotlin
import ktx.scene2d.*

table {
  label("Hello world!")
}
```

The example above would create a `Table` instance with a single `Label` child. All factory methods return instances of
the created actors. If you need a direct reference to an actor, you can always assign the result of the factory methods:

```Kotlin
import ktx.scene2d.*

val myRoot = table {
  val myLabel = label("Hello world!")
  // myLabel is Label -> true
}
// myRoot is Table -> true
```

Obviously, actors can be nested: as weird as it might sound, children of your children can have children:

```Kotlin
import ktx.scene2d.*

table {
  button {
    label("Click me.")
  }
}
```

This snippet would create a `Table` instance with a single `Button` child, which is a parent of a `Label`. There is no
practical nesting limit: as long as you do not manually set up circular references (actors being parents of themselves),
your widget hierarchies should *just work*.

When it comes to widget customization, only parameters crucial to construct the actors are present in factory methods.
For example, `Label` requires some text that it can draw, so it has a `CharSequence` (`String` or `StringBuilder`)
parameter. However, most other settings must be changed manually inside widgets' blocks by invoking their methods:

```Kotlin
import ktx.scene2d.*
import com.badlogic.gdx.graphics.Color

table {
  pad(4f) // Setting table padding.
  label("Test.") {
    color = Color.RED // Setting text's color.
    setWrap(true) // Setting label's text wrapping.
  }
}
```

You basically have full access to the `Scene2D` widgets API - type-safe Kotlin builders is just syntax sugar. Some
properties are Kotlin-compatible (`color = Color.RED`), most are not (`setWrap(true)`), but it should be pretty
straightforward to configure most actors.

What is best about it: all the lambdas are inlined during compile time, which means there is little to no runtime
overhead when using `ktx-scene2d`. This code is as fast as your good old Java.

#### Working with `Skin`

`Skin` instances store styles of the widgets and other required assets - like fonts and drawables.

Additionally to constructor parameters, most factory methods also include `skin` and `style` name parameters that default
to `Scene2DSkin.defaultSkin` and `"default"` respectively. To globally set the `Skin` instance that will be used by
default to create your widgets, modify `defaultSkin` field of `Scene2DSkin` container:

```Kotlin
val mySkin: Skin = /* TODO Create your Skin */
Scene2DSkin.defaultSkin = mySkin
```

`Scene2DSkin` class includes support for listeners that will be invoked each time you reload your skin. For example, for
can introduce different GUI skins in your application and add a listener that recreates the GUI each time the theme is
changed:

```Kotlin

Scene2DSkin.addListener {
  // Invoked each time defaultSkin is changed.
  myViewsManager.reloadViews()
}

Scene2DSkin.defaultSkin = someOtherTheme // Invokes the listener.
```

Of course, you can easily access skin resources. `style` name parameter allows you to choose the look of your widgets
based on the style stored in the chosen skin. For example, to create the `Label` defined below, constructor would
extract `LabelStyle` linked to `"custom"` name in `myCustomSkin`:

```Kotlin
table {
  label(text = "Custom style!", skin = myCustomSkin, style = "custom")
}
```

#### `KWidgets`

To avoid method collisions and ease the implementation of type-safe builders, most so-called "parental" widgets (extending
`Group` class) were extended with `K` prefix. `Table`-extending widgets in `ktx-scene2d` now implement the `KTable`
interface, which gives access to inlined builder methods that allows to customize the added widgets, as well as their
`Cell` instances. Regular `WidgetGroup`-extending widgets now implement `KGroup` interface, which allows to build their
children with type-safe builder syntax.

The only exceptions from the naming scheme are the `KTableWidget` and `KTreeWidget`, which extend `Table` and `Tree`
respectively.

You generally do not have to worry about the `KWidgets`: you can still reference these by their original extended class.
This is basically just an internal representation: we choose not to create entirely new class hierarchy for the builders
(as opposed to simply extending existing `Scene2D` classes) as it would basically require to rewrite the entirety of
`Scene2D` module.

### Usage examples

TODO.

### Known issues

Because of how the scopes work, children building blocks have full access to all parent methods. For example, this code
would compile:

```Kotlin
table {
  label("") {
    label("") {
      pad(4f)
    }
  }
}
```

As weird as it might seem, the snippet above would be an equivalent to:

```Kotlin
table {
  label("")
  label("")
  pad(4f) // Pad is actually applied to the Table instance.
}
```

We obviously cannot change Kotlin's scoping rules, but these kind of "formatting errors" can be easily avoided if you
are ready to pay the cost of verbosity - by proceeding all calls with `this.`, you can no longer invoke parents' methods
in nested children blocks:

```Kotlin
table {
  this.label("") {
    this.label("") { // Compile error: Label has no 'label' method.
      this.pad(4f) // Compile error: Label has no 'pad' method.
    }
  }
}
```

We would suggest to go with the more readable `this`-less syntax during prototyping to speed things up, but eventually
refactor critical code to use `this` for safety.

### Alternatives

- Creating layouts with [Scene2D](https://github.com/libgdx/libgdx/wiki/Scene2d) directly in Kotlin or Java.
- [VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI) greatly extends
[Scene2D](https://github.com/libgdx/libgdx/wiki/Scene2d.ui) API, but lacks first-class Kotlin support.
- [LibGDX Markup Language](https://github.com/czyzby/gdx-lml/tree/master/lml) allows to build `Scene2D` views using
HTML-like syntax. It also features a [VisUI extension](https://github.com/czyzby/gdx-lml/tree/master/lml-vis). However,
it lacks first-class Kotlin support and the flexibility of a powerful programming language.
- [ktx-vis](../vis) features type-safe builders of [VisUI](https://github.com/kotcrab/vis-editor/wiki/VisUI) widgets.

#### Additional documentation

- [Scene2D article.](https://github.com/libgdx/libgdx/wiki/Scene2d)
- [Scene2D UI article.](https://github.com/libgdx/libgdx/wiki/Scene2d.ui)
- [`Table` article.](https://github.com/libgdx/libgdx/wiki/Table)
- [`Skin` article.](https://github.com/libgdx/libgdx/wiki/Skin)
