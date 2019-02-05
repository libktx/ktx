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

Some *root* actors do not usually make sense as other actors' children: a good example might be the `Window`, which was
clearly designed to be added directly to the `Stage`. That is why there are no factory methods for `Window` and `Dialog`
classes as children of other groups simply because this is a very specific and rare use case. Other widgets should be
available in one form or another.

You usually start with a single *root* actor and - using its factory methods - fill it with children:

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

Thanks to `@DslMarker` Kotlin API, you cannot implicitly access parents from within children blocks, which resolves most
scoping issues. For example, `Label` is not a `Group` and it cannot have children, so this code would not compile:

```Kotlin
import ktx.scene2d.*

table {
  label("Not a real parent.") {
    label("Invalid.") // Does not compile: outside of table scope.
  }
}
```

When it comes to widget customization, only data crucial to construct the actors is present in factory methods
as parameters. For example, `Label` requires some text that it can draw, so it has a `CharSequence` (`String` or
`StringBuilder`) parameter. However, most other settings must be changed manually inside widgets' blocks by invoking
their methods - even if they are as common as `Color`:

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

This was a design choice - instead of duplicating the whole `Scene2D` widgets API in method parameters, we
decided to simply expose the widgets themselves instance during building.

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
val mySkin: Skin = TODO("Create your skin.")
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

The only exceptions from the naming scheme are the `KTableWidget`, `KTreeWidget` and `KListWidget`, which extend `Table`,
`Tree` and `List` respectively. Also, `KListWidget` factory methods are named `listWidget` and `listWidgetOf` instead
of `list` or `listOf` to avoid collisions with Kotlin standard library.

You generally do not have to worry about the `KWidgets`: you can still reference these by their original extended class.
This is basically just an internal representation: we choose not to create entirely new class hierarchy for the builders
(as opposed to simply extending existing `Scene2D` classes) as it would basically require to rewrite the entirety of
`Scene2D` module.

#### `Stage`

`Stage` is a crucial part of the `Scene2D` API. It contains the root actor that is the parent of all of the displayed
widgets. It handles input processing, rendering and resizing. If you are unfamiliar with `Stage` API, try reading one
of the resources listed at the bottom of the page.

Initiating `Stage` instances:

```kotlin
// Default settings:
val stage = Stage()

// Custom viewport:
val stage = Stage(ExtendViewport(800, 600))

// Custom viewport and batch:
val stage = Stage(ExtendViewport(800, 600), SpriteBatch())
```

Using [`ktx-actors`](../actors):

```kotlin
import ktx.actors.stage

val stage = stage(batch = SpriteBatch())
```

If you are already using a `SpriteBatch` to render your textures, reusing the same instance for `Stage` is recommended.

Make sure to set the `Stage` as the input processor before it is rendered:

```kotlin
Gdx.input.inputProcessor = stage
```

Note that [`ktx-app` module](../app) makes it easier to work with input processors and multiple application screens,
while [`ktx-actors` module](../actors) has general `Scene2D` utilities including `Stage` factory method.

### Usage examples

Loading and setting of the default application's `Skin`:
```Kotlin
import ktx.scene2d.*
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Skin

Scene2DSkin.defaultSkin = Skin(Gdx.files.internal("ui.json"))
// Note: ktx-assets module might help with skin loading and files management.
```

Creating a `Table` with a `Label` child:
```Kotlin
import ktx.scene2d.*

table {
  label("Hello world!")
}
```
![Table](img/00.png)

Creating a `Table` with customized background image and a `Label` with custom color:
```Kotlin
import ktx.scene2d.*
import com.badlogic.gdx.graphics.Color

table {
  background("button")
  label("Hello world!") {
    color = Color.CORAL
  }
}
```
![Background](img/01.png)

Manipulating `Cell` properties of `Window` and its children:
```Kotlin
import ktx.scene2d.*
import com.badlogic.gdx.graphics.Color

window(title = "Hello world!") {
  button { cell ->
    // Changing button properties - button is "this":
    color = Color.CORAL
    // Changing cell properties:
    cell.fillX().row()
  }
  table {
    // Changing nested table properties:
    defaults().pad(2f)
    // Adding table children:
    label("Nested")
    label("table")
    label("of")
    label("labels.")
    // Cell of the nested actor is also available through "it":
    it.spaceBottom(10f).row()
  }
  textButton(text = "Click me!")
  // Packing the root window:
  pack()
}
```
![Window](img/02.png)

Accessing `Cell` instances of`Table` children outside of building blocks:
```Kotlin
import ktx.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label

table {
  val label: Label = label("Cell properties modified.").cell(expand = true)
  val cell: Cell<Label> = label("Wrapped in cell.").inCell
  val combined: Cell<Label> = label("Modified and wrapped.").cell(expand = true).inCell
  val afterBuildingBlock: Cell<Label> = label("Not limited to no init block actors.") {
    setWrap(true)
  }.cell(expand = true).inCell

  // Cells are available only for direct children of tables (or its extensions). 
  stack {
    // These would not compile:
    label("Invalid.").cell(expand = true)
    label("Invalid").inCell
  }
}
```

Creating `Tree` of `Label` instances:
```Kotlin
import ktx.scene2d.*

tree {
  label("Node")
  label("Node")
  label("Nest") { node ->
    node {
      label("Nested") { node ->
        node.label("Nested")
      }
      label("Nested")
    }
  }
  label("Node")
}
```
![Tree](img/03.png)

Accessing `Node` instances of `Tree` children outside of building blocks (note that `KNode` is **KTX** custom wrapper
of LibGDX `Tree.Node` with additional building API support):
```Kotlin
import ktx.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node

tree {
  val label: Label = label("Node properties modified.").node(expanded = true)
  val cell: Node = label("Wrapped in node.").inNode
  val combined: Node = label("Modified and wrapped.").node(expanded = true).inNode
  val afterBuildingBlock: KNode = label("Not limited to no init block actors.") {
    setWrap(true)
  }.node(expanded = true).inNode
  // Nodes are available only for children of trees.
}
```

Creating `List` and `SelectBox` widgets storing strings:
```Kotlin
import ktx.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Cell

table {
  // List and SelectBox generics represent the type of items that
  // they store and type of their parent actor container (like Cell).
  listWidget<String, Cell<*>> {
    -"First."
    -"Second"
    -"Third."
  }
  selectBox<String, Cell<*>> {
    -"First"
    -"Second"
    -"Third"
  }
}
```
![List](img/04.png)

#### Synergy

Pair this library with [`ktx-style`](../style) for type-safe styles building and [`ktx-actors`](../actors) for useful
extension methods for `Scene2D` API. [`ktx-assets`](../assets) might help with `Skin` management.

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
